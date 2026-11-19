package com.nexus.service;

import com.nexus.data.*;
import com.nexus.domain.*;
import com.nexus.domain.Rules.*;
import java.time.*;
import java.util.*;

/**
 * Imports fictional historical incidents for demonstration.
 * Existing customer records are never deleted or overwritten.
 */
public final class DemoHistory {

    private DemoHistory() {}

    public static void populate(
            AccountRepository accounts,
            IncidentRepository incidents,
            ITServiceRepository services,
            CategoryRepository categories,
            AuditRepository audit,
            SettingRepository settings) {

        if (settings.existsById("demo-history-v2")) return;

        var staff = List.of(
            (Technician) accounts.findByEmail("tech@nexus.demo").orElseThrow(),
            (Technician) accounts.findByEmail("tech2@nexus.demo").orElseThrow()
        );

        var employees = List.of(
            accounts.findByEmail("employee@nexus.demo").orElseThrow(),
            accounts.findByEmail("employee2@nexus.demo").orElseThrow()
        );

        String[][] cases = {
            {"Core network", "NETWORK", "Intermittent network connectivity"},
            {"Identity & access", "AUTHENTICATION", "Unable to sign in"},
            {"Database cluster", "DATABASE", "Database response timeout"},
            {"Enterprise email", "SOFTWARE", "Email synchronization failed"},
            {"ERP portal", "APPLICATION", "ERP dashboard unavailable"},
            {"Payment gateway", "APPLICATION", "Payment processing interrupted"},
            {"Cloud storage", "STORAGE", "Shared files inaccessible"},
            {"Employee applications", "SOFTWARE", "Application freezes during startup"}
        };

        var availableServices = services.findAll();
        var availableCategories = categories.findAll();
        var random = new Random(213);
        Instant now = Instant.now();

        for (int n = 0; n < 30; n++) {

            String[] item = cases[n % cases.length];

            ITService service = availableServices.stream()
                .filter(s -> s.name.equals(item[0]))
                .findFirst().orElse(null);

            Category category = availableCategories.stream()
                .filter(c -> c.name.equals(item[1]))
                .findFirst().orElse(null);

            if (service == null || category == null) continue;

            boolean recent = n >= 24;

            int impact = recent ? 3 : 1 + random.nextInt(3);
            int urgency = recent ? 3 : 1 + random.nextInt(3);

            Instant created = recent
                ? now.minusSeconds((2L + (n - 24) * 9L) * 60)
                : now.minusSeconds((2L + random.nextInt(25)) * 86400
                    + random.nextInt(43200));

            Priority priority = Rules.priority(
                impact, urgency, service.criticality
            );

            Target target = Rules.defaultTarget(priority);

            Incident incident = incidents.save(new Incident(
                "[Demo] " + item[2],
                "Fictional demonstration incident involving "
                    + service.name + ". Users reported a disruption "
                    + "to normal organizational operations.",
                employees.get(n % employees.size()),
                service, category, impact, urgency, created, target
            ));

            entry(audit, incident, created, incident.reporter.name,
                "CREATED", null, "NEW", "Imported fictional demo report");

            incident.move(Status.CLASSIFIED);

            entry(audit, incident, created, "SYSTEM",
                "CLASSIFIED", "NEW", "CLASSIFIED", category.name);

            entry(audit, incident, created, "SYSTEM",
                "PRIORITY_CALCULATED", "CLASSIFIED", "CLASSIFIED",
                impact + " x " + urgency + " x "
                    + service.criticality + " = " + priority);

            Technician technician = staff.get(n % staff.size());

            long workload = incidents.findAll().stream()
                .filter(i -> i.status().active()
                    && i.technician != null
                    && technician.id.equals(i.technician.id))
                .count();

            if (recent && (!technician.available
                    || !technician.enabled
                    || workload >= technician.capacity)) {
                continue;
            }

            incident.assign(technician);

            entry(audit, incident, created, "DEMO IMPORT",
                "ASSIGNED", "CLASSIFIED", "ASSIGNED",
                "Historical demonstration assignment to " + technician.name);

            if (recent && n % 2 == 0) continue;

            Instant acknowledged = created.plusSeconds(
                Math.max(1, target.responseMinutes() / 2) * 60L
            );

            if (recent && acknowledged.isAfter(now)) continue;

            incident.acknowledge(acknowledged);

            entry(audit, incident, acknowledged, technician.name,
                "ACKNOWLEDGED", "ASSIGNED", "ACKNOWLEDGED",
                "Technician acknowledged demo report");

            incident.move(Status.IN_PROGRESS);

            entry(audit, incident, acknowledged, technician.name,
                "START", "ACKNOWLEDGED", "IN_PROGRESS",
                "Investigation started");

            if (recent) continue;

            Instant resolved = created.plusSeconds(
                target.resolutionMinutes() * 60L
                    * (n % 5 == 0 ? 2 : 1) / (n % 5 == 0 ? 1 : 2)
            );

            incident.resolve(
                "Service diagnostics completed and normal operation restored.",
                resolved
            );

            incident.responseBreach =
                acknowledged.isAfter(incident.responseDue);

            incident.resolutionBreach =
                resolved.isAfter(incident.resolutionDue);

            entry(audit, incident, resolved, technician.name,
                "RESOLVE", "IN_PROGRESS", "RESOLVED", incident.resolution);

            if (incident.resolutionBreach) {
                entry(audit, incident, incident.resolutionDue, "SYSTEM",
                    "RESOLUTION_BREACH", "IN_PROGRESS", "IN_PROGRESS",
                    "Historical demonstration SLA violation");
            }

            if (n % 4 != 0) {
                Instant verified = resolved.plusSeconds(120);

                incident.verify("Service working normally.", 5, verified);

                entry(audit, incident, verified, incident.reporter.name,
                    "VERIFY", "RESOLVED", "VERIFIED", "Resolution confirmed");

                incident.close(verified.plusSeconds(60));

                entry(audit, incident, verified.plusSeconds(60),
                    incident.reporter.name,
                    "CLOSE", "VERIFIED", "CLOSED", "Incident closed");
            }
        }

        settings.save(new Setting("demo-history-v2", "complete"));
    }

    private static void entry(
            AuditRepository audit, Incident incident, Instant at,
            String actor, String action, String oldState,
            String newState, String description) {

        audit.save(new Audit(
            incident.id, at, actor, action,
            oldState, newState, description, false
        ));
    }
}
