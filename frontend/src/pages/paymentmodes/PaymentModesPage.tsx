import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { FiEdit2 } from "react-icons/fi";
import { toast } from "sonner";

import { paymentModeService } from "../../services/paymentModeService";
import { useAppQuery } from "../../hooks/useAppQuery";
import { useAppMutation } from "../../hooks/useAppMutation";
import { AppError } from "../../errors/AppError";

import { FieldErrorMessage } from "../../components/ui/FieldErrorMessage";
import { FormError } from "../../components/ui/FormError";
import { QueryError } from "../../components/ui/QueryError";
import Spinner from "../../components/ui/Spinner";

import type {
  PaymentModeResponse,
  UpdatePaymentModeRequest,
} from "../../types/paymentMode.types";

interface RowProps {
  mode: PaymentModeResponse;
  onDelete: (id: string) => void;
  onEdit: (mode: PaymentModeResponse) => void;
  isDeleting: boolean;
}

const PaymentModeRow: React.FC<RowProps> = ({
  mode,
  onDelete,
  onEdit,
  isDeleting,
}) => (
  <tr className="border-t border-gray-100 hover:bg-gray-50 transition-colors">
    <td className="px-4 py-3 text-sm text-gray-800">
      <span>{mode.name}</span>
      {mode.isDefault && (
        <span className="ml-2 text-xs text-gray-400">(default)</span>
      )}
    </td>

    <td className="px-4 py-3 text-right">
      {!mode.isDefault ? (
        <div className="flex justify-end gap-3">
          <button
            onClick={() => onEdit(mode)}
            className="text-gray-500 hover:text-black"
          >
            <FiEdit2 size={14} />
          </button>

          <button
            onClick={() => onDelete(mode.id)}
            disabled={isDeleting}
            className="text-xs text-red-500 hover:text-red-700 disabled:opacity-40"
          >
            Delete
          </button>
        </div>
      ) : (
        <span className="text-xs text-gray-300">—</span>
      )}
    </td>
  </tr>
);

// page
const PaymentModesPage: React.FC = () => {
  const queryClient = useQueryClient();

  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState("");

  const [editingMode, setEditingMode] = useState<PaymentModeResponse | null>(
    null,
  );

  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  // fetch
  const { data, isLoading, error, refetch } = useAppQuery<{
    data: PaymentModeResponse[];
  }>({
    queryKey: ["payment-modes"],
    queryFn: paymentModeService.getAll,
  });

  const modes = data?.data ?? [];

  // helper
  const resetForm = () => {
    setName("");
    setFieldErrors({});
    setFormError(null);
    setShowForm(false);
    setEditingMode(null);
  };

  // create payment mode
  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: () =>
      paymentModeService.create({
        name: name.trim(),
      }),

    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({
        queryKey: ["payment-modes"],
      });
      toast.success("Payment mode created");
    },

    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  // update payment mode
  const { mutate: updateMode, isPending: isUpdating } = useAppMutation({
    mutationFn: (payload: { id: string; data: UpdatePaymentModeRequest }) =>
      paymentModeService.update(payload.id, payload.data),

    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({
        queryKey: ["payment-modes"],
      });
      toast.success("Payment mode updated");
    },

    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  // delete payment mode
  const { mutate: remove, isPending: isDeleting } = useAppMutation({
    mutationFn: (id: string) => paymentModeService.delete(id),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["payment-modes"],
      });
      toast.success("Payment mode deleted");
    },

    onError: (err: AppError) => toast.error(err.message),
  });

  // helpers
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!name.trim()) {
      setFieldErrors({ name: "Name is required" });
      return;
    }

    setFieldErrors({});
    setFormError(null);

    if (editingMode) {
      updateMode({
        id: editingMode.id,
        data: { name: name.trim() },
      });
    } else {
      create(undefined);
    }
  };

  const handleEdit = (mode: PaymentModeResponse) => {
    setEditingMode(mode);
    setName(mode.name);
    setShowForm(true);
  };

  const openForm = () => {
    setName("");
    setShowForm(true);
  };

  // UI
  return (
    <div className="max-w-3xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-xl font-semibold text-gray-900">Payment Modes</h1>
          <p className="text-sm text-gray-500 mt-1">
            Manage your payment methods.
          </p>
        </div>

        {!showForm && (
          <button
            onClick={openForm}
            className="text-sm bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800"
          >
            + New Payment Mode
          </button>
        )}
      </div>

      {/* Form */}
      {showForm && (
        <div className="bg-white border border-gray-200 rounded-lg p-5 mb-6">
          <h2 className="text-sm font-medium text-gray-700 mb-4">
            {editingMode ? "Edit Payment Mode" : "New Payment Mode"}
          </h2>

          <FormError error={formError} />

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="text-sm text-gray-600 block mb-1">Name</label>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. UPI, Cash, Card"
                className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm"
              />
              <FieldErrorMessage message={fieldErrors.name} />
            </div>

            <div className="flex gap-2">
              <button
                type="submit"
                disabled={isCreating || isUpdating}
                className="bg-black text-white px-4 py-2 rounded-lg text-sm"
              >
                {editingMode
                  ? isUpdating
                    ? "Updating..."
                    : "Update"
                  : isCreating
                    ? "Creating..."
                    : "Create"}
              </button>

              <button
                type="button"
                onClick={resetForm}
                className="px-4 py-2 text-sm border rounded-lg"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Table */}
      <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
        {isLoading ? (
          <div className="flex justify-center py-10">
            <Spinner />
          </div>
        ) : error ? (
          <div className="p-6">
            <QueryError error={error} onRetry={refetch} />
          </div>
        ) : modes.length === 0 ? (
          <div className="py-10 text-center text-sm text-gray-400">
            No payment modes yet.
          </div>
        ) : (
          <table className="w-full">
            <thead>
              <tr className="text-left text-xs text-gray-400 bg-gray-50">
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody>
              {modes.map((mode) => (
                <PaymentModeRow
                  key={mode.id}
                  mode={mode}
                  onDelete={remove}
                  onEdit={handleEdit}
                  isDeleting={isDeleting}
                />
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Footer */}
      {modes.length > 0 && (
        <p className="text-xs text-gray-400 mt-3 text-right">
          {modes.length} mode{modes.length === 1 ? "" : "s"} ·{" "}
          {modes.filter((m) => !m.isDefault).length} custom
        </p>
      )}
    </div>
  );
};

export default PaymentModesPage;
