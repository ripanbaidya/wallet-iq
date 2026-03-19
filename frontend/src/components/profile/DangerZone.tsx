/**
 * DangerZone
 *
 * Displays irreversible account actions.
 * Delete Account is disabled until the backend endpoint is implemented.
 * Styled consistently with the rest of the profile page.
 */
const DangerZone: React.FC = () => (
  <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
    {/* Dark header band — consistent with ProfileHeader top band */}
    <div className="bg-[#0f0f0f] px-5 py-4 flex items-center gap-3 relative overflow-hidden">
      {/* Dot grid texture */}
      <div
        className="absolute inset-0 pointer-events-none"
        style={{
          backgroundImage:
            "radial-gradient(circle, #2a2a2a 1px, transparent 1px)",
          backgroundSize: "20px 20px",
        }}
      />
      <span className="relative text-red-400 text-base">⚠</span>
      <div className="relative">
        <p className="text-sm font-semibold text-white">Danger Zone</p>
        <p className="text-xs text-white/40 mt-0.5">
          These actions are permanent and cannot be undone.
        </p>
      </div>
    </div>

    {/* Actions */}
    <div className="px-5 py-5 space-y-4">
      {/* Delete account row */}
      <div className="flex items-center justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-gray-900">Delete account</p>
          <p className="text-xs text-gray-400 mt-0.5">
            Permanently removes your account and all associated data.
          </p>
        </div>
        <button
          disabled
          title="Not yet available"
          className="shrink-0 text-xs font-medium text-red-500 border border-red-200 px-4 py-2 rounded-lg opacity-40 cursor-not-allowed"
        >
          Delete
        </button>
      </div>
    </div>
  </div>
);

export default DangerZone;
