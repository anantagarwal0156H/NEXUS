# NEXUS V6.1 — Live demonstration flow

**Audience:** OOP course evaluators. This is a guided scenario; do not claim a step was independently tested until actually demonstrated.

## 1. Opening and context

Show the campus-themed login and explain that the React interface talks to a Java Spring Boot API with PostgreSQL persistence. Briefly show Theme Centre presets if time permits.

## 2. Student / employee: create a report

1. Log in with a configured demo employee account (credentials provided privately).
2. Open reporting. Choose a service/category, impact, urgency and optional building/location details.
3. Explain how Java computes priority and previews related incidents.
4. Submit and record the incident ID, status and any technician assignment.
5. Open incident detail, notifications and the audit timeline. Emphasize the user's visibility is role-scoped.
6. If demonstrating smart assistance, explicitly identify it as **optional**, permission-based and able to fall back to local rules.

## 3. Technician: take ownership

1. Sign in as the assigned technician.
2. Open the assigned queue, acknowledge and start work.
3. Add a visible update or request information; contrast with a private technician note.
4. Resolve with a meaningful explanation. For location-tagged tickets requiring pilot site verification, use the application's legitimate location workflow; do not fake GPS readings.
5. Return to the student to verify and close, or explain the supported reopen path.

## 4. Administrator: operations and control

1. Open the administrator overview, Incident Centre and SLA Monitor as **separate workspaces**.
2. Show workload and incident trends, service graph/topology, and Decision Inspector.
3. Explain the distinction between saved service links, illustrative proposed campus links, and incident-derived associations.
4. Inspect audit entries and notifications.
5. Show administration of users, services/categories, policies, and assignment strategy. Avoid modifying important production data solely for the demo.
6. Explain that optional external email is distinct from the persistent simulated outbox.

## 5. Conclude with OOP

Point to `Account` inheritance, `Incident` state handling, `AssignmentStrategy` polymorphism, and the event listeners used for notifications/audit.

## Demonstration safeguards

- Use demo/test accounts and disposable incidents.
- Capture the current deployment URL and status before presenting; Render free services can cold-start.
- Don't imply NEXUS receives live campus network alerts, that AI is always enabled, or that all tests have recently passed.
- Source reference: [current status](../STATUS.md) and [viva code map](../TEAM_GUIDE.md).
