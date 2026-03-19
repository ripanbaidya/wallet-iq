import type { DashboardSummary } from "../../types/dashboard.types";

interface Props {
  summary: DashboardSummary;
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const SummaryCards: React.FC<Props> = ({ summary }) => {
  const { totalIncome, totalExpense, netBalance } = summary;

  const savingsRate =
    totalIncome > 0 ? Math.max(0, (netBalance / totalIncome) * 100) : 0;

  const cards = [
    {
      label: "Total Income",
      value: fmt(totalIncome),
      color: "text-green-600",
      bg: "bg-green-50",
      border: "border-green-100",
      icon: "↑",
    },
    {
      label: "Total Expense",
      value: fmt(totalExpense),
      color: "text-red-600",
      bg: "bg-red-50",
      border: "border-red-100",
      icon: "↓",
    },
    {
      label: "Net Balance",
      value: fmt(netBalance),
      color: netBalance >= 0 ? "text-gray-900" : "text-red-600",
      bg: "bg-white",
      border: "border-gray-200",
      icon: "=",
    },
    {
      label: "Savings Rate",
      value: `${savingsRate.toFixed(1)}%`,
      color:
        savingsRate >= 20
          ? "text-green-600"
          : savingsRate >= 10
            ? "text-amber-600"
            : "text-red-500",
      bg: "bg-white",
      border: "border-gray-200",
      icon: "◎",
    },
  ];

  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card) => (
        <div
          key={card.label}
          className={`${card.bg} border ${card.border} rounded-lg p-4`}
        >
          <div className="flex items-center justify-between mb-2">
            <p className="text-xs text-gray-500 font-medium">{card.label}</p>
            <span className={`text-sm font-semibold ${card.color}`}>
              {card.icon}
            </span>
          </div>
          <p className={`text-xl font-semibold ${card.color}`}>{card.value}</p>
        </div>
      ))}
    </div>
  );
};

export default SummaryCards;
