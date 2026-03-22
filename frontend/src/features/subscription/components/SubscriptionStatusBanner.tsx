interface Props {
  expiresAt: string;
}

const formatExpiry = (expiresAt: string) =>
  new Date(expiresAt).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "long",
    year: "numeric",
  });

const SubscriptionStatusBanner: React.FC<Props> = ({ expiresAt }) => (
  <div className="bg-green-50 border border-green-200 rounded-xl px-5 py-4 flex items-start gap-3">
    <span className="text-green-600 text-lg mt-0.5">✓</span>
    <div>
      <p className="text-sm font-semibold text-green-800">
        Subscription Active
      </p>
      <p className="text-xs text-green-600 mt-0.5">
        Valid until {formatExpiry(expiresAt)}
      </p>
    </div>
  </div>
);

export default SubscriptionStatusBanner;
