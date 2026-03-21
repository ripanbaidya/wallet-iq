import StatCard from "./StatCard";

const AdminStatsGrid: React.FC = () => (
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
);

export default AdminStatsGrid;
