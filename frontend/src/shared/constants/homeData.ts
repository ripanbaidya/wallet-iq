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
    avatarUrl: "https://i.pravatar.cc/150?img=1",
  },
  {
    quote:
      "Budget alerts changed my habits completely. I get pinged the second I'm close to my limit — no more month-end panic.",
    author: "Robert Smith",
    role: "Product Manager, Mumbai",
    avatarUrl: "https://i.pravatar.cc/150?img=2",
  },
  {
    quote:
      "The recurring transaction forecast helped me plan the next six months. This is what modern personal finance looks like.",
    author: "Rahul K.",
    role: "Freelancer, Hyderabad",
    avatarUrl: "https://i.pravatar.cc/150?img=3",
  },
  {
    quote:
      "Clean UI, fast, and the AI chat is scarily accurate. Switched from three different apps to just this one.",
    author: "Nisha R.",
    role: "Data Analyst, Pune",
    avatarUrl: "https://i.pravatar.cc/150?img=4",
  },
  {
    quote:
      "Set up my savings goal in two minutes. Seeing the progress bar move every week is genuinely motivating.",
    author: "Kiran B.",
    role: "Designer, Chennai",
    avatarUrl: "https://i.pravatar.cc/150?img=5",
  },
  {
    quote: "The AI insights actually feel personalized. It’s like having a financial advisor in my pocket.",
    author: "Ankit Sharma",
    role: "Backend Developer, Delhi",
    avatarUrl: "https://i.pravatar.cc/150?img=6",
  },
  {
    quote: "I finally understand where my money goes every month. The categorization is super accurate.",
    author: "Priya Mehta",
    role: "Chartered Accountant, Ahmedabad",
    avatarUrl: "https://i.pravatar.cc/150?img=7",
  },
  {
    quote: "Super intuitive interface. Didn’t need any onboarding to get started.",
    author: "Arjun Nair",
    role: "Startup Founder, Kochi",
    avatarUrl: "https://i.pravatar.cc/150?img=8",
  },
  {
    quote: "The reminders are a lifesaver. Never missed a bill payment again.",
    author: "Sneha Gupta",
    role: "Teacher, Lucknow",
    avatarUrl: "https://i.pravatar.cc/150?img=9",
  },
  {
    quote: "I love how fast everything is. No lag, no clutter, just clean finance tracking.",
    author: "Rohit Verma",
    role: "QA Engineer, Noida",
    avatarUrl: "https://i.pravatar.cc/150?img=10",
  },
  {
    quote: "This replaced Excel for me. That says everything.",
    author: "Vikram Joshi",
    role: "Business Analyst, Indore",
    avatarUrl: "https://i.pravatar.cc/150?img=11",
  },
  {
    quote: "The predictions are shockingly accurate. Helps me plan ahead confidently.",
    author: "Ayesha Khan",
    role: "Marketing Manager, Bhopal",
    avatarUrl: "https://i.pravatar.cc/150?img=12",
  },
  {
    quote: "Minimal UI but very powerful features underneath. Perfect balance.",
    author: "Deepak Yadav",
    role: "Civil Engineer, Patna",
    avatarUrl: "https://i.pravatar.cc/150?img=13",
  },
  {
    quote: "The savings goal feature actually keeps me disciplined.",
    author: "Mehul Shah",
    role: "Trader, Surat",
    avatarUrl: "https://i.pravatar.cc/150?img=14",
  },
  {
    quote: "I like how everything is automated. Less manual work, more insights.",
    author: "Pooja Singh",
    role: "HR Manager, Jaipur",
    avatarUrl: "https://i.pravatar.cc/150?img=15",
  },
  {
    quote: "Feels like this app was built for Indian users specifically. Love the experience.",
    author: "Sandeep Reddy",
    role: "Doctor, Visakhapatnam",
    avatarUrl: "https://i.pravatar.cc/150?img=16",
  },
  {
    quote: "No unnecessary features, just what I need to manage money properly.",
    author: "Harsh Patel",
    role: "Student, Vadodara",
    avatarUrl: "https://i.pravatar.cc/150?img=17",
  },
  {
    quote: "AI suggestions actually helped me cut down unnecessary expenses.",
    author: "Neha Agarwal",
    role: "Entrepreneur, Kanpur",
    avatarUrl: "https://i.pravatar.cc/150?img=18",
  },
  {
    quote: "Love the recurring tracking. Subscriptions are no longer hidden.",
    author: "Aditya Kulkarni",
    role: "Software Engineer, Nagpur",
    avatarUrl: "https://i.pravatar.cc/150?img=19",
  },
  {
    quote: "Everything just works smoothly. No bugs so far.",
    author: "Ritika Das",
    role: "Content Writer, Kolkata",
    avatarUrl: "https://i.pravatar.cc/150?img=20",
  },
  {
    quote: "Perfect for freelancers like me. Cash flow tracking is on point.",
    author: "Imran Sheikh",
    role: "Freelancer, Pune",
    avatarUrl: "https://i.pravatar.cc/150?img=21",
  },
  {
    quote: "Helps me stay within budget without feeling restricted.",
    author: "Kavya Iyer",
    role: "UX Designer, Bangalore",
    avatarUrl: "https://i.pravatar.cc/150?img=22",
  },
  {
    quote: "Notifications are timely but not annoying. Well-balanced.",
    author: "Manish Tiwari",
    role: "Banker, Varanasi",
    avatarUrl: "https://i.pravatar.cc/150?img=23",
  },
  {
    quote: "Very lightweight app. Doesn’t drain battery or data.",
    author: "Nitin Arora",
    role: "Sales Executive, Chandigarh",
    avatarUrl: "https://i.pravatar.cc/150?img=24",
  },
  {
    quote: "My entire financial life is now in one place. Super convenient.",
    author: "Divya Nair",
    role: "Consultant, Trivandrum",
    avatarUrl: "https://i.pravatar.cc/150?img=25",
  },
  {
    quote: "Simple, fast, and reliable. That’s all I needed.",
    author: "Gaurav Mishra",
    role: "Engineer, Ranchi",
    avatarUrl: "https://i.pravatar.cc/150?img=26",
  },
  {
    quote: "Great for tracking UPI expenses too. Very relevant for India.",
    author: "Rakesh Kumar",
    role: "Shop Owner, Delhi",
    avatarUrl: "https://i.pravatar.cc/150?img=27",
  },
  {
    quote: "The UI feels premium. Didn’t expect this level of polish.",
    author: "Shruti Kapoor",
    role: "Lawyer, Mumbai",
    avatarUrl: "https://i.pravatar.cc/150?img=28",
  },
  {
    quote: "Helps me stay financially aware every single day.",
    author: "Aman Jain",
    role: "Student, Kota",
    avatarUrl: "https://i.pravatar.cc/150?img=29",
  },
  {
    quote: "Best budgeting app I’ve used so far, hands down.",
    author: "Tanvi Desai",
    role: "Interior Designer, Surat",
    avatarUrl: "https://i.pravatar.cc/150?img=30",
  },
  {
    quote: "Everything is just one tap away. Super efficient.",
    author: "Yash Thakur",
    role: "Developer, Gurgaon",
    avatarUrl: "https://i.pravatar.cc/150?img=31",
  },
  {
    quote: "Makes financial planning feel easy and approachable.",
    author: "Rekha Pillai",
    role: "Homemaker, Kochi",
    avatarUrl: "https://i.pravatar.cc/150?img=32",
  },
  {
    quote: "Even my parents started using it. That says a lot.",
    author: "Abhishek Pandey",
    role: "Engineer, Allahabad",
    avatarUrl: "https://i.pravatar.cc/150?img=33",
  },
  {
    quote: "Tracks everything without overwhelming me. Perfect UX.",
    author: "Komal Verma",
    role: "Student, Jaipur",
    avatarUrl: "https://i.pravatar.cc/150?img=34",
  },
  {
    quote: "The cleanest finance app I’ve come across in India.",
    author: "Sahil Choudhary",
    role: "Photographer, Delhi",
    avatarUrl: "https://i.pravatar.cc/150?img=35",
  },
];