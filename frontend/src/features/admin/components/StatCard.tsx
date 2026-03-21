import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { adminService } from "../adminService";
import type { Role } from "../admin.types";

interface Props {
  label: string;
  queryKey: string[];
  role: Role;
  active: boolean;
  accent?: boolean;
}

const StatCard: React.FC<Props> = ({
  label,
  queryKey,
  role,
  active,
  accent = false,
}) => {
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
};

export default StatCard;
