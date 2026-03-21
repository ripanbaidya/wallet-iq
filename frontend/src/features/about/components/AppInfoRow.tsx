interface Props {
  label: string;
  value: React.ReactNode;
}

const AppInfoRow: React.FC<Props> = ({ label, value }) => (
  <div className="flex items-center justify-between py-3.5 gap-4 border-b border-gray-100 last:border-0">
    <span className="text-sm text-gray-500 shrink-0">{label}</span>
    <span className="text-sm font-medium text-gray-900 text-right break-all min-w-0">
      {value}
    </span>
  </div>
);

export default AppInfoRow;
