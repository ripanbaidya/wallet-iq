interface Props {
  title: string;
  children: React.ReactNode;
}

const AppInfoSection: React.FC<Props> = ({ title, children }) => (
  <div className="bg-white border border-gray-200 rounded-2xl overflow-hidden">
    <div className="px-4 sm:px-5 pt-4 pb-2 border-b border-gray-100">
      <p className="text-xs font-semibold uppercase tracking-widest text-gray-400">
        {title}
      </p>
    </div>
    <div className="px-4 sm:px-5">{children}</div>
  </div>
);

export default AppInfoSection;
