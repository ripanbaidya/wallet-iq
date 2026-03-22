import { useEffect, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

import { chatService } from "../chatService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";
import Spinner from "../../../shared/components/ui/Spinner";
import { QueryError } from "../../../shared/components/ui/QueryError";

import SessionList from "../components/SessionList";
import MessageList from "../components/MessageList";
import MessageInput from "../components/MessageInput";
import { useSubscriptionGuard } from "../../../features/subscription/hooks/useSubscriptionGuard";

import type { ChatMessageResponse, ChatSessionResponse } from "../chat.types";
import { subscriptionService } from "../../subscription/subscriptionService";
import { ROUTES } from "../../../routes/routePaths";

export default function ChatPage() {
  const navigate = useNavigate();

  // ── All hooks first — no early returns before this block ──

  const { isLoading: isCheckingSubscription, isActive } =
    useSubscriptionGuard();

  const { data: subData } = useAppQuery({
    queryKey: ["subscription-status"],
    queryFn: () => subscriptionService.getStatus(),
    staleTime: 1000 * 60 * 5,
  });

  const queryClient = useQueryClient();

  const [activeSession, setActiveSession] =
    useState<ChatSessionResponse | null>(null);
  const [messages, setMessages] = useState<ChatMessageResponse[]>([]);
  const [isQuerying, setIsQuerying] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const sessionsQuery = useAppQuery({
    queryKey: ["chat-sessions"],
    queryFn: () => chatService.getSessions(),
    enabled: !isCheckingSubscription && isActive,
  });

  const messagesQuery = useAppQuery({
    queryKey: ["chat-messages", activeSession?.id],
    queryFn: () => chatService.getMessages(activeSession!.id),
    enabled: !!activeSession && !isCheckingSubscription && isActive,
  });

  useEffect(() => {
    const serverMessages = messagesQuery.data?.data;
    if (serverMessages && !isQuerying) {
      setMessages(serverMessages);
    }
  }, [messagesQuery.data?.data]);

  const { mutate: createSession, isPending: isCreatingSession } =
    useAppMutation({
      mutationFn: () => chatService.createSession({}),
      onSuccess: (res) => {
        queryClient.invalidateQueries({ queryKey: ["chat-sessions"] });
        setActiveSession(res.data);
        setMessages([]);
        setSidebarOpen(false);
      },
      onError: (err: AppError) =>
        console.error("Create session failed", err.message),
    });

  const { mutate: deleteSession, isPending: isDeletingSession } =
    useAppMutation({
      mutationFn: (id: string) => chatService.deleteSession(id),
      onSuccess: (_, deletedId) => {
        queryClient.invalidateQueries({ queryKey: ["chat-sessions"] });
        if (activeSession?.id === deletedId) {
          setActiveSession(null);
          setMessages([]);
        }
      },
      onError: (err: AppError) =>
        console.error("Delete session failed", err.message),
    });

  const { mutate: sendQuery } = useAppMutation({
    mutationFn: ({
      sessionId,
      question,
    }: {
      sessionId: string;
      question: string;
    }) => chatService.query(sessionId, { question }),

    onMutate: ({ question }) => {
      const userMsg: ChatMessageResponse = {
        id: `temp-user-${Date.now()}`,
        role: "USER",
        content: question,
        createdAt: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, userMsg]);
      setIsQuerying(true);
    },

    onSuccess: (res) => {
      const assistantMsg: ChatMessageResponse = {
        id: `temp-assistant-${Date.now()}`,
        role: "ASSISTANT",
        content: res.data.answer,
        createdAt: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, assistantMsg]);
      setIsQuerying(false);
      queryClient.invalidateQueries({ queryKey: ["chat-sessions"] });
      queryClient.invalidateQueries({
        queryKey: ["chat-messages", activeSession?.id],
      });
    },

    onError: (err: AppError) => {
      setIsQuerying(false);
      const errMsg: ChatMessageResponse = {
        id: `temp-error-${Date.now()}`,
        role: "ASSISTANT",
        content: `Something went wrong: ${err.message}. Please try again.`,
        createdAt: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, errMsg]);
    },
  });

  // ── Derived values ──
  const expiresAt = subData?.data?.expiresAt;
  const daysLeft = expiresAt
    ? Math.ceil(
        (new Date(expiresAt).getTime() - Date.now()) / (1000 * 60 * 60 * 24),
      )
    : null;

  const sessions = sessionsQuery.data?.data ?? [];

  // ── Handlers ──
  const handleSelectSession = (session: ChatSessionResponse) => {
    if (session.id === activeSession?.id) return;
    setActiveSession(session);
    setMessages([]);
    setIsQuerying(false);
    setSidebarOpen(false);
  };

  const handleSend = (question: string) => {
    if (!activeSession || isQuerying) return;
    sendQuery({ sessionId: activeSession.id, question });
  };

  // ── Early returns AFTER all hooks ──

  // 1. Still checking subscription
  if (isCheckingSubscription) {
    return (
      <div className="flex justify-center py-24">
        <Spinner />
      </div>
    );
  }

  // 2. No active subscription — show locked state inside the page
  if (!isActive) {
    return (
      <div className="flex h-[calc(100vh_-_5rem)] rounded-2xl overflow-hidden border border-gray-200 shadow-sm bg-white">
        {/* Blurred mock sidebar */}
        <div className="hidden md:flex w-60 shrink-0 bg-gray-50 border-r border-gray-200 flex-col items-center justify-center gap-3 opacity-40 pointer-events-none select-none">
          {[80, 64, 72, 56, 68].map((w, i) => (
            <div
              key={i}
              className="h-8 bg-gray-200 rounded-lg"
              style={{ width: `${w}%` }}
            />
          ))}
        </div>

        {/* Locked state — main area */}
        <div className="flex-1 flex flex-col items-center justify-center px-6 text-center relative overflow-hidden">
          {/* Background decoration */}
          <div
            className="absolute inset-0 pointer-events-none opacity-30"
            style={{
              backgroundImage:
                "radial-gradient(circle, #e5e7eb 1px, transparent 1px)",
              backgroundSize: "24px 24px",
            }}
          />
          <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-48 bg-gray-100 rounded-full blur-3xl pointer-events-none" />

          {/* Lock icon */}
          <div className="relative mb-6">
            <div className="w-16 h-16 rounded-2xl bg-[#0f0f0f] flex items-center justify-center shadow-lg">
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#e8ff4f"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
            </div>
          </div>

          {/* Heading */}
          <h2 className="relative text-xl font-bold text-gray-900 mb-2">
            AI Chat requires a subscription
          </h2>
          <p className="relative text-sm text-gray-500 max-w-sm leading-relaxed mb-8">
            Get unlimited access to WalletIQ's RAG-powered assistant — ask
            anything about your spending, budgets, and savings goals in plain
            English.
          </p>

          {/* Feature pills */}
          <div className="relative flex flex-wrap justify-center gap-2 mb-8 max-w-sm">
            {[
              "💬 Unlimited Q&A",
              "📊 Spending insights",
              "🎯 Goal tracking",
              "🔍 Transaction search",
            ].map((f) => (
              <span
                key={f}
                className="text-xs text-gray-600 bg-gray-100 border border-gray-200 px-3 py-1.5 rounded-full"
              >
                {f}
              </span>
            ))}
          </div>

          {/* CTA */}
          <div className="relative flex flex-col sm:flex-row items-center gap-3">
            <button
              onClick={() => navigate(ROUTES.subscription)}
              className="inline-flex items-center gap-2 px-6 py-3 bg-[#0f0f0f] text-white text-sm font-semibold rounded-xl hover:bg-black transition-colors shadow-md"
            >
              View subscription plans
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <line x1="5" y1="12" x2="19" y2="12" />
                <polyline points="12 5 19 12 12 19" />
              </svg>
            </button>

            <span className="text-xs text-gray-400">
              ₹199 / month · No auto-renewal
            </span>
          </div>
        </div>
      </div>
    );
  }

  // ── Sidebar content (only rendered when subscribed) ──
  const sidebarContent = sessionsQuery.isLoading ? (
    <div className="flex justify-center py-10">
      <Spinner />
    </div>
  ) : sessionsQuery.error ? (
    <div className="p-4">
      <QueryError
        error={sessionsQuery.error}
        onRetry={() => sessionsQuery.refetch()}
      />
    </div>
  ) : (
    <SessionList
      sessions={sessions}
      activeSessionId={activeSession?.id ?? null}
      onSelect={handleSelectSession}
      onCreate={() => createSession(undefined)}
      onDelete={(id) => deleteSession(id)}
      isCreating={isCreatingSession}
      isDeleting={isDeletingSession}
    />
  );

  // ── Normal chat render (subscribed users only) ──
  return (
    <div className="flex h-[calc(100vh_-_5rem)] rounded-2xl overflow-hidden border border-gray-200 shadow-sm bg-white relative">
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/30 z-20 md:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      <div
        className={`
          fixed top-0 left-0 h-full w-64 bg-gray-50 border-r border-gray-200 z-30 flex flex-col
          transition-transform duration-300 ease-in-out
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
          md:static md:w-60 md:shrink-0 md:translate-x-0 md:z-auto md:transition-none
        `}
      >
        {sidebarContent}
      </div>

      <div className="flex-1 flex flex-col min-w-0 bg-gray-50/30">
        {!activeSession ? (
          <div className="flex-1 flex flex-col items-center justify-center text-center px-6 sm:px-8">
            <button
              onClick={() => setSidebarOpen(true)}
              className="md:hidden absolute top-4 left-4 w-8 h-8 flex items-center justify-center rounded-lg text-gray-500 hover:bg-gray-100 transition-colors"
              aria-label="Open conversations"
            >
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
              >
                <line x1="3" y1="6" x2="21" y2="6" />
                <line x1="3" y1="12" x2="21" y2="12" />
                <line x1="3" y1="18" x2="21" y2="18" />
              </svg>
            </button>

            <div className="w-14 h-14 sm:w-16 sm:h-16 rounded-2xl bg-gradient-to-br from-gray-900 to-gray-700 flex items-center justify-center mb-5 shadow-lg">
              <span className="text-white text-xl sm:text-2xl font-bold">
                W
              </span>
            </div>
            <h2 className="text-base font-semibold text-gray-900 mb-1.5">
              WalletIQ AI Assistant
            </h2>
            <p className="text-xs text-gray-400 max-w-xs leading-relaxed mb-5">
              Powered by RAG — every answer is grounded in your actual
              transaction data, not generic advice.
            </p>
            <button
              onClick={() => createSession(undefined)}
              disabled={isCreatingSession}
              className="flex items-center gap-2 text-sm bg-gray-900 text-white px-5 py-2.5 rounded-xl hover:bg-black disabled:opacity-60 transition-colors shadow-sm"
            >
              {isCreatingSession ? (
                <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <svg
                  width="14"
                  height="14"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                </svg>
              )}
              {isCreatingSession ? "Starting..." : "Start a conversation"}
            </button>

            <div className="mt-8 grid grid-cols-3 gap-2 sm:gap-3 text-xs text-gray-500 max-w-sm">
              {[
                { icon: "🔍", label: "Semantic search over your transactions" },
                { icon: "💡", label: "Budget & savings insights" },
                { icon: "📊", label: "Spending patterns & trends" },
              ].map(({ icon, label }) => (
                <div
                  key={label}
                  className="flex flex-col items-center gap-1.5 p-2.5 sm:p-3 rounded-xl bg-white border border-gray-100 text-center"
                >
                  <span className="text-xl">{icon}</span>
                  <span className="leading-snug text-gray-400">{label}</span>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <>
            <div className="shrink-0 flex items-center gap-3 px-3 sm:px-5 py-3.5 bg-white border-b border-gray-100 shadow-sm">
              <button
                onClick={() => setSidebarOpen(true)}
                className="md:hidden w-8 h-8 flex items-center justify-center rounded-lg text-gray-500 hover:bg-gray-100 transition-colors shrink-0"
                aria-label="Open conversations"
              >
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                >
                  <line x1="3" y1="6" x2="21" y2="6" />
                  <line x1="3" y1="12" x2="21" y2="12" />
                  <line x1="3" y1="18" x2="21" y2="18" />
                </svg>
              </button>

              <div className="min-w-0 flex-1">
                <p className="text-sm font-semibold text-gray-900 truncate">
                  {activeSession.title}
                </p>
                <p className="text-xs text-gray-400 mt-0.5">
                  {messages.length} message{messages.length !== 1 ? "s" : ""}
                  {isQuerying && (
                    <span className="ml-2 text-blue-500 font-medium">
                      · Thinking...
                    </span>
                  )}
                </p>
              </div>

              {daysLeft !== null && daysLeft <= 5 && (
                <span className="text-xs text-amber-600 bg-amber-50 border border-amber-200 px-2 py-1 rounded-lg shrink-0">
                  ⚡ {daysLeft}d left
                </span>
              )}

              <div className="shrink-0">
                <button
                  onClick={() => createSession(undefined)}
                  disabled={isCreatingSession}
                  title="New chat"
                  className="text-xs text-gray-500 border border-gray-200 px-3 py-1.5 rounded-lg hover:bg-gray-50 disabled:opacity-40 transition-colors flex items-center gap-1.5"
                >
                  <svg
                    width="11"
                    height="11"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <line x1="12" y1="5" x2="12" y2="19" />
                    <line x1="5" y1="12" x2="19" y2="12" />
                  </svg>
                  <span className="hidden sm:inline">New chat</span>
                </button>
              </div>
            </div>

            {messagesQuery.isLoading && messages.length === 0 ? (
              <div className="flex-1 flex items-center justify-center">
                <Spinner />
              </div>
            ) : (
              <MessageList messages={messages} isQuerying={isQuerying} />
            )}

            <MessageInput onSend={handleSend} isDisabled={isQuerying} />
          </>
        )}
      </div>
    </div>
  );
}
