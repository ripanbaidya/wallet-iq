import { useState, useRef, useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";
import {
  RiNotification3Line,
  RiDeleteBinLine,
  RiCloseLine,
} from "react-icons/ri";

import { notificationService } from "../notificationService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import type {
  NotificationResponse,
  NotificationType,
} from "../notification.types";

// Type metadata
const TYPE_CONFIG: Record<
  NotificationType,
  { icon: string; color: string; bg: string }
> = {
  BUDGET_ALERT: { icon: "⚠", color: "text-amber-700", bg: "bg-amber-50" },
  RECURRING_TRANSACTION: {
    icon: "↻",
    color: "text-blue-700",
    bg: "bg-blue-50",
  },
  SAVINGS_GOAL: { icon: "◎", color: "text-green-700", bg: "bg-green-50" },
  LOGIN_ALERT: { icon: "⚡", color: "text-red-700", bg: "bg-red-50" },
  SYSTEM: { icon: "·", color: "text-gray-600", bg: "bg-gray-100" },
};

// Relative time formatter
const formatRelativeTime = (isoStr: string): string => {
  const diff = Date.now() - new Date(isoStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return "Just now";
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
};

/* Single notificaiton row */
const NotificationItem: React.FC<{
  notification: NotificationResponse;
  onDelete: (id: string) => void;
  isDeleting: boolean;
}> = ({ notification, onDelete, isDeleting }) => {
  const cfg =
    TYPE_CONFIG[notification.type as NotificationType] ?? TYPE_CONFIG.SYSTEM;

  return (
    <div className="flex items-start gap-3 px-4 py-3 hover:bg-gray-50 transition-colors group">
      {/* Type icon */}
      <div
        className={`shrink-0 w-7 h-7 rounded-full ${cfg.bg} flex items-center justify-center text-xs font-bold mt-0.5 ${cfg.color}`}
      >
        {cfg.icon}
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0">
        <p className="text-[13px] text-gray-800 leading-snug">
          {notification.message}
        </p>
        <p className="text-[11px] text-gray-400 mt-1">
          {formatRelativeTime(notification.createdAt)}
        </p>
      </div>

      {/* Delete button — visible on hover */}
      <button
        onClick={() => onDelete(notification.id)}
        disabled={isDeleting}
        className="shrink-0 opacity-0 group-hover:opacity-100 transition-opacity p-1 rounded text-gray-300 hover:text-red-500 disabled:opacity-20"
        aria-label="Dismiss notification"
      >
        <RiCloseLine size={13} />
      </button>
    </div>
  );
};

/* Main bell component */
interface Props {
  /** Pass "sm" on tablet to render a smaller icon */
  size?: "sm" | "md";
  /** Align the dropdown panel to the left or right of the bell icon */
  align?: "left" | "right";
}

const NotificationBell: React.FC<Props> = ({ size = "md", align = "left" }) => {
  const [open, setOpen] = useState(false);
  const panelRef = useRef<HTMLDivElement>(null);
  const queryClient = useQueryClient();

  /* Fetch */
  const { data } = useAppQuery({
    queryKey: ["notifications"],
    queryFn: () => notificationService.getAll(),
    // Poll every 30s so new budget/goal alerts appear without WebSocket for now
    refetchInterval: 30_000,
  });

  const notifications = data?.data ?? [];
  const count = notifications.length;

  /* Delete one */
  const { mutate: deleteOne, isPending: isDeletingOne } = useAppMutation({
    mutationFn: (id: string) => notificationService.deleteById(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  /* Delete all */
  const { mutate: deleteAll, isPending: isDeletingAll } = useAppMutation<
    void,
    void
  >({
    mutationFn: () => notificationService.deleteAll(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  /* Close on outside click */
  useEffect(() => {
    if (!open) return;
    const handle = (e: MouseEvent) => {
      if (panelRef.current && !panelRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handle);
    return () => document.removeEventListener("mousedown", handle);
  }, [open]);

  const iconSize = size === "sm" ? 17 : 19;

  return (
    <div className="relative" ref={panelRef}>
      {/* Bell button */}
      <button
        onClick={() => setOpen((v) => !v)}
        className={`relative flex items-center justify-center rounded-lg transition-colors
          ${size === "sm" ? "w-10 h-10" : "p-1.5"}
          text-gray-400 hover:text-gray-700 hover:bg-gray-50`}
        aria-label="Notifications"
      >
        <RiNotification3Line size={iconSize} />

        {/* Badge */}
        {count > 0 && (
          <span className="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center px-1 leading-none">
            {count > 9 ? "9+" : count}
          </span>
        )}
      </button>

      {/* Dropdown panel */}
      {open && (
        <div
          className={`absolute ${align === "right" ? "right-0" : "left-0"} top-full mt-2 w-80 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden`}
          style={{ maxHeight: "420px" }}
        >
          {/* Panel header */}
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
            <div className="flex items-center gap-2">
              <span className="text-sm font-semibold text-gray-900">
                Notifications
              </span>
              {count > 0 && (
                <span className="text-xs bg-gray-100 text-gray-600 px-1.5 py-0.5 rounded-full font-medium">
                  {count}
                </span>
              )}
            </div>
            {count > 0 && (
              <button
                onClick={() => deleteAll()}
                disabled={isDeletingAll}
                className="flex items-center gap-1 text-xs text-gray-400 hover:text-red-500 transition-colors disabled:opacity-40"
              >
                <RiDeleteBinLine size={12} />
                Clear all
              </button>
            )}
          </div>

          {/* Notification list */}
          <div className="overflow-y-auto" style={{ maxHeight: "360px" }}>
            {notifications.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-10 px-4 text-center">
                <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center mb-3">
                  <RiNotification3Line size={18} className="text-gray-400" />
                </div>
                <p className="text-sm text-gray-400">You're all caught up</p>
                <p className="text-xs text-gray-300 mt-1">
                  Budget alerts and goal updates will appear here
                </p>
              </div>
            ) : (
              <div className="divide-y divide-gray-50">
                {notifications.map((n: any) => (
                  <NotificationItem
                    key={n.id}
                    notification={n}
                    onDelete={deleteOne}
                    isDeleting={isDeletingOne}
                  />
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
