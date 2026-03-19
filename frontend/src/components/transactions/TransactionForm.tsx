import { useState, useEffect } from "react";
import { AppError } from "../../errors/AppError";
import { FieldErrorMessage } from "../ui/FieldErrorMessage";
import { FormError } from "../ui/FormError";
import type { CategoryResponse } from "../../types/category.types";
import type { PaymentModeResponse } from "../../types/paymentMode.types";
import type {
  CreateTransactionRequest,
  UpdateTransactionRequest,
  TransactionResponse,
  TxnType,
} from "../../types/transaction.types";

type CreateProps = {
  mode: "create";
  onSubmit: (data: CreateTransactionRequest) => void;
  initialData?: never;
};

type EditProps = {
  mode: "edit";
  onSubmit: (data: UpdateTransactionRequest) => void;
  initialData: TransactionResponse;
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

interface FormState {
  amount: string;
  type: TxnType;
  date: string;
  note: string;
  categoryId: string;
  paymentModeId: string;
}

const today = () => new Date().toISOString().split("T")[0];

const EMPTY: FormState = {
  amount: "",
  type: "EXPENSE",
  date: today(),
  note: "",
  categoryId: "",
  paymentModeId: "",
};

const TransactionForm: React.FC<Props> = (props) => {
  const { open, onClose, isPending, submitError, categories, paymentModes } =
    props;
  const mode = props.mode;
  const initialData = props.mode === "edit" ? props.initialData : undefined;

  const [form, setForm] = useState<FormState>(EMPTY);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  // Reset form when panel opens
  useEffect(() => {
    if (open) {
      if (mode === "edit" && initialData) {
        setForm({
          amount: initialData.amount.toString(),
          type: initialData.type,
          date: initialData.date,
          note: initialData.note || "",
          categoryId: initialData.categoryId || "",
          paymentModeId: initialData.paymentModeId || "",
        });
      } else {
        setForm(EMPTY);
      }
      setFieldErrors({});
    }
  }, [open, mode, initialData]);

  // Populate server-side field errors
  useEffect(() => {
    if (submitError?.isValidation) {
      setFieldErrors(submitError.toFieldErrorMap());
    }
  }, [submitError]);

  // Filter categories by selected type
  const filteredCategories = categories.filter(
    (c) => c.categoryType === form.type,
  );

  const set = (key: keyof FormState, value: string) => {
    setForm((prev) => {
      const next = { ...prev, [key]: value };
      // When type changes, reset category (it belongs to different type)
      if (key === "type") {
        next.categoryId = "";
      }
      return next;
    });
    if (fieldErrors[key]) {
      setFieldErrors((p) => ({ ...p, [key]: "" }));
    }
  };

  const validate = (): boolean => {
    const errors: Record<string, string> = {};
    if (
      !form.amount ||
      isNaN(Number(form.amount)) ||
      Number(form.amount) <= 0
    ) {
      errors.amount = "Enter a valid amount greater than 0";
    }
    if (!form.date) {
      errors.date = "Date is required";
    }
    if (!form.categoryId) {
      errors.categoryId = "Select a category";
    }
    if (!form.paymentModeId) {
      errors.paymentModeId = "Select a payment mode";
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    if (props.mode === "create") {
      props.onSubmit({
        amount: Number(form.amount),
        type: form.type,
        date: form.date,
        note: form.note.trim() || undefined,
        categoryId: form.categoryId || undefined,
        paymentModeId: form.paymentModeId || undefined,
      });
    } else {
      props.onSubmit({
        amount: Number(form.amount),
        type: form.type,
        date: form.date,
        note: form.note.trim() || undefined,
        categoryId: form.categoryId || undefined,
        paymentModeId: form.paymentModeId || undefined,
      });
    }
  };

  if (!open) return null;

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/30 z-30" onClick={onClose} />

      {/* Panel */}
      <div className="fixed inset-y-0 right-0 w-full max-w-md bg-white shadow-xl z-40 flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h2 className="text-base font-semibold text-gray-900">
            {mode === "edit" ? "Edit Transaction" : "New Transaction"}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-700 transition-colors text-xl leading-none"
          >
            ✕
          </button>
        </div>

        {/* Form */}
        <form
          onSubmit={handleSubmit}
          className="flex-1 overflow-y-auto px-6 py-5 space-y-5"
        >
          {/* Server error */}
          {submitError && !submitError.isValidation && (
            <FormError error={submitError.message} />
          )}

          {/* Type toggle */}
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

          {/* Date */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">Date</label>
            <input
              type="date"
              value={form.date}
              max={today()}
              onChange={(e) => set("date", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={fieldErrors.date} />
          </div>

          {/* Category */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">Category</label>
            <select
              value={form.categoryId}
              onChange={(e) => set("categoryId", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black bg-white"
            >
              <option value="">Select category</option>
              {filteredCategories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
            {filteredCategories.length === 0 && (
              <p className="text-xs text-amber-600 mt-1">
                No {form.type.toLowerCase()} categories found. Create one first.
              </p>
            )}
            <FieldErrorMessage message={fieldErrors.categoryId} />
          </div>

          {/* Payment Mode */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Payment Mode
            </label>
            <select
              value={form.paymentModeId}
              onChange={(e) => set("paymentModeId", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black bg-white"
            >
              <option value="">Select payment mode</option>
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
              placeholder="e.g. Dinner with friends"
              rows={3}
              maxLength={1000}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black resize-none"
            />
          </div>
        </form>

        {/* Footer actions */}
        <div className="px-6 py-4 border-t border-gray-100 flex gap-2">
          <button
            onClick={handleSubmit}
            disabled={isPending}
            className="flex-1 py-2.5 bg-black text-white text-sm font-medium rounded-lg hover:bg-gray-800 disabled:opacity-60 transition-colors flex items-center justify-center gap-2"
          >
            {isPending && (
              <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            )}
            {isPending
              ? "Saving..."
              : mode === "edit"
                ? "Update Transaction"
                : "Save Transaction"}
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

export default TransactionForm;
