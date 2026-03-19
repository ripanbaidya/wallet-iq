/* Data for Home page */
export interface Feature {
  icon: string;
  title: string;
  description: string;
}

export interface Step {
  number: string;
  title: string;
  description: string;
}

export interface Testimonial {
  quote: string;
  author: string;
  role: string;
  /**
   * avatarUrl can be a local path like "/avatars/john.jpg" or an absolute URL.
   * Falls back to initials if undefined.
   */
  avatarUrl?: string;
}

export interface Stat {
  value: number;
  suffix: string;
  label: string;
}

/* Features list */
export const FEATURES: Feature[] = [
  {
    icon: "💬",
    title: "RAG-Powered AI Chat",
    description:
      "Ask anything about your spending in plain English. The assistant retrieves your actual transaction data — no guesswork, no hallucinations.",
  },
  {
    icon: "📊",
    title: "Live Dashboard",
    description:
      "Income, expenses, net balance, category splits and daily trends at a glance — all filtered by any month you choose.",
  },
  {
    icon: "🔔",
    title: "Budget Alerts",
    description:
      "Set monthly limits per category. Get real-time alerts the moment you approach or breach your threshold.",
  },
  {
    icon: "🎯",
    title: "Savings Goals",
    description:
      "Define goals with target amounts and deadlines. Contribute anytime and watch your progress bar move.",
  },
  {
    icon: "🔁",
    title: "Recurring Transactions",
    description:
      "Schedule daily, weekly, monthly or yearly entries. Get a 30–365 day cash-flow forecast automatically.",
  },
  {
    icon: "📥",
    title: "CSV Export",
    description:
      "Download your full history as a UTF-8 CSV in one click — ready for Excel, Google Sheets or your accountant.",
  },
];

/* How-it-works steps */
export const STEPS: Step[] = [
  {
    number: "01",
    title: "Create your account",
    description:
      "Sign up free and verify your email with a 6-digit OTP. Takes under a minute.",
  },
  {
    number: "02",
    title: "Log your transactions",
    description:
      "Add income and expenses manually, or automate repeating ones with recurring rules.",
  },
  {
    number: "03",
    title: "Set budgets and goals",
    description:
      "Define monthly spending caps per category and long-term savings milestones.",
  },
  {
    number: "04",
    title: "Ask your AI assistant",
    description:
      "Chat in natural language. WalletIQ pulls your real data to give grounded, personalised answers.",
  },
];

/* Social proof stats */
export const STATS: Stat[] = [
  { value: 12000, suffix: "+", label: "Transactions tracked" },
  { value: 98, suffix: "%", label: "Uptime" },
  { value: 5, suffix: "×", label: "Faster insights" },
  { value: 500, suffix: "+", label: "Active users" },
];

/* Testimonials */
export const TESTIMONIALS: Testimonial[] = [
  {
    quote:
      "Finally a finance app that actually answers my questions instead of showing me useless charts. The AI is surprisingly good.",
    author: "John Doe",
    role: "Software Engineer, Bangalore",
    avatarUrl: "/avatars/john.png",
  },
  {
    quote:
      "Budget alerts changed my habits completely. I get pinged the second I'm close to my limit — no more month-end panic.",
    author: "Robert Smith",
    role: "Product Manager, Mumbai",
    avatarUrl: undefined,
  },
  {
    quote:
      "The recurring transaction forecast helped me plan the next six months. This is what modern personal finance looks like.",
    author: "Rahul K.",
    role: "Freelancer, Hyderabad",
    avatarUrl: undefined,
  },
  {
    quote:
      "Clean UI, fast, and the AI chat is scarily accurate. Switched from three different apps to just this one.",
    author: "Nisha R.",
    role: "Data Analyst, Pune",
    avatarUrl: undefined,
  },
  {
    quote:
      "Set up my savings goal in two minutes. Seeing the progress bar move every week is genuinely motivating.",
    author: "Kiran B.",
    role: "Designer, Chennai",
    avatarUrl: undefined,
  },
];