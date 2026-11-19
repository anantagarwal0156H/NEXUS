WITH templates(k, service_name, category_name, title, description, fix) AS (
    VALUES
    (0, 'Core network', 'NETWORK',
        'Intermittent network connectivity',
        'Multiple users experienced packet loss during business hours.',
        'Identified unstable network equipment and restored connectivity.'),

    (1, 'Identity & access', 'AUTHENTICATION',
        'Single sign-on authentication failure',
        'Employees could not authenticate using their organizational accounts.',
        'Corrected identity provider configuration and refreshed sessions.'),

    (2, 'Database cluster', 'DATABASE',
        'Database query performance degradation',
        'Long-running queries caused application response delays.',
        'Optimized affected queries and cleared blocked transactions.'),

    (3, 'Enterprise email', 'SOFTWARE',
        'Email synchronization failure',
        'Incoming messages were not synchronizing across devices.',
        'Restored synchronization service and verified message delivery.'),

    (4, 'ERP portal', 'APPLICATION',
        'ERP dashboard unavailable',
        'Employees reported blank screens while opening the ERP portal.',
        'Restarted application components and verified dashboard access.'),

    (5, 'Payment gateway', 'APPLICATION',
        'Payment transactions failing',
        'Payment requests were returning processing errors.',
        'Restored payment application connectivity and validated transactions.'),

    (6, 'Cloud storage', 'STORAGE',
        'Shared files inaccessible',
        'Employees could not access shared organizational documents.',
        'Restored storage permissions and verified file access.'),

    (7, 'Employee applications', 'SOFTWARE',
        'Application crashes during startup',
        'The application closed unexpectedly after launching.',
        'Corrected application configuration and validated startup.'),

    (8, 'Core network', 'HARDWARE',
        'Network switch malfunction',
        'A network equipment fault interrupted connectivity.',
        'Replaced faulty equipment and verified network stability.'),

    (9, 'Identity & access', 'SECURITY',
        'Suspicious authentication activity',
        'Repeated unsuccessful authentication attempts were reported.',
        'Reviewed authentication activity and secured affected accounts.'),

    (10, 'Cloud storage', 'CLOUD',
        'Cloud service synchronization delayed',
        'Cloud-hosted documents were not updating correctly.',
        'Restored service synchronization and checked data consistency.'),

    (11, 'Database cluster', 'DATABASE',
        'Database connection pool exhausted',
        'Applications could not establish additional database connections.',
        'Reconfigured connection pooling and released inactive connections.'),

    (12, 'Enterprise email', 'APPLICATION',
        'Outgoing messages delayed',
        'Messages remained queued without successful delivery.',
        'Cleared the delivery queue and verified outgoing messages.'),

    (13, 'ERP portal', 'AUTHENTICATION',
        'Session expiry errors',
        'Active users were being unexpectedly signed out.',
        'Corrected session configuration and verified authentication.'),

    (14, 'Payment gateway', 'NETWORK',
        'Payment service connection timeout',
        'The payment application could not reach an upstream service.',
        'Restored upstream connectivity and verified transactions.'),

    (15, 'Employee applications', 'HARDWARE',
        'Workstation peripheral unavailable',
        'Employees reported equipment connectivity problems.',
        'Reconfigured affected devices and verified functionality.')
),

source_rows AS (
    SELECT
        g.n,
        s.id AS service_id,
        s.criticality,
        c.id AS category_id,
        c.name AS category_name,
        e.id AS reporter_id,
        t.id AS candidate_id,
        t.available,
        t.enabled,
        t.capacity,

        ROW_NUMBER() OVER (
            PARTITION BY (g.n > 228), t.id
            ORDER BY g.n
        ) AS candidate_rank,

        (
            SELECT COUNT(*)
            FROM incidents existing
            WHERE existing.technician_id = t.id
              AND existing.status IN (
                'NEW','CLASSIFIED','ASSIGNED',
                'ACKNOWLEDGED','IN_PROGRESS'
              )
        ) AS existing_workload,

        EXISTS (
            SELECT 1 FROM technician_skills skill
            WHERE skill.account_id = t.id
              AND skill.category_name = c.name
              AND skill.skill_level > 0
        ) AS qualified,

        '[Demo] Archive / '
            || LPAD(g.n::text, 3, '0')
            || ' - ' || x.title AS title,

        '[Fictional historical record] ' || x.description
            AS description,

        x.fix,

        CASE
            WHEN g.n > 228 THEN
                CASE WHEN g.n % 4 = 0 THEN 2 ELSE 3 END
            ELSE 1 + (g.n * 7 + g.n / 9) % 3
        END AS impact,

        CASE
            WHEN g.n > 228 THEN 3
            ELSE 1 + (g.n * 11 + g.n / 5) % 3
        END AS urgency,

        CASE
            WHEN g.n > 228 THEN
                NOW()
                - (2 + g.n % 3) * INTERVAL '1 day'
                - (g.n % 20) * INTERVAL '1 hour'
            ELSE
                DATE_TRUNC('day', NOW())
                - (
                    3 + CASE
                        WHEN g.n % 5 < 3 THEN (g.n * 7) % 11
                        ELSE 11 + (g.n * 13) % 32
                    END
                ) * INTERVAL '1 day'
                + ((g.n * 7 + g.n / 5) % 24)
                    * INTERVAL '1 hour'
                + ((g.n * 17) % 60)
                    * INTERVAL '1 minute'
        END AS created_at

    FROM GENERATE_SERIES(1, 240) AS g(n)
    JOIN templates x ON x.k = (g.n - 1) % 16
    JOIN it_services s ON s.name = x.service_name
    JOIN incident_categories c ON c.name = x.category_name
    JOIN accounts e ON e.email = CASE
        WHEN g.n % 2 = 0 THEN 'employee@nexus.demo'
        ELSE 'employee2@nexus.demo'
    END
    JOIN accounts t ON t.email = CASE
        WHEN g.n % 2 = 0 THEN 'tech@nexus.demo'
        ELSE 'tech2@nexus.demo'
    END
),

prioritized AS (
    SELECT *,
        CASE
            WHEN impact * urgency * criticality >= 18 THEN 'P1'
            WHEN impact * urgency * criticality >= 9 THEN 'P2'
            WHEN impact * urgency * criticality >= 4 THEN 'P3'
            ELSE 'P4'
        END AS priority,

        CASE
            WHEN n <= 228 THEN candidate_id
            WHEN available AND enabled AND qualified
                AND n % 3 <> 0
                AND candidate_rank <= capacity - existing_workload
            THEN candidate_id
            ELSE NULL
        END AS technician_id
    FROM source_rows
),

targets AS (
    SELECT *,
        CASE priority
            WHEN 'P1' THEN 5
            WHEN 'P2' THEN 15
            WHEN 'P3' THEN 60
            ELSE 240
        END AS response_min,

        CASE priority
            WHEN 'P1' THEN 30
            WHEN 'P2' THEN 120
            WHEN 'P3' THEN 480
            ELSE 1440
        END AS resolution_min,

        CASE priority
            WHEN 'P1' THEN 2
            WHEN 'P2' THEN 5
            WHEN 'P3' THEN 15
            ELSE 60
        END AS warning_min,

        CASE
            WHEN n > 228 AND technician_id IS NULL
                THEN 'CLASSIFIED'
            WHEN n > 228 AND n % 2 = 0
                THEN 'IN_PROGRESS'
            WHEN n > 228 THEN 'ASSIGNED'
            WHEN n % 7 = 0 THEN 'RESOLVED'
            WHEN n % 11 = 0 THEN 'VERIFIED'
            ELSE 'CLOSED'
        END AS status

    FROM prioritized
),

timing AS (
    SELECT *,
        created_at + response_min * INTERVAL '1 minute'
            AS response_due,

        created_at + resolution_min * INTERVAL '1 minute'
            AS resolution_due,

        CASE
            WHEN status IN ('CLASSIFIED','ASSIGNED') THEN NULL
            ELSE created_at
                + response_min
                * CASE WHEN n % 11 = 0 THEN 1.6 ELSE 0.4 END
                * INTERVAL '1 minute'
        END AS acknowledged_at

    FROM targets
),

completed AS (
    SELECT *,
        CASE
            WHEN n > 228 THEN NULL
            ELSE GREATEST(
                created_at + resolution_min
                    * CASE WHEN n % 7 = 0 THEN 1.5 ELSE 0.5 END
                    * INTERVAL '1 minute',
                acknowledged_at + INTERVAL '2 minutes'
            )
        END AS resolved_at
    FROM timing
),

calculated AS (
    SELECT *,
        COALESCE(acknowledged_at > response_due, TRUE)
            AS response_late,

        COALESCE(resolved_at > resolution_due, TRUE)
            AS resolution_late,

        CASE
            WHEN status IN ('VERIFIED','CLOSED')
                THEN resolved_at + INTERVAL '30 minutes'
            ELSE NULL
        END AS verified_at,

        CASE
            WHEN status = 'CLOSED'
                THEN resolved_at + INTERVAL '40 minutes'
            ELSE NULL
        END AS closed_at
    FROM completed
),

inserted AS (
    INSERT INTO incidents (
        version, title, description,
        reporter_id, technician_id,
        service_id, category_id,
        impact, urgency, criticality,
        priority, status,
        warning_minutes,
        created_at, response_due, resolution_due,
        acknowledged_at, resolved_at, verified_at, closed_at,
        resolution, feedback, rating,
        response_warning, resolution_warning,
        response_breach, resolution_breach, escalated
    )
    SELECT
        0, title, description,
        reporter_id, technician_id,
        service_id, category_id,
        impact, urgency, criticality,
        priority, status,
        warning_min,
        created_at, response_due, resolution_due,
        acknowledged_at, resolved_at, verified_at, closed_at,

        CASE WHEN resolved_at IS NOT NULL THEN fix END,

        CASE WHEN verified_at IS NOT NULL
            THEN 'Service restored and verified by employee.'
        END,

        CASE WHEN verified_at IS NOT NULL
            THEN CASE WHEN resolution_late THEN 3 ELSE 5 END
        END,

        response_late, resolution_late,
        response_late, resolution_late,
        priority = 'P1' AND response_late

    FROM calculated
    ORDER BY n
    RETURNING *
),

audit_history AS (
    INSERT INTO audit_logs (
        incident_id, "at", actor, action,
        previous_state, new_state, detail, private_entry
    )
    SELECT
        i.id,
        ev.event_at,
        ev.actor,
        ev.action,
        ev.old_state,
        ev.new_state,
        ev.detail,
        FALSE

    FROM inserted i
    JOIN accounts e ON e.id = i.reporter_id
    LEFT JOIN accounts t ON t.id = i.technician_id

    CROSS JOIN LATERAL (
        VALUES

        (1, i.created_at,
            e.name, 'CREATED', NULL::text, 'NEW',
            'Fictional historical incident reported'),

        (2, i.created_at + INTERVAL '5 seconds',
            'SYSTEM', 'CLASSIFIED', 'NEW', 'CLASSIFIED',
            'Incident classified'),

        (3, i.created_at + INTERVAL '10 seconds',
            'SYSTEM', 'PRIORITY_CALCULATED',
            'CLASSIFIED', 'CLASSIFIED',
            'Priority score: '
                || i.impact || ' x ' || i.urgency
                || ' x ' || i.criticality
                || ' = ' || i.priority),

        (4,
            CASE WHEN i.technician_id IS NOT NULL
                THEN i.created_at + INTERVAL '20 seconds' END,
            'DEMO IMPORT', 'ASSIGNED', 'CLASSIFIED', 'ASSIGNED',
            'Historical assignment to ' || t.name),

        (5,
            CASE WHEN i.technician_id IS NULL
                THEN i.created_at + INTERVAL '20 seconds' END,
            'SYSTEM', 'NO_TECHNICIAN',
            'CLASSIFIED', 'CLASSIFIED',
            'Awaiting available qualified technician'),

        (6,
            CASE WHEN i.response_warning
                THEN i.response_due
                    - i.warning_minutes * INTERVAL '1 minute'
            END,
            'SYSTEM', 'RESPONSE_WARNING',
            CASE WHEN i.technician_id IS NULL
                THEN 'CLASSIFIED' ELSE 'ASSIGNED' END,
            CASE WHEN i.technician_id IS NULL
                THEN 'CLASSIFIED' ELSE 'ASSIGNED' END,
            'Response deadline approaching'),

        (7,
            CASE WHEN i.response_breach THEN i.response_due END,
            'SYSTEM', 'RESPONSE_BREACH',
            CASE WHEN i.technician_id IS NULL
                THEN 'CLASSIFIED' ELSE 'ASSIGNED' END,
            CASE WHEN i.technician_id IS NULL
                THEN 'CLASSIFIED' ELSE 'ASSIGNED' END,
            'Response SLA target exceeded'),

        (8,
            CASE WHEN i.escalated
                THEN i.response_due + INTERVAL '1 second' END,
            'SYSTEM', 'ESCALATED',
            CASE WHEN i.technician_id IS NULL
                THEN 'CLASSIFIED' ELSE 'ASSIGNED' END,
            CASE WHEN i.technician_id IS NULL
                THEN 'CLASSIFIED' ELSE 'ASSIGNED' END,
            'Critical incident escalated due to response SLA violation'),

        (9, i.acknowledged_at,
            t.name, 'ACKNOWLEDGED', 'ASSIGNED', 'ACKNOWLEDGED',
            'Technician acknowledged incident'),

        (10, i.acknowledged_at + INTERVAL '15 seconds',
            t.name, 'START', 'ACKNOWLEDGED', 'IN_PROGRESS',
            'Technical investigation initiated'),

        (11, i.acknowledged_at + INTERVAL '30 seconds',
            t.name, 'NOTE', 'IN_PROGRESS', 'IN_PROGRESS',
            'Reviewed service diagnostics and identified affected components'),

        (12,
            CASE WHEN i.resolution_warning
                THEN i.resolution_due
                    - i.warning_minutes * INTERVAL '1 minute'
            END,
            'SYSTEM', 'RESOLUTION_WARNING',
            CASE
                WHEN i.acknowledged_at IS NOT NULL THEN 'IN_PROGRESS'
                WHEN i.technician_id IS NOT NULL THEN 'ASSIGNED'
                ELSE 'CLASSIFIED'
            END,
            CASE
                WHEN i.acknowledged_at IS NOT NULL THEN 'IN_PROGRESS'
                WHEN i.technician_id IS NOT NULL THEN 'ASSIGNED'
                ELSE 'CLASSIFIED'
            END,
            'Resolution deadline approaching'),

        (13,
            CASE WHEN i.resolution_breach THEN i.resolution_due END,
            'SYSTEM', 'RESOLUTION_BREACH',
            CASE
                WHEN i.acknowledged_at IS NOT NULL THEN 'IN_PROGRESS'
                WHEN i.technician_id IS NOT NULL THEN 'ASSIGNED'
                ELSE 'CLASSIFIED'
            END,
            CASE
                WHEN i.acknowledged_at IS NOT NULL THEN 'IN_PROGRESS'
                WHEN i.technician_id IS NOT NULL THEN 'ASSIGNED'
                ELSE 'CLASSIFIED'
            END,
            'Resolution SLA target exceeded'),

        (14, i.resolved_at,
            t.name, 'RESOLVE', 'IN_PROGRESS', 'RESOLVED',
            i.resolution),

        (15, i.verified_at,
            e.name, 'VERIFY', 'RESOLVED', 'VERIFIED',
            'Employee confirmed service restoration'),

        (16, i.closed_at,
            e.name, 'CLOSE', 'VERIFIED', 'CLOSED',
            'Incident investigation completed')

    ) AS ev(
        seq, event_at, actor, action,
        old_state, new_state, detail
    )

    WHERE ev.event_at IS NOT NULL

    ORDER BY i.id, ev.event_at, ev.seq

    RETURNING id
)

SELECT
    (SELECT COUNT(*) FROM inserted) AS incident_count,
    (SELECT COUNT(*) FROM audit_history) AS event_count;
