# Frontend

# ============================================================
# Stage 1: Build React app
# ============================================================
FROM node:20-alpine AS builder

WORKDIR /app

# Accept build-time API URL
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Copy package files first — layer cache for node_modules
COPY frontend/package*.json ./
RUN npm ci

# Copy source and build
COPY frontend/ ./
RUN npm run build

# ============================================================
# Stage 2: Serve with Nginx
# ============================================================
FROM alpine
WORKDIR /app
COPY --from=builder /app/dist .