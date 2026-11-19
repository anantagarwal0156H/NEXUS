# API overview

Same-origin JSON and session cookie. GET /api/auth/csrf yields token/header; send that header on every write. POST /api/auth/register {name,email,password}; POST /api/auth/login {email,password}; GET /api/auth/me; POST /api/auth/logout. Public registration always EMPLOYEE.

GET /api/catalog; GET /api/incidents?q=&status=&priority=&category=&service=&technician=&sla=&from=&to=; POST /api/incidents/preview and POST /api/incidents {title,description,serviceId,categoryId,impact,urgency}; GET /api/incidents/{id}; POST /api/incidents/{id}/{action} with optional {text,rating,technicianId,relatedId}. Actions: acknowledge, start, resolve, verify, close, reopen, note, request-info, private-note, assign, link. Role/ownership enforced server-side. GET /api/reports, /api/notifications; POST /api/notifications/{id}/read; POST /api/availability {available}.

Admin only: GET/POST /api/admin/users; PUT /api/admin/users/{id}; POST /api/admin/categories; POST /api/admin/services; PUT /api/admin/services/{id}; GET/PUT /api/admin/policies; GET/PUT /api/admin/strategy; GET /api/admin/decision/{id}?strategy=SKILL; GET /api/admin/graph, /api/admin/audit, /api/admin/outbox. See DTO records in controllers for exact request fields. Errors: 400 validation/transition, 401 unauthenticated/invalid login, 403 forbidden, 404 missing, 409 conflicting data.
