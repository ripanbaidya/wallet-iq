import type { TopExpenseItem } from "../../types/dashboard.types";

interface Props {
  data: TopExpenseItem[];
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);

const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
  });

const TopExpenses: React.FC<Props> = ({ data }) => {
  if (data.length === 0) {
    return (
      <p className="text-xs text-gray-400 py-4 text-center">
        No expenses this month.
      </p>
    );
  }

  return (
    <table className="w-full">
      <thead>
        <tr className="text-left text-xs text-gray-400 uppercase tracking-wide border-b border-gray-100">
          <th className="pb-2 font-medium">#</th>
          <th className="pb-2 font-medium">Category</th>
          <th className="pb-2 font-medium">Note</th>
          <th className="pb-2 font-medium">Date</th>
          <th className="pb-2 font-medium text-right">Amount</th>
        </tr>
      </thead>
      <tbody>
        {data.map((item, idx) => (
          <tr
            key={item.id}
            className="border-t border-gray-50 hover:bg-gray-50 transition-colors"
          >
            <td className="py-2.5 pr-3 text-xs text-gray-400">{idx + 1}</td>
            <td className="py-2.5 pr-3 text-sm text-gray-700">
              {item.categoryName ?? <span className="text-gray-400">—</span>}
            </td>
            <td className="py-2.5 pr-3 text-sm text-gray-500 max-w-[160px] truncate">
              {item.note ?? <span className="text-gray-300">—</span>}
            </td>
            <td className="py-2.5 pr-3 text-xs text-gray-400 whitespace-nowrap">
              {formatDate(item.date)}
            </td>
            <td className="py-2.5 text-sm font-medium text-red-600 text-right whitespace-nowrap">
              -{fmt(item.amount)}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default TopExpenses;
