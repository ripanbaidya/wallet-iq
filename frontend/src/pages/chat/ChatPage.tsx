import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { chatService } from "../../services/chatService";
import { useAppQuery } from "../../hooks/useAppQuery";
import { useAppMutation } from "../../hooks/useAppMutation";
import { AppError } from "../../errors/AppError";
import Spinner from "../../components/ui/Spinner";
import { QueryError } from "../../components/ui/QueryError";

import SessionList from "../../components/chat/SessionList";
import MessageList from "../../components/chat/MessageList";
import MessageInput from "../../components/chat/MessageInput";

import type {
  ChatMessageResponse,
  ChatSessionResponse,
} from "../../types/chat.types";

export default function ChatPage() {
  const queryClient = useQueryClient();

  const [activeSession, setActiveSession] =
    useState<ChatSessionResponse | null>(null);
  const [messages, setMessages] = useState<ChatMessageResponse[]>([]);
  const [isQuerying, setIsQuerying] = useState(false);

  // Controls the mobile sidebar drawer
  const [sidebarOpen, setSidebarOpen] = useState(false);

  // Sessions
  const sessionsQuery = useAppQuery({
    queryKey: ["chat-sessions"],
    queryFn: () => chatService.getSessions(),
  });

  const sessions = sessionsQuery.data?.data ?? [];

  // Messages for active session
  const messagesQuery = useAppQuery({
    queryKey: ["chat-messages", activeSession?.id],
    queryFn: () => chatService.getMessages(activeSession!.id),
    enabled: !!activeSession,
  });

  // Sync server messages into local state (only when not mid-query)
  const serverMessages = messagesQuery.data?.data;
  if (
    serverMessages &&
    !isQuerying &&
    JSON.stringify(serverMessages) !== JSON.stringify(messages)
  ) {
    setMessages(serverMessages);
  }

  // Create session
  const { mutate: createSession, isPending: isCreatingSession } =
    useAppMutation({
      mutationFn: () => chatService.createSession({}),
      onSuccess: (res) => {
        queryClient.invalidateQueries({ queryKey: ["chat-sessions"] });
        setActiveSession(res.data);
        setMessages([]);
        setSidebarOpen(false); // close drawer on mobile after creating
      },
      onError: (err: AppError) =>
        console.error("Create session failed", err.message),
    });

  // Delete session
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

  // Send query
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

  // Handlers
  const handleSelectSession = (session: ChatSessionResponse) => {
    if (session.id === activeSession?.id) return;
    setActiveSession(session);
    setMessages([]);
    setIsQuerying(false);
    setSidebarOpen(false); // close drawer on mobile after selecting
  };

  const handleSend = (question: string) => {
    if (!activeSession || isQuerying) return;
    sendQuery({ sessionId: activeSession.id, question });
  };

  // Sidebar content
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

  // Render
  return (
    <div className="flex h-[calc(100vh_-_5rem)] rounded-2xl overflow-hidden border border-gray-200 shadow-sm bg-white relative">
      {/* Mobile sidebar backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/30 z-20 md:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Left sidebar
          - Mobile  : fixed overlay drawer, slides in from left (z-30)
          - md+     : static column, always visible
      */}
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

      {/* ══ Right: conversation panel ══ */}
      <div className="flex-1 flex flex-col min-w-0 bg-gray-50/30">
        {!activeSession ? (
          /* ── No session selected ── */
          <div className="flex-1 flex flex-col items-center justify-center text-center px-6 sm:px-8">
            {/* Mobile: hamburger to open sidebar */}
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

            {/* Feature hints */}
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
          /* ── Active session ── */
          <>
            {/* Conversation header */}
            <div className="shrink-0 flex items-center gap-3 px-3 sm:px-5 py-3.5 bg-white border-b border-gray-100 shadow-sm">
              {/* Hamburger — mobile only */}
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

              {/* New chat button */}
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

            {/* Messages */}
            {messagesQuery.isLoading && messages.length === 0 ? (
              <div className="flex-1 flex items-center justify-center">
                <Spinner />
              </div>
            ) : (
              <MessageList messages={messages} isQuerying={isQuerying} />
            )}

            {/* Input */}
            <MessageInput onSend={handleSend} isDisabled={isQuerying} />
          </>
        )}
      </div>
    </div>
  );
}
