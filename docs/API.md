# NEXUS V6.1 — API map

**Base:** same-origin `/api` using a session cookie. Obtain CSRF token/header via `GET /api/auth/csrf`; include it on state-changing requests. Server-side role/ownership checks apply.

| Area | Example endpoints | Authorization |
| --- | --- | --- |
| Authentication | `GET /auth/csrf`, `POST /auth/register`, `POST /auth/login`, `GET /auth/me`, `POST /auth/logout` | Login/register public; account-specific endpoints require session as appropriate |
| Catalog | `GET /catalog` | Authenticated |
| Reporting | `POST /incidents/preview`, `POST /incidents`, `GET /incidents`, `GET /incidents/{id}` | Employee creation, role-scoped visibility |
| Lifecycle | `POST /incidents/{id}/{action}` | Authorized actor based on action and ticket |
| Location verification | See `IncidentController` location-check method and resolution validation | Assigned technician where required |
| Smart assistance | `POST /ai/report` | Employee; opt-in consent and input limits; rules fallback |
| Notifications | `GET /notifications`, `POST /notifications/{id}/read` | Recipient only |
| Technician | `POST /availability` | Technician |
| Reporting | `GET /reports` | Role-scoped |
| Administration | `GET/POST /admin/users`, `PUT /admin/users/{id}`; `POST /admin/categories`; `GET/POST/PUT /admin/services...` | Administrator |
| SLA & assignment | `GET/PUT /admin/policies`, `GET/PUT /admin/strategy`, `GET /admin/decision/{id}` | Administrator |
| Operations | `GET /admin/graph`, `GET /admin/audit`, `GET /admin/outbox`, `GET /admin/activity` | Administrator |
| Campus topology | `POST /admin/topology-v6` | Administrator |

The incident action endpoint supports `acknowledge`, `start`, `resolve`, `verify`, `close`, `reopen`, `note`, `request-info`, `private-note`, `assign`, and `link`, subject to action-specific role and state restrictions.

## Reporting fields

The current incident submission accepts title, description, service/category identifiers, impact, urgency, and optional `locationCode` / `locationNote` fields. The location catalogue comes from `GET /catalog`. Client-side geolocation is a suggestion feature, not a substitute for server validation.

## Error handling

Common responses: `400` invalid request/transition, `401` unauthenticated, `403` forbidden, `404` missing resource, `409` conflict. Inspect controller DTOs and source for exact payloads rather than treating this overview as a generated OpenAPI schema.

See [architecture](architecture.md) and [current status](../STATUS.md).
