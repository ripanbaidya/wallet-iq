import Spinner from "../../../shared/components/ui/Spinner";

interface Props {
  isVisible: boolean;
}

const PaymentProcessingOverlay: React.FC<Props> = ({ isVisible }) => {
  if (!isVisible) return null;

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl px-8 py-8 text-center shadow-2xl max-w-xs mx-4">
        <Spinner size="lg" className="mx-auto mb-4" />
        <p className="text-sm font-semibold text-gray-900">
          Verifying payment...
        </p>
        <p className="text-xs text-gray-400 mt-1">
          Please don't close this window
        </p>
      </div>
    </div>
  );
};

export default PaymentProcessingOverlay;
