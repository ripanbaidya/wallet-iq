import { useState } from "react";
import { useAppQuery } from "../../hooks/useAppQuery";
import { adminService } from "../../services/adminService";
import type { UserProfileResponse } from "../../types/admin.types";
import { RiRefreshLine, RiCloseLine, RiShieldUserLine } from "react-icons/ri";

/* Stat Card */
function StatCard({
  label,
  queryKey,
  role,
  active,
  accent = false,
}: {
  label: string;
  queryKey: string[];
  role: "USER" | "ADMIN";
  active: boolean;
  accent?: boolean;
}) {
  const { data, isLoading } = useAppQuery({
    queryKey,
    queryFn: () => adminService.getUserCount(role, active),
  });

  return (
    <div
      className={`rounded-xl border p-4 sm:p-5 ${
        accent ? "bg-gray-900 border-gray-800" : "bg-white border-gray-100"
      }`}
    >
      <p className="text-[11px] font-semibold uppercase tracking-widest mb-2 text-gray-400">
        {label}
      </p>
      <p
        className={`text-2xl sm:text-3xl font-bold leading-none ${
          accent ? "text-[#e8ff4f]" : "text-gray-900"
        }`}
      >
        {isLoading ? (
          <span
            className={`inline-block w-10 h-7 rounded animate-pulse ${
              accent ? "bg-gray-800" : "bg-gray-100"
            }`}
          />
        ) : (
          (data?.data ?? "—")
        )}
      </p>
    </div>
  );
}

/* User Details modal */

function UserDetailModal({
  userId,
  onClose,
}: {
  userId: string;
  onClose: () => void;
}) {
  const { data, isLoading } = useAppQuery({
    queryKey: ["admin", "user", userId],
    queryFn: () => adminService.getUserById(userId),
  });

  const user = data?.data;

  return (
    <div
      className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/50 px-0 sm:px-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-t-2xl sm:rounded-xl border border-gray-100 w-full sm:max-w-md shadow-2xl"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-5 sm:px-6 py-4 border-b border-gray-100">
          <div className="flex items-center gap-2">
            <span className="text-gray-900">
              <RiShieldUserLine size={18} />
            </span>
            <h2 className="text-sm font-semibold text-gray-900">
              User Details
            </h2>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-gray-400 hover:bg-gray-100 hover:text-gray-600 transition-colors"
          >
            <RiCloseLine size={18} />
          </button>
        </div>

        {/* Body */}
        <div className="px-5 sm:px-6 py-5">
          {isLoading ? (
            <div className="space-y-3">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="flex gap-3 items-center">
                  <div className="h-4 bg-gray-100 animate-pulse rounded w-24 shrink-0" />
                  <div className="h-4 bg-gray-100 animate-pulse rounded flex-1" />
                </div>
              ))}
            </div>
          ) : user ? (
            <dl className="space-y-3 text-sm">
              {(
                [
                  ["ID", user.id],
                  ["Full Name", user.fullName],
                  ["Email", user.email],
                  ["Account Status", user.active ? "Active" : "Inactive"],
                  [
                    "Email Verified",
                    user.isEmailVerified ? "Verified" : "Not verified",
                  ],
                ] as [string, string][]
              ).map(([key, val]) => (
                <div key={key} className="flex gap-3">
                  <dt className="w-28 shrink-0 text-gray-400 text-[12px] font-medium pt-0.5">
                    {key}
                  </dt>
                  <dd
                    className={`break-all text-[13px] font-medium ${
                      key === "Account Status"
                        ? val === "Active"
                          ? "text-green-600"
                          : "text-gray-400"
                        : key === "Email Verified"
                          ? val === "Verified"
                            ? "text-green-600"
                            : "text-gray-400"
                          : "text-gray-800"
                    }`}
                  >
                    {val}
                  </dd>
                </div>
              ))}
            </dl>
          ) : (
            <p className="text-sm text-gray-400 text-center py-4">
              User not found.
            </p>
          )}
        </div>

        {/* Footer */}
        <div className="px-5 sm:px-6 pb-[calc(1.25rem_+_env(safe-area-inset-bottom))]">
          <button
            onClick={onClose}
            className="w-full py-2.5 bg-gray-900 text-white text-sm font-medium rounded-xl hover:bg-gray-700 transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

// Status Badge

function Badge({ active }: { active: boolean }) {
  return (
    <span
      className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
        active
          ? "bg-green-50 text-green-700 border border-green-200"
          : "bg-gray-100 text-gray-400 border border-gray-200"
      }`}
    >
      <span
        className={`w-1.5 h-1.5 rounded-full ${
          active ? "bg-green-500" : "bg-gray-300"
        }`}
      />
      {active ? "Active" : "Inactive"}
    </span>
  );
}

// User Card (mobile row)

/**
 * Shows name, email, badges and a View button in a compact card layout.
 */
function UserCard({
  user,
  onView,
}: {
  user: UserProfileResponse;
  onView: () => void;
}) {
  return (
    <div className="px-4 py-3.5 border-b border-gray-50 last:border-0 flex items-center gap-3">
      {/* Avatar */}
      <div className="w-9 h-9 rounded-full bg-gray-900 flex items-center justify-center shrink-0">
        <span className="text-[#e8ff4f] text-xs font-black select-none">
          {user.fullName?.charAt(0)?.toUpperCase() ?? "?"}
        </span>
      </div>

      {/* Info */}
      <div className="flex-1 min-w-0">
        <p className="text-[13px] font-semibold text-gray-900 truncate leading-tight">
          {user.fullName}
        </p>
        <p className="text-xs text-gray-400 truncate mt-0.5">{user.email}</p>
        <div className="flex items-center gap-2 mt-1.5">
          <Badge active={user.active} />
          <span
            className={`text-[11px] font-medium ${
              user.isEmailVerified ? "text-green-600" : "text-gray-300"
            }`}
          >
            {user.isEmailVerified ? "✓ Verified" : "Unverified"}
          </span>
        </div>
      </div>

      {/* View */}
      <button
        onClick={onView}
        className="shrink-0 text-[12px] font-medium text-gray-500 hover:text-gray-900 border border-gray-200 hover:border-gray-300 px-2.5 py-1.5 rounded-lg transition-colors"
      >
        View
      </button>
    </div>
  );
}

// Loading skeleton

function SkeletonRows() {
  return (
    <div className="divide-y divide-gray-50">
      {[...Array(5)].map((_, i) => (
        <div key={i} className="px-5 py-4 flex items-center gap-4">
          <div className="w-8 h-8 rounded-full bg-gray-100 animate-pulse shrink-0" />
          <div className="flex-1 space-y-2">
            <div className="h-3.5 bg-gray-100 animate-pulse rounded w-1/3" />
            <div className="h-3 bg-gray-100 animate-pulse rounded w-1/2" />
          </div>
          <div className="h-5 w-14 bg-gray-100 animate-pulse rounded-full" />
        </div>
      ))}
    </div>
  );
}

// Main Page

const PAGE_SIZE = 10;

export default function AdminPage() {
  const [page, setPage] = useState(0);
  const [selectedUserId, setSelectedUserId] = useState<string | null>(null);

  const { data, isLoading, isError, refetch, isFetching } = useAppQuery({
    queryKey: ["admin", "users", page],
    queryFn: () => adminService.getUsers(page, PAGE_SIZE),
  });

  const users: UserProfileResponse[] = data?.data?.content ?? [];
  const pageInfo = data?.data?.page;
  const totalPages = pageInfo?.totalPages ?? 0;
  const totalElements = pageInfo?.totalElements ?? 0;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-0 py-2 sm:py-4 space-y-4 sm:space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-xl font-bold text-gray-900">Admin Panel</h1>
          <p className="text-sm text-gray-400 mt-0.5">
            Manage users and view system stats.
          </p>
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 sm:gap-3">
        <StatCard
          label="Active Users"
          queryKey={["admin", "count", "USER", "active"]}
          role="USER"
          active={true}
          accent={true}
        />
        <StatCard
          label="Inactive Users"
          queryKey={["admin", "count", "USER", "inactive"]}
          role="USER"
          active={false}
        />
        <StatCard
          label="Active Admins"
          queryKey={["admin", "count", "ADMIN", "active"]}
          role="ADMIN"
          active={true}
        />
        <StatCard
          label="Total (Active)"
          queryKey={["admin", "count", "USER", "total"]}
          role="USER"
          active={true}
        />
      </div>

      {/* Users table / card list */}
      <div className="bg-white border border-gray-100 rounded-2xl overflow-hidden">
        {/* Toolbar */}
        <div className="px-4 sm:px-5 py-4 border-b border-gray-100 flex items-center justify-between gap-3">
          <div>
            <h2 className="text-sm font-semibold text-gray-900">All Users</h2>
            {totalElements > 0 && (
              <p className="text-xs text-gray-400 mt-0.5">
                {totalElements} total
              </p>
            )}
          </div>
          <button
            onClick={() => refetch()}
            disabled={isFetching}
            className="flex items-center gap-1.5 text-xs text-gray-500 hover:text-gray-900 border border-gray-200 px-3 py-1.5 rounded-lg transition-colors disabled:opacity-50"
          >
            <RiRefreshLine
              size={13}
              className={isFetching ? "animate-spin" : ""}
            />
            <span className="hidden xs:inline">Refresh</span>
          </button>
        </div>

        {/* Loading */}
        {isLoading && <SkeletonRows />}

        {/* Error */}
        {isError && (
          <div className="px-5 py-12 text-center">
            <p className="text-sm text-gray-400 mb-3">Failed to load users.</p>
            <button
              onClick={() => refetch()}
              className="text-xs text-gray-900 border border-gray-200 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Try again
            </button>
          </div>
        )}

        {/* Empty */}
        {!isLoading && !isError && users.length === 0 && (
          <div className="px-5 py-12 text-center text-sm text-gray-400">
            No users found.
          </div>
        )}

        {!isLoading && !isError && users.length > 0 && (
          <>
            {/* ── Mobile: card list (< sm) ── */}
            <div className="sm:hidden">
              {users.map((user) => (
                <UserCard
                  key={user.id}
                  user={user}
                  onView={() => setSelectedUserId(user.id)}
                />
              ))}
            </div>

            {/* ── sm+: full table ── */}
            <div className="hidden sm:block overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left border-b border-gray-100 bg-gray-50/50">
                    <th className="px-5 py-3 text-[11px] font-semibold text-gray-400 uppercase tracking-wider">
                      User
                    </th>
                    <th className="px-5 py-3 text-[11px] font-semibold text-gray-400 uppercase tracking-wider">
                      Email
                    </th>
                    <th className="px-5 py-3 text-[11px] font-semibold text-gray-400 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-5 py-3 text-[11px] font-semibold text-gray-400 uppercase tracking-wider">
                      Verified
                    </th>
                    <th className="px-5 py-3" />
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {users.map((user) => (
                    <tr
                      key={user.id}
                      className="hover:bg-gray-50/70 transition-colors"
                    >
                      <td className="px-5 py-3.5">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-full bg-gray-900 flex items-center justify-center shrink-0">
                            <span className="text-[#e8ff4f] text-xs font-black select-none">
                              {user.fullName?.charAt(0)?.toUpperCase() ?? "?"}
                            </span>
                          </div>
                          <span className="font-medium text-gray-900 text-[13px]">
                            {user.fullName}
                          </span>
                        </div>
                      </td>
                      <td className="px-5 py-3.5 text-gray-400 text-[13px]">
                        {user.email}
                      </td>
                      <td className="px-5 py-3.5">
                        <Badge active={user.active} />
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`text-[12px] font-medium ${
                            user.isEmailVerified
                              ? "text-green-600"
                              : "text-gray-300"
                          }`}
                        >
                          {user.isEmailVerified ? "✓ Verified" : "Unverified"}
                        </span>
                      </td>
                      <td className="px-5 py-3.5">
                        <button
                          onClick={() => setSelectedUserId(user.id)}
                          className="text-[12px] font-medium text-gray-500 hover:text-gray-900 border border-gray-200 hover:border-gray-300 px-2.5 py-1 rounded-lg transition-colors"
                        >
                          View
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="px-4 sm:px-5 py-3.5 border-t border-gray-100 flex items-center justify-between gap-3">
            <p className="text-xs text-gray-400">
              Page {page + 1} of {totalPages}
            </p>
            <div className="flex gap-2">
              <button
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
                className="text-xs border border-gray-200 px-3 py-1.5 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
              >
                Previous
              </button>
              <button
                disabled={page + 1 >= totalPages}
                onClick={() => setPage((p) => p + 1)}
                className="text-xs bg-gray-900 text-white px-3 py-1.5 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-700 transition-colors"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Detail modal */}
      {selectedUserId && (
        <UserDetailModal
          userId={selectedUserId}
          onClose={() => setSelectedUserId(null)}
        />
      )}
    </div>
  );
}
