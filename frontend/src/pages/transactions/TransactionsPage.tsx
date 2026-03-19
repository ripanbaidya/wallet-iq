import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { transactionService } from "../../services/transactionService";
import { categoryService } from "../../services/categoryService";
import { paymentModeService } from "../../services/paymentModeService";
import { useAppQuery } from "../../hooks/useAppQuery";
import { useAppMutation } from "../../hooks/useAppMutation";
import { AppError } from "../../errors/AppError";
import { QueryError } from "../../components/ui/QueryError";
import Spinner from "../../components/ui/Spinner";
import { FiDownload } from "react-icons/fi";

import TransactionFilters, {
  DEFAULT_FILTERS,
  type FilterState,
} from "../../components/transactions/TransactionFilters";
import TransactionTable from "../../components/transactions/TransactionTable";
import TransactionForm from "../../components/transactions/TransactionForm";

import type {
  CreateTransactionRequest,
  PageInfo,
  TransactionResponse,
  UpdateTransactionRequest,
} from "../../types/transaction.types";

/* Constants */
const PAGE_SIZE = 20;

const EMPTY_PAGE_INFO: PageInfo = {
  number: 0,
  size: PAGE_SIZE,
  totalElements: 0,
  totalPages: 0,
};

/* Page */
export default function TransactionsPage() {
  const queryClient = useQueryClient();

  /* List state */
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState<FilterState>(DEFAULT_FILTERS);

  /* Panel state */
  /* 
    null  → panel closed
    false → create mode
    TransactionResponse → edit mode with that transaction pre-filled
  */
  const [editing, setEditing] = useState<TransactionResponse | null | false>(
    null,
  );
  const [submitError, setSubmitError] = useState<AppError | null>(null);
  const [isExporting, setIsExporting] = useState(false);

  const isFormOpen = editing !== null;
  const isEditMode = editing !== null && editing !== false;

  // Transactions query
  const txnQuery = useAppQuery({
    queryKey: ["transactions", page, filters],
    queryFn: () =>
      transactionService.getAll({
        page,
        size: PAGE_SIZE,
        type: filters.type || undefined,
        dateFrom: filters.dateFrom || undefined,
        dateTo: filters.dateTo || undefined,
      }),
    placeholderData: (prev) => prev,
  });

  // Category queries (both types for form dropdown)
  const categoryIncomeQuery = useAppQuery({
    queryKey: ["categories", "INCOME"],
    queryFn: () => categoryService.getAll("INCOME"),
  });
  const categoryExpenseQuery = useAppQuery({
    queryKey: ["categories", "EXPENSE"],
    queryFn: () => categoryService.getAll("EXPENSE"),
  });

  // Payment modes
  const paymentModeQuery = useAppQuery({
    queryKey: ["payment-modes"],
    queryFn: () => paymentModeService.getAll(),
  });

  // Handler
  const handleExport = async () => {
    setIsExporting(true);
    try {
      await transactionService.exportCsv();
    } catch (e) {
      console.error("Export failed", e);
    } finally {
      setIsExporting(false);
    }
  };

  // Create
  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: (data: CreateTransactionRequest) =>
      transactionService.create(data),
    onSuccess: () => {
      closeForm();
      setPage(0);
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
    onError: (err: AppError) => setSubmitError(err),
  });

  // Update
  const { mutate: update, isPending: isUpdating } = useAppMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: UpdateTransactionRequest;
    }) => transactionService.update(id, data),
    onSuccess: () => {
      closeForm();
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
    onError: (err: AppError) => setSubmitError(err),
  });

  // Delete
  const { mutate: remove, isPending: isDeleting } = useAppMutation({
    mutationFn: (id: string) => transactionService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
    onError: (err: AppError) => console.error("Delete failed", err.message),
  });

  // Helpers
  const openCreate = () => {
    setSubmitError(null);
    setEditing(false); // false = create mode, panel open
  };

  const openEdit = (txn: TransactionResponse) => {
    setSubmitError(null);
    setEditing(txn); // TransactionResponse = edit mode
  };

  const closeForm = () => {
    setEditing(null); // null = closed
    setSubmitError(null);
  };

  const handleFilterChange = (next: FilterState) => {
    setFilters(next);
    setPage(0);
  };

  const handleFilterReset = () => {
    setFilters(DEFAULT_FILTERS);
    setPage(0);
  };

  // Derived data
  const transactions = txnQuery.data?.data?.content ?? [];
  const pageInfo: PageInfo = txnQuery.data?.data?.page ?? EMPTY_PAGE_INFO;

  const allCategories = [
    ...(categoryIncomeQuery.data?.data ?? []),
    ...(categoryExpenseQuery.data?.data ?? []),
  ];
  const paymentModes = paymentModeQuery.data?.data ?? [];

  const isPageLoading = txnQuery.isLoading;
  const isPageRefetching = txnQuery.isFetching && !txnQuery.isLoading;
  const isPending = isCreating || isUpdating;

  // Render
  return (
    <div className="max-w-6xl mx-auto space-y-5">
      {/* Header */}
      <div className="flex items-center gap-2 justify-between">
        <div>
          <h1 className="text-xl font-semibold text-gray-900">Transactions</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            {pageInfo.totalElements > 0
              ? `${pageInfo.totalElements} transaction${pageInfo.totalElements === 1 ? "" : "s"} total`
              : "Track your income and expenses."}
          </p>
        </div>

        {/* Export CSV button */}
        <button
          onClick={handleExport}
          disabled={isExporting}
          className="text-sm font-medium border border-gray-200 text-gray-700 px-4 py-2 rounded-lg 
             hover:bg-gray-100 hover:border-gray-300 
             transition-all duration-200 
             disabled:opacity-50 disabled:cursor-not-allowed 
             flex items-center gap-2 active:scale-[0.98]"
        >
          {isExporting ? (
            <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin" />
          ) : (
            <FiDownload className="text-base" />
          )}

          <span>{isExporting ? "Exporting..." : "Export CSV"}</span>
        </button>

        {/* New Transaction button */}
        <button
          onClick={openCreate}
          className="text-sm bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors"
        >
          + New Transaction
        </button>
      </div>

      {/* Filters */}
      <TransactionFilters
        filters={filters}
        onChange={handleFilterChange}
        onReset={handleFilterReset}
      />

      {/* Content */}
      {isPageLoading ? (
        <div className="flex justify-center py-16">
          <Spinner />
        </div>
      ) : txnQuery.error ? (
        <QueryError error={txnQuery.error} onRetry={() => txnQuery.refetch()} />
      ) : transactions.length === 0 ? (
        <div className="bg-white border border-gray-200 rounded-lg py-16 text-center">
          <p className="text-sm text-gray-400 mb-3">
            {filters.type || filters.dateFrom || filters.dateTo
              ? "No transactions match the current filters."
              : "No transactions yet."}
          </p>
          {!(filters.type || filters.dateFrom || filters.dateTo) && (
            <button
              onClick={openCreate}
              className="text-sm text-black underline underline-offset-2"
            >
              Add your first transaction
            </button>
          )}
        </div>
      ) : (
        <div className="relative">
          {isPageRefetching && (
            <div className="absolute inset-0 bg-white/60 z-10 flex items-center justify-center rounded-lg">
              <Spinner />
            </div>
          )}
          <TransactionTable
            transactions={transactions}
            pageInfo={pageInfo}
            currentPage={page}
            onPageChange={setPage}
            onEdit={openEdit}
            onDelete={(id) => remove(id)}
            isDeleting={isDeleting}
          />
        </div>
      )}

      {/* Create / Edit panel — single component, mode-aware */}
      {isFormOpen &&
        (isEditMode ? (
          <TransactionForm
            mode="edit"
            open={isFormOpen}
            onClose={closeForm}
            onSubmit={(data: UpdateTransactionRequest) =>
              update({ id: (editing as TransactionResponse).id, data })
            }
            isPending={isPending}
            submitError={submitError}
            initialData={editing as TransactionResponse}
            categories={allCategories}
            paymentModes={paymentModes}
          />
        ) : (
          <TransactionForm
            mode="create"
            open={isFormOpen}
            onClose={closeForm}
            onSubmit={(data: CreateTransactionRequest) => create(data)}
            isPending={isPending}
            submitError={submitError}
            categories={allCategories}
            paymentModes={paymentModes}
          />
        ))}
    </div>
  );
}
