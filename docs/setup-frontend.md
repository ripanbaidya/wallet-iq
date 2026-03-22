# 🚀 Step 0 — Clean Start

From your project root (`walletiq/`):

```bash
rm -rf frontend
mkdir frontend
cd frontend
```

---

# 🧱 Step 1 — Create React + TypeScript App (Vite)

Run:

```bash
npm create vite@latest
```

### It will ask:

* Project name → `.` (VERY IMPORTANT, so it uses current folder)
* Framework → `React`
* Variant → `TypeScript`

---

### Then run:

```bash
npm install
```

---

# ▶️ Step 2 — Verify Basic App

```bash
npm run dev
```

👉 Open browser → you should see default Vite React app

---

### Stop server:

```
Ctrl + C
```

---

# 🎨 Step 3 — Install Tailwind (Correct way)

```bash
npm install -D tailwindcss@3.4.3 postcss autoprefixer
```

---

# ⚙️ Step 4 — Initialize Tailwind

```bash
npx tailwindcss init -p
```

👉 This creates:

* `tailwind.config.js`
* `postcss.config.js`

---

# 🧩 Step 5 — Configure Tailwind

Open `tailwind.config.js`:

```js
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

---

# 🎨 Step 6 — Setup CSS

Open:

```text
src/index.css
```

Replace everything with:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

---

# 🔌 Step 7 — Ensure Import

Check `src/main.tsx`:

```ts
import './index.css'
```

👉 Already present in most cases

---

# 🧪 Step 8 — Test Tailwind

Update `App.tsx`:

```tsx
function App() {
  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <h1 className="text-2xl font-semibold text-gray-800">
        WalletIQ Ready 🚀
      </h1>
    </div>
  )
}

export default App
```

---

# ▶️ Step 9 — Run Again

```bash
npm run dev
```

---

# ✅ Expected Output

* Centered UI
* Gray background
* Styled text

---

# 🧠 What you just learned (important)

You now know:

1. How to bootstrap React (Vite)
2. How Tailwind is wired
3. Where config lives
4. How CSS pipeline works

This is your **foundation**

---

# ⏭️ Next Step (don’t jump ahead yet)

After you confirm:

👉 “Working again”

We move to:

* Clean folder structure (very important for your backend mindset)
* Routing setup

---

## ⚠️ Important Rule

Do NOT skip verification.
If Tailwind breaks later, debugging becomes painful.

---

## 👉 Your move

Follow step-by-step and reply:

👉 **“Working again”** or paste error
