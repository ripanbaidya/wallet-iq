interface Props {
  active: boolean;
}

const UserStatusBadge: React.FC<Props> = ({ active }) => (
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

export default UserStatusBadge;
