# Health Insights

Private Spring Boot API for the HealthUnspoken internal review dashboard.

The API is designed to run on the VPS near the crawler MySQL databases. Vercel should call this API with a secret token. Browser clients should never connect directly to MySQL.

## First Endpoints

- `GET /health`
- `GET /api/comments/by-date?date=2026-06-09&database=both&limit=100`
- `GET /api/comments/yesterday?database=both&limit=100`
- `GET /api/comments/top-questions?date=2026-06-09&database=both&limit=50`

All `/api/**` endpoints require:

```text
X-Insights-Api-Key: <INSIGHTS_API_KEY>
```

`/health` is intentionally public for uptime checks.

## Local Run

```bash
cp .env.example .env
# fill .env
set -a && source .env && set +a
./mvnw spring-boot:run
```

Or with system Maven:

```bash
mvn spring-boot:run
```

## Required MySQL Indexes

Run on both `youtube` and `youtube_health_influencers` databases:

```sql
create index comments_fetched_at_idx on comments(fetched_at);
create index comments_video_id_idx on comments(video_id);
create index videos_fetched_at_idx on videos(fetched_at);
```

Use `create index if not exists` only if your MySQL version supports it.

## Environment

See `.env.example`.
