import { useState, useEffect } from "react";
import { AppError } from "../../errors/AppError";
import { FieldErrorMessage } from "../ui/FieldErrorMessage";
import { FormError } from "../ui/FormError";
import type { CategoryResponse } from "../../types/category.types";
import type { PaymentModeResponse } from "../../types/paymentMode.types";
import type {
  RecurringTransactionResponse,
  RecurringFrequency,
  CreateRecurringTransactionRequest,
  UpdateRecurringTransactionRequest,
} from "../../types/recurring.types";
import type { TxnType } from "../../types/transaction.types";

// ── Props ─────────────────────────────────────────────────────────────────────

type CreateProps = {
  mode: "create";
  onSubmit: (data: CreateRecurringTransactionRequest) => void;
  initialData?: never;
};

type EditProps = {
  mode: "edit";
  onSubmit: (data: UpdateRecurringTransactionRequest) => void;
  initialData: RecurringTransactionResponse;
};

type BaseProps = {
  open: boolean;
  onClose: () => void;
  isPending: boolean;
  submitError: AppError | null;
  categories: CategoryResponse[];
  paymentModes: PaymentModeResponse[];
};

type Props = BaseProps & (CreateProps | EditProps);

// ── Form state ────────────────────────────────────────────────────────────────

interface FormState {
  title: string;
  amount: string;
  type: TxnType;
  frequency: RecurringFrequency;
  startDate: string;
  endDate: string;
  note: string;
  categoryId: string;
  paymentModeId: string;
}

const FREQUENCIES: RecurringFrequency[] = [
  "DAILY",
  "WEEKLY",
  "MONTHLY",
  "YEARLY",
];

const FREQ_LABELS: Record<RecurringFrequency, string> = {
  DAILY: "Daily",
  WEEKLY: "Weekly",
  MONTHLY: "Monthly",
  YEARLY: "Yearly",
};

const today = () => new Date().toISOString().split("T")[0];

const EMPTY: FormState = {
  title: "",
  amount: "",
  type: "EXPENSE",
  frequency: "MONTHLY",
  startDate: today(),
  endDate: "",
  note: "",
  categoryId: "",
  paymentModeId: "",
};

// ── Component ─────────────────────────────────────────────────────────────────

const RecurringForm: React.FC<Props> = (props) => {
  const { open, onClose, isPending, submitError, categories, paymentModes } =
    props;

  const [form, setForm] = useState<FormState>(EMPTY);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  // Reset / pre-fill on open
  useEffect(() => {
    if (!open) return;
    if (props.mode === "edit" && props.initialData) {
      const d = props.initialData;
      setForm({
        title: d.title,
        amount: String(d.amount),
        type: d.type,
        frequency: d.frequency,
        startDate: d.startDate,
        endDate: d.endDate ?? "",
        note: d.note ?? "",
        // categoryId / paymentModeId not in response — keep empty for edit
        categoryId: "",
        paymentModeId: "",
      });
    } else {
      setForm(EMPTY);
    }
    setFieldErrors({});
  }, [open]);

  // Surface server validation
  useEffect(() => {
    if (submitError?.isValidation) {
      setFieldErrors(submitError.toFieldErrorMap());
    }
  }, [submitError]);

  const filteredCategories = categories.filter(
    (c) => c.categoryType === form.type,
  );

  const set = (key: keyof FormState, value: string) => {
    setForm((prev) => {
      const next = { ...prev, [key]: value };
      if (key === "type") next.categoryId = "";
      return next;
    });
    if (fieldErrors[key]) setFieldErrors((p) => ({ ...p, [key]: "" }));
  };

  const validate = (): boolean => {
    const errors: Record<string, string> = {};

    if (!form.title.trim()) errors.title = "Title is required";

    if (!form.amount || isNaN(Number(form.amount)) || Number(form.amount) <= 0)
      errors.amount = "Enter a valid amount greater than 0";

    if (!form.startDate) errors.startDate = "Start date is required";

    if (props.mode === "create") {
      if (!form.categoryId) errors.categoryId = "Select a category";
      if (!form.paymentModeId) errors.paymentModeId = "Select a payment mode";
    }

    if (form.endDate && form.endDate <= form.startDate)
      errors.endDate = "End date must be after start date";

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!validate()) return;

    if (props.mode === "create") {
      props.onSubmit({
        title: form.title.trim(),
        amount: Number(form.amount),
        type: form.type,
        frequency: form.frequency,
        startDate: form.startDate,
        endDate: form.endDate || undefined,
        note: form.note.trim() || undefined,
        categoryId: form.categoryId,
        paymentModeId: form.paymentModeId,
      });
    } else {
      // Only send changed fields for PATCH
      const payload: UpdateRecurringTransactionRequest = {};
      const d = props.initialData;
      if (form.title.trim() !== d.title) payload.title = form.title.trim();
      if (Number(form.amount) !== d.amount)
        payload.amount = Number(form.amount);
      if (form.frequency !== d.frequency) payload.frequency = form.frequency;
      if (form.note.trim() !== (d.note ?? ""))
        payload.note = form.note.trim() || undefined;
      if (form.endDate !== (d.endDate ?? ""))
        payload.endDate = form.endDate || undefined;
      if (form.categoryId) payload.categoryId = form.categoryId;
      if (form.paymentModeId) payload.paymentModeId = form.paymentModeId;
      props.onSubmit(payload);
    }
  };

  if (!open) return null;

  const isCreate = props.mode === "create";

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/30 z-30" onClick={onClose} />

      {/* Panel */}
      <div className="fixed inset-y-0 right-0 w-full max-w-md bg-white shadow-xl z-40 flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <div>
            <h2 className="text-base font-semibold text-gray-900">
              {isCreate
                ? "New Recurring Transaction"
                : "Edit Recurring Transaction"}
            </h2>
            <p className="text-xs text-gray-400 mt-0.5">
              {isCreate
                ? "Set up an automatic recurring rule"
                : "Update this recurring rule"}
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-700 transition-colors text-xl leading-none"
          >
            ✕
          </button>
        </div>

        {/* Form body */}
        <form
          onSubmit={handleSubmit}
          className="flex-1 overflow-y-auto px-6 py-5 space-y-5"
        >
          {submitError && !submitError.isValidation && (
            <FormError error={submitError.message} />
          )}

          {/* Title */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">Title</label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => set("title", e.target.value)}
              placeholder="e.g. Wifi Bill"
              autoFocus
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={fieldErrors.title} />
          </div>

          {/* Type toggle — only shown on create */}
          {isCreate && (
            <div>
              <label className="block text-sm text-gray-600 mb-2">Type</label>
              <div className="flex rounded-lg border border-gray-200 overflow-hidden">
                {(["EXPENSE", "INCOME"] as TxnType[]).map((t) => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => set("type", t)}
                    className={`flex-1 py-2 text-sm font-medium transition-colors ${
                      form.type === t
                        ? t === "INCOME"
                          ? "bg-green-600 text-white"
                          : "bg-red-500 text-white"
                        : "bg-white text-gray-500 hover:bg-gray-50"
                    }`}
                  >
                    {t === "INCOME" ? "↑ Income" : "↓ Expense"}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Amount */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Amount (₹)
            </label>
            <input
              type="number"
              min="0.01"
              step="0.01"
              value={form.amount}
              onChange={(e) => set("amount", e.target.value)}
              placeholder="0.00"
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={fieldErrors.amount} />
          </div>

          {/* Frequency */}
          <div>
            <label className="block text-sm text-gray-600 mb-2">
              Frequency
            </label>
            <div className="grid grid-cols-4 gap-2">
              {FREQUENCIES.map((f) => (
                <button
                  key={f}
                  type="button"
                  onClick={() => set("frequency", f)}
                  className={`py-2 text-xs font-medium rounded-lg border transition-colors ${
                    form.frequency === f
                      ? "bg-gray-900 text-white border-gray-900"
                      : "bg-white text-gray-500 border-gray-200 hover:border-gray-400"
                  }`}
                >
                  {FREQ_LABELS[f]}
                </button>
              ))}
            </div>
          </div>

          {/* Start date */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Start Date
            </label>
            <input
              type="date"
              value={form.startDate}
              onChange={(e) => set("startDate", e.target.value)}
              disabled={!isCreate}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black disabled:bg-gray-50 disabled:text-gray-400"
            />
            <FieldErrorMessage message={fieldErrors.startDate} />
          </div>

          {/* End date */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              End Date{" "}
              <span className="text-gray-400 font-normal">(optional)</span>
            </label>
            <input
              type="date"
              value={form.endDate}
              min={form.startDate || today()}
              onChange={(e) => set("endDate", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={fieldErrors.endDate} />
          </div>

          {/* Category — always shown, required on create */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Category{" "}
              {!isCreate && (
                <span className="text-gray-400 font-normal">
                  (optional — leave blank to keep current)
                </span>
              )}
            </label>
            <select
              value={form.categoryId}
              onChange={(e) => set("categoryId", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black bg-white"
            >
              <option value="">
                {isCreate ? "Select category" : "Keep current category"}
              </option>
              {filteredCategories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
            {isCreate && filteredCategories.length === 0 && (
              <p className="text-xs text-amber-600 mt-1">
                No {form.type.toLowerCase()} categories found. Create one first.
              </p>
            )}
            <FieldErrorMessage message={fieldErrors.categoryId} />
          </div>

          {/* Payment mode */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Payment Mode{" "}
              {!isCreate && (
                <span className="text-gray-400 font-normal">(optional)</span>
              )}
            </label>
            <select
              value={form.paymentModeId}
              onChange={(e) => set("paymentModeId", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black bg-white"
            >
              <option value="">
                {isCreate ? "Select payment mode" : "Keep current payment mode"}
              </option>
              {paymentModes.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name}
                </option>
              ))}
            </select>
            <FieldErrorMessage message={fieldErrors.paymentModeId} />
          </div>

          {/* Note */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Note <span className="text-gray-400 font-normal">(optional)</span>
            </label>
            <textarea
              value={form.note}
              onChange={(e) => set("note", e.target.value)}
              placeholder="e.g. Monthly wifi bill"
              rows={3}
              maxLength={255}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black resize-none"
            />
          </div>
        </form>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-100 flex gap-2">
          <button
            onClick={handleSubmit}
            disabled={isPending}
            className="flex-1 py-2.5 bg-black text-white text-sm font-medium rounded-lg hover:bg-gray-800 disabled:opacity-60 transition-colors flex items-center justify-center gap-2"
          >
            {isPending && (
              <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            )}
            {isPending ? "Saving..." : isCreate ? "Create Rule" : "Update Rule"}
          </button>
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2.5 text-sm text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
        </div>
      </div>
    </>
  );
};

export default RecurringForm;
