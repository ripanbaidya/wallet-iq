import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { FiEdit2 } from "react-icons/fi";
import { toast } from "sonner";

import { categoryService } from "../../services/categoryService";
import { useAppQuery } from "../../hooks/useAppQuery";
import { useAppMutation } from "../../hooks/useAppMutation";
import { AppError } from "../../errors/AppError";
import { FieldErrorMessage } from "../../components/ui/FieldErrorMessage";
import { FormError } from "../../components/ui/FormError";
import { QueryError } from "../../components/ui/QueryError";
import Spinner from "../../components/ui/Spinner";

import type {
  CategoryResponse,
  CategoryType,
  UpdateCategoryRequest,
} from "../../types/category.types";

interface RowProps {
  category: CategoryResponse;
  onDelete: (id: string) => void;
  onEdit: (category: CategoryResponse) => void;
  isDeleting: boolean;
}

const CategoryRow: React.FC<RowProps> = ({
  category,
  onDelete,
  onEdit,
  isDeleting,
}) => (
  <tr className="border-t border-gray-100 hover:bg-gray-50 transition-colors">
    <td className="px-4 py-3 text-sm text-gray-800">
      <span>{category.name}</span>
      {category.isDefault && (
        <span className="ml-2 text-xs text-gray-400">(default)</span>
      )}
    </td>

    <td className="px-4 py-3">
      <span
        className={`inline-block text-xs font-medium px-2 py-0.5 rounded-full ${
          category.categoryType === "INCOME"
            ? "bg-green-50 text-green-700"
            : "bg-red-50 text-red-600"
        }`}
      >
        {category.categoryType}
      </span>
    </td>

    <td className="px-4 py-3 text-right">
      {!category.isDefault ? (
        <div className="flex justify-end gap-3">
          <button
            onClick={() => onEdit(category)}
            className="text-gray-500 hover:text-black"
          >
            <FiEdit2 size={14} />
          </button>

          <button
            onClick={() => onDelete(category.id)}
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

export default function CategoriesPage() {
  const queryClient = useQueryClient();

  const [activeType, setActiveType] = useState<CategoryType>("EXPENSE");

  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState("");
  const [formType, setFormType] = useState<CategoryType>("EXPENSE");

  const [editingCategory, setEditingCategory] =
    useState<CategoryResponse | null>(null);

  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  const { data, isLoading, error, refetch } = useAppQuery<{
    data: CategoryResponse[];
  }>({
    queryKey: ["categories", activeType],
    queryFn: () => categoryService.getAll(activeType),
  });

  const categories = data?.data ?? [];

  const resetForm = () => {
    setName("");
    setFieldErrors({});
    setFormError(null);
    setShowForm(false);
    setEditingCategory(null);
  };

  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: () =>
      categoryService.create({
        name: name.trim(),
        categoryType: formType,
      }),

    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({
        queryKey: ["categories"],
      });
      toast.success("Category created");
    },

    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  // update
  const { mutate: updateCategory, isPending: isUpdating } = useAppMutation({
    mutationFn: (payload: { id: string; data: UpdateCategoryRequest }) =>
      categoryService.update(payload.id, payload.data),

    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({
        queryKey: ["categories"],
      });
      toast.success("Category updated");
    },

    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  // delete
  const { mutate: remove, isPending: isDeleting } = useAppMutation({
    mutationFn: (id: string) => categoryService.delete(id),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["categories"],
      });
      toast.success("Category deleted");
    },

    onError: (err: AppError) => toast.error(err.message),
  });

  // ── Handlers ──────────────────────────────────────────
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!name.trim()) {
      setFieldErrors({ name: "Name is required" });
      return;
    }

    setFieldErrors({});
    setFormError(null);

    if (editingCategory) {
      updateCategory({
        id: editingCategory.id,
        data: {
          name: name.trim(),
          categoryType: formType,
        },
      });
    } else {
      create(undefined);
    }
  };

  const handleEdit = (category: CategoryResponse) => {
    setEditingCategory(category);
    setName(category.name);
    setFormType(category.categoryType);
    setShowForm(true);
  };

  const openForm = () => {
    setFormType(activeType);
    setName("");
    setShowForm(true);
  };

  const switchTab = (type: CategoryType) => {
    setActiveType(type);
    setShowForm(false);
  };

  // ui
  return (
    <div className="max-w-3xl mx-auto">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div className="flex items-center gap-6">
          <h1 className="text-xl font-semibold">Categories</h1>

          {/* Tabs for switching category types */}
          <div className="flex border rounded overflow-hidden">
            {(["EXPENSE", "INCOME"] as CategoryType[]).map((type) => (
              <button
                key={type}
                onClick={() => switchTab(type)}
                className={`px-4 py-1.5 text-sm font-medium transition-colors ${
                  activeType === type
                    ? "bg-gray-100 text-black"
                    : "bg-white text-gray-500 hover:bg-gray-50"
                }`}
              >
                {type}
              </button>
            ))}
          </div>
        </div>

        {!showForm && (
          <button
            onClick={openForm}
            className="bg-black text-white px-4 py-2 rounded"
          >
            + New
          </button>
        )}
      </div>

      {/* Form */}
      {showForm && (
        <div className="border p-4 mb-6 rounded">
          <h2 className="mb-3">{editingCategory ? "Edit" : "New"} Category</h2>

          <FormError error={formError} />

          <form onSubmit={handleSubmit} className="space-y-3">
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Name"
              className="border p-2 w-full"
            />
            <FieldErrorMessage message={fieldErrors.name} />

            <div className="flex gap-2">
              {(["EXPENSE", "INCOME"] as CategoryType[]).map((t) => (
                <button
                  key={t}
                  type="button"
                  onClick={() => setFormType(t)}
                  className={`px-3 py-1 border ${
                    formType === t ? "bg-black text-white" : ""
                  }`}
                >
                  {t}
                </button>
              ))}
            </div>

            <button
              type="submit"
              className="bg-black text-white px-4 py-2 rounded"
            >
              {editingCategory
                ? isUpdating
                  ? "Updating..."
                  : "Update"
                : isCreating
                  ? "Creating..."
                  : "Create"}
            </button>
          </form>
        </div>
      )}

      {/* Table */}
      <div className="border rounded">
        {isLoading ? (
          <Spinner />
        ) : error ? (
          <QueryError error={error} onRetry={refetch} />
        ) : (
          <table className="w-full">
            <tbody>
              {categories.map((cat) => (
                <CategoryRow
                  key={cat.id}
                  category={cat}
                  onDelete={remove}
                  onEdit={handleEdit}
                  isDeleting={isDeleting}
                />
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
