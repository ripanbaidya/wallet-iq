# WalletIQ — Complete Production Deployment Guide

## Prerequisites Checklist

Before starting, confirm you have:
- [ ] Supabase project created (free tier is fine)
- [ ] Upstash Redis database created
- [ ] OpenAI account with an API key
- [ ] Gmail account with App Password set up
- [ ] GitHub account with your code pushed to a repo
- [ ] Render account (render.com — free)
- [ ] Vercel account (vercel.com — free)

---

## Step 0 — Get Your Service Credentials

### Supabase (PostgreSQL)
1. Go to supabase.com → Your Project
2. Navigate to: **Settings → Database**
3. Scroll to "Connection parameters" — choose **Session mode (port 5432)**
    - DB_HOST     = `db.xxxxxxxxxxxx.supabase.co`
    - DB_PORT     = `5432`
    - DB_NAME     = `postgres`
    - DB_USERNAME = `postgres`
    - DB_PASSWORD = your Supabase password
4. ⚠️ Important: Use port **5432** (session mode), NOT 6543 (transaction mode).
   Spring Boot / Hibernate needs persistent connections.

### Upstash Redis
1. Go to console.upstash.com → Your Database
2. Click on the database name
3. Under "Connection Details" find the **Redis URL**
    - It starts with: `rediss://default:xxxx@xxx.upstash.io:6379`
    - Copy the entire URL → this is your REDIS_URL

### OpenAI
1. Go to platform.openai.com → API Keys
2. Create a new secret key
3. Copy it (you won't see it again) → this is SPRING_AI_OPENAI_API_KEY
4. ⚠️ Add $5-10 credit — free tier has very limited usage

### Gmail App Password
1. Go to myaccount.google.com
2. Security → 2-Step Verification (must be ON)
3. Security → App passwords
4. Select "Mail" + "Other device" → name it "WalletIQ"
5. Google generates a 16-char password → this is MAIL_PASSWORD
6. MAIL_USERNAME = your full Gmail address

---

## Step 1 — Prepare Your Git Repository

### 1.1 File placement
Ensure these files are in your repo (all provided above):

```
your-repo/
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile                    ← ADD THIS
│   └── src/main/resources/
│       ├── application.yaml
│       ├── application-dev.yaml
│       └── application-prod.yaml     ← REPLACE WITH NEW VERSION
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vercel.json                   ← ADD THIS (in frontend/ folder)
├── render.yaml                       ← ADD THIS (repo root)
└── .github/
    └── workflows/
        ├── backend-deploy.yml        ← ADD THIS
        └── frontend-deploy.yml       ← ADD THIS
```

### 1.2 Update application-prod.yaml
Replace your existing `backend/src/main/resources/application-prod.yaml` with the new
version that includes OpenAI configuration instead of Ollama.

Key differences from your current prod yaml:
- Remove `spring.ai.ollama` section
- Add `spring.ai.openai` section
- Add `spring.vectorstore.pgvector.dimensions: 1536` (OpenAI uses 1536, not 768)
- Add CORS env var: `allowed-origins: ${CORS_ALLOWED_ORIGINS:https://walletiq.vercel.app}`

### 1.3 Update vector_store table dimensions
If your Supabase already has the vector_store table with 768 dimensions (from Ollama),
you need to recreate it for OpenAI's 1536 dimensions:

```sql
-- Run this in Supabase → SQL Editor
DROP INDEX IF EXISTS vector_store_embedding_idx;
ALTER TABLE vector_store DROP COLUMN IF EXISTS embedding;
ALTER TABLE vector_store ADD COLUMN embedding vector(1536);

CREATE INDEX vector_store_embedding_idx
ON vector_store
USING hnsw (embedding vector_cosine_ops);
```

If the table doesn't exist yet, Flyway will create it (but make sure your
V1__init_schema.sql uses `vector(1536)` not `vector(768)` before deploying).

### 1.4 Commit and push everything
```bash
git add .
git commit -m "chore: add production deployment config"
git push origin main
```

---

## Step 2 — Deploy Backend to Render

### 2.1 Create the Render service
1. Go to render.com → **New → Web Service**
2. Connect your GitHub account (first time)
3. Select your WalletIQ repository
4. Render will detect your `render.yaml` automatically

If it doesn't auto-detect render.yaml:
- Name: `walletiq-api`
- Runtime: **Docker**
- Dockerfile Path: `./backend/Dockerfile`
- Docker Context: `./backend`
- Branch: `main`

### 2.2 Set Environment Variables
In Render → Your Service → **Environment**:

| Key | Value | Notes |
|-----|-------|-------|
| SPRING_PROFILES_ACTIVE | prod | |
| SERVER_PORT | 8080 | |
| API_VERSION | /v1 | |
| DB_HOST | `db.xxxx.supabase.co` | From Supabase |
| DB_PORT | 5432 | |
| DB_NAME | postgres | |
| DB_USERNAME | postgres | |
| DB_PASSWORD | your-password | Mark as Secret |
| REDIS_URL | `rediss://default:xxx@xxx.upstash.io:6379` | From Upstash, mark as Secret |
| SPRING_AI_OPENAI_API_KEY | `sk-...` | From OpenAI, mark as Secret |
| OPENAI_CHAT_MODEL | gpt-4o-mini | |
| OPENAI_EMBEDDING_MODEL | text-embedding-3-small | |
| PGVECTOR_DIMENSIONS | 1536 | |
| MAIL_USERNAME | you@gmail.com | |
| MAIL_PASSWORD | 16-char app password | Mark as Secret |
| JWT_ACCESS_TOKEN_EXPIRY | 3600000 | |
| JWT_REFRESH_TOKEN_EXPIRY | 86400000 | |
| CORS_ALLOWED_ORIGINS | https://walletiq.vercel.app | Update after Vercel deploy |

⚠️ Click **"Mark as Secret"** for: DB_PASSWORD, REDIS_URL, SPRING_AI_OPENAI_API_KEY, MAIL_PASSWORD
These are encrypted at rest and hidden in logs.

### 2.3 Deploy
Click **"Create Web Service"** — Render will:
1. Pull your repo
2. Build the Docker image (takes 5-8 minutes first time)
3. Start the container
4. Show logs in real-time

### 2.4 Note your Render URL
Once deployed, Render gives you a URL like:
`https://walletiq-api.onrender.com`

Test it: open `https://walletiq-api.onrender.com/api/v1/actuator/health`
You should see: `{"status":"UP"}`

⚠️ Free Render services spin down after 15 minutes of inactivity. First request after sleep takes 30-60 seconds. This is a free tier limitation.

### 2.5 Get Deploy Hook for GitHub Actions
Render → Your Service → **Settings → Deploy Hook**
Copy the URL (it looks like: `https://api.render.com/deploy/srv-xxx?key=yyy`)

Go to: GitHub repo → **Settings → Secrets and variables → Actions → New repository secret**
- Name: `RENDER_DEPLOY_HOOK_URL`
- Value: paste the deploy hook URL

---

## Step 3 — Deploy Frontend to Vercel

### 3.1 Connect to Vercel
1. Go to vercel.com → **New Project**
2. Connect GitHub → Select your WalletIQ repo
3. Vercel will ask which directory is the frontend

Configure:
- **Root Directory**: `frontend` (click "Edit" if it shows the repo root)
- **Framework Preset**: Vite (auto-detected)
- **Build Command**: `npm run build` (auto-filled)
- **Output Directory**: `dist` (auto-filled)

### 3.2 Set Environment Variables
Before clicking Deploy, add environment variable:

| Key | Value |
|-----|-------|
| VITE_API_BASE_URL | https://walletiq-api.onrender.com/api/v1 |

Replace `walletiq-api` with your actual Render service name.

### 3.3 Deploy
Click **Deploy**. Vercel will:
1. Clone your repo
2. Run `npm ci`
3. Run `npm run build`
4. Deploy to CDN globally
5. Give you a URL like: `https://walletiq.vercel.app`

### 3.4 Update CORS in Render
Now that you have your Vercel URL:
1. Render → Your Service → Environment
2. Update `CORS_ALLOWED_ORIGINS` to your actual Vercel URL:
   `https://walletiq.vercel.app`
3. Render will automatically redeploy

---

## Step 4 — Set Up GitHub Actions Secrets

Go to: GitHub → Your Repo → **Settings → Secrets and variables → Actions**

Add these repository secrets:

| Secret Name | Value | Where to get it |
|-------------|-------|-----------------|
| RENDER_DEPLOY_HOOK_URL | `https://api.render.com/deploy/srv-xxx?key=yyy` | Render → Service → Settings → Deploy Hook |

That's the only secret GitHub Actions needs. Vercel deploys automatically via Git integration — no secrets needed.

---

## Step 5 — Custom Domain (Optional)

### Vercel custom domain
Vercel → Your Project → **Settings → Domains**
- Add your domain (e.g., `walletiq.com` or `app.walletiq.com`)
- Follow DNS instructions

### Render custom domain
Render → Your Service → **Settings → Custom Domains**
- Add your API domain (e.g., `api.walletiq.com`)
- Follow DNS instructions

If you add custom domains, update `CORS_ALLOWED_ORIGINS` in Render to include both:
`https://walletiq.vercel.app,https://app.walletiq.com`

---

## Step 6 — Verify Everything Works

### 6.1 Test backend health
```
GET https://walletiq-api.onrender.com/api/v1/actuator/health
→ { "status": "UP" }
```

### 6.2 Test backend API
```
POST https://walletiq-api.onrender.com/api/v1/auth/signup
Content-Type: application/json
{
  "fullName": "Test User",
  "email": "test@example.com",
  "password": "TestPass123"
}
→ 201 Created
```

### 6.3 Test frontend
1. Open your Vercel URL
2. Click "Get started" / Sign up
3. Complete registration
4. Verify email OTP works (check Gmail)
5. Log in → confirm dashboard loads

### 6.4 Test CORS
Open browser console on your Vercel frontend and check for CORS errors.
If you see CORS errors:
- Check `CORS_ALLOWED_ORIGINS` in Render matches your exact Vercel URL (no trailing slash)
- Ensure the Render service redeployed after the env var change

---

## Troubleshooting Common Issues

### "Database connection refused"
- Verify Supabase is using port 5432 (not 6543)
- Add `?sslmode=require` to the JDBC URL
- Check DB_HOST has no typos

### "Redis connection failed"
- REDIS_URL must start with `rediss://` (double-s, for SSL)
- Copy the exact URL from Upstash — don't manually construct it

### "Flyway migration failed"
- This usually means vector_store table has wrong dimensions
- Run the SQL in Step 1.3 in Supabase SQL Editor

### "OTP email not sending"
- MAIL_PASSWORD must be the App Password (16 chars, no spaces), not your Gmail password
- Make sure 2-Step Verification is ON in Google Account

### "OpenAI API error"
- Add billing credit to your OpenAI account
- Check the API key starts with `sk-proj-` or `sk-`

### "Free tier Render cold starts"
- Render free services sleep after 15min inactivity
- First request takes 30-60 seconds — this is expected
- Upgrade to Starter ($7/month) to avoid spin-down

---

## Environment Variables Quick Reference

### Render (Backend) — All Variables
```
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
API_VERSION=/v1
DB_HOST=db.xxxx.supabase.co
DB_PORT=5432
DB_NAME=postgres
DB_USERNAME=postgres
DB_PASSWORD=<secret>
REDIS_URL=rediss://default:xxx@xxx.upstash.io:6379
SPRING_AI_OPENAI_API_KEY=sk-...
OPENAI_CHAT_MODEL=gpt-4o-mini
OPENAI_EMBEDDING_MODEL=text-embedding-3-small
PGVECTOR_DIMENSIONS=1536
MAIL_USERNAME=you@gmail.com
MAIL_PASSWORD=<16-char-app-password>
JWT_ACCESS_TOKEN_EXPIRY=3600000
JWT_REFRESH_TOKEN_EXPIRY=86400000
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

### Vercel (Frontend) — All Variables
```
VITE_API_BASE_URL=https://walletiq-api.onrender.com/api/v1
```

### GitHub Secrets — For CI/CD
```
RENDER_DEPLOY_HOOK_URL=https://api.render.com/deploy/srv-xxx?key=yyy
```