CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE public.vector_store
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content   TEXT NOT NULL,
    metadata  JSONB,
    embedding VECTOR(768)
);

CREATE INDEX vector_store_embedding_idx
    ON public.vector_store
        USING hnsw (embedding vector_cosine_ops);

CREATE INDEX vector_store_metadata_idx
    ON vector_store USING GIN (metadata);