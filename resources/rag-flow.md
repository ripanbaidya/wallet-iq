# RAG Flow

## What is RAG?

RAG = **Retrieval Augmented Generation**

LLMs like GPT are powerful but they **don't know your private data**. RAG solves this by:

1. **Retrieving** relevant data from YOUR database
2. **Giving it to the LLM** as context
3. LLM **generates** an answer based on that context

Think of it like an **open book exam** — the LLM is the student, your expense data is the book.

## The Complete RAG Flow

```
User Question: "How much did I spend on food last month?"
        │
        ▼
┌───────────────────────────────────────────────────────┐
│  STEP 1 — Convert question to embedding               │
│                                                       │
│  "How much did I spend on food last month?"           │
│                    ↓ OpenAI Embedding Model           │
│  [0.021, -0.810, 0.339, ...]                          │
└───────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────┐
│  STEP 2 — Search PgVector for similar transactions    │
│                                                       │
│  Find top 10 vectors closest to query embedding       │
│  + filter where userId = currentUser                  │
│                                                       │
│  Returns:                                             │
│  - "Spent 450 on food via card on 2025-03-01"         │
│  - "Spent 200 on food via cash on 2025-03-05"         │
│  - "Spent 800 on groceries via card on 2025-03-10"    │
│  - ... (top 10 matches)                               │
└───────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────┐
│  STEP 3 — Load conversation history from PostgreSQL   │
│                                                       │
│  Last 5 messages of this chat session                 │
│  (so LLM understands context of conversation)         │
└───────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────┐
│  STEP 4 — Build prompt and send to GPT                │
│                                                       │
│  System: You are a finance assistant. Answer ONLY     │
│          based on the expense data below.             │
│                                                       │
│  Expense Data (from Step 2):                          │
│  - Spent 450 on food via card on 2025-03-01           │
│  - Spent 200 on food via cash on 2025-03-05           │
│  - Spent 800 on groceries via card on 2025-03-10      │
│  - ...                                                │
│                                                       │
│  Conversation History (from Step 3):                  │
│  - User: ...                                          │
│  - Assistant: ...                                     │
│                                                       │
│  User Question: How much did I spend on food?         │
└───────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────┐
│  STEP 5 — GPT generates answer                        │
│                                                       │
│  "You spent ₹1,450 on food last month across          │
│   3 transactions — ₹450 on Mar 1, ₹200 on Mar 5,     │
│   and ₹800 on groceries on Mar 10."                   │
└───────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────┐
│  STEP 6 — Save both messages to PostgreSQL            │
│                                                       │
│  ChatMessage (USER):      "How much did I spend..."   │
│  ChatMessage (ASSISTANT): "You spent ₹1,450..."       │
└───────────────────────────────────────────────────────┘
        │
        ▼
   Return answer to user
```

---

## When Does Embedding Happen?

Embedding is **not at query time for transactions** — it happens when you **save/update/delete** a transaction.

```
CREATE transaction
    │
    ├── 1. Save to PostgreSQL
    ├── 2. Build text: "Spent 450 on food via card on 2025-03-01. Note: lunch"
    ├── 3. Send text to OpenAI → get embedding vector
    ├── 4. Store text + vector in PgVector → get back vectorId
    └── 5. Save vectorId into transactions.embedding_id in PostgreSQL

UPDATE transaction
    │
    ├── 1. Update PostgreSQL row
    ├── 2. Delete OLD vector from PgVector using embedding_id
    ├── 3. Re-embed updated text
    ├── 4. Store new vector in PgVector → get new vectorId
    └── 5. Update transactions.embedding_id with new vectorId

DELETE transaction
    │
    ├── 1. Delete vector from PgVector using embedding_id
    └── 2. Delete row from PostgreSQL
```

---

## Summary in One Diagram

```
                    WRITE PATH
                    (on save/update/delete)

  User saves transaction
         │
         ├──────────────────────────────────┐
         ▼                                  ▼
  PostgreSQL                           PgVector
  (source of truth)                    (search index)
  stores real data                     stores text + vector
         │                                  │
         └──── embeddingId links ───────────┘
               these two together


                    READ PATH
                    (on user question)

  User asks question
         │
         ▼
  Embed the question
         │
         ▼
  Search PgVector         ← finds semantically similar transactions
         │
         ▼
  Build prompt with results
         │
         ▼
  Send to GPT
         │
         ▼
  Return natural language answer
```

---

## One Line Summary of Each Concept

| Concept | What it is |
|---------|-----------|
| **Embedding** | Converting text into numbers that capture meaning |
| **PgVector** | PostgreSQL extension that stores and searches these numbers |
| **embeddingId** | The ID that links a transaction row to its vector in PgVector |
| **RAG** | Fetch relevant data → give it to LLM → get a grounded answer |
| **Retrieval** | The step where you search PgVector with the user's question |
| **Augmented** | The prompt is augmented/enriched with retrieved data |
| **Generation** | GPT generates the final natural language answer |