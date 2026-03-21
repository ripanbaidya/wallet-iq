/**
 * DangerZone
 *
 * Displays irreversible account actions.
 * Delete Account is disabled until the backend endpoint is implemented.
 */
const DangerZone: React.FC = () => (
  <div className="bg-white border border-gray-200 rounded-2xl overflow-hidden">
    {/* Dark header band */}
    <div className="bg-[#0f0f0f] px-4 sm:px-5 py-4 flex items-start gap-3 relative overflow-hidden">
      <div
        className="absolute inset-0 pointer-events-none opacity-60"
        style={{
          backgroundImage:
            "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
          backgroundSize: "20px 20px",
        }}
      />
      {/* Red accent glow */}
      <div className="absolute top-0 right-0 w-24 h-24 bg-red-500/10 rounded-full blur-2xl pointer-events-none" />

      <span className="relative text-red-400 text-base mt-0.5">⚠</span>
      <div className="relative">
        <p className="text-sm font-semibold text-white">Danger Zone</p>
        <p className="text-xs text-white/40 mt-0.5 leading-relaxed">
          These actions are permanent and cannot be undone.
        </p>
      </div>
    </div>

    {/* Actions */}
    <div className="px-4 sm:px-5 py-4 sm:py-5">
      <div className="flex flex-col xs:flex-row xs:items-center justify-between gap-3 xs:gap-4">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-gray-900">Delete account</p>
          <p className="text-xs text-gray-400 mt-0.5 leading-relaxed">
            Permanently removes your account and all associated data.
          </p>
        </div>
        <button
          disabled
          title="Not yet available"
          className="shrink-0 self-start xs:self-auto text-xs font-semibold text-red-500 border border-red-200 bg-red-50/50 px-4 py-2 rounded-lg opacity-50 cursor-not-allowed"
        >
          Delete
        </button>
      </div>
    </div>
  </div>
);

export default DangerZone;
