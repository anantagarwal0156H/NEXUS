package com.nexus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;

@Component
public class HistoryImporter {

    private final JdbcTemplate jdbc;

    @Value("${NEXUS_IMPORT_HISTORY:false}")
    private boolean enabled;

    @Value("${nexus.demo:false}")
    private boolean demo;

    public HistoryImporter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void importHistory() {

        if (!enabled || !demo) return;

        int marker = jdbc.update("""
            INSERT INTO nexus_settings (id, setting_value)
            VALUES ('demo-history-v3', 'imported')
            ON CONFLICT (id) DO NOTHING
        """);

        if (marker == 0) {
            System.out.println("NEXUS history already imported.");
            return;
        }

        jdbc.update("""
            INSERT INTO incident_categories (name)
            VALUES ('HARDWARE'), ('SOFTWARE'), ('NETWORK'),
                   ('DATABASE'), ('CLOUD'), ('SECURITY'),
                   ('AUTHENTICATION'), ('APPLICATION'), ('STORAGE')
            ON CONFLICT (name) DO NOTHING
        """);

        try {
            String sql = new ClassPathResource("demo/history-v3.sql")
                .getContentAsString(StandardCharsets.UTF_8);

            var result = jdbc.queryForMap(sql);

            int incidents =
                ((Number) result.get("incident_count")).intValue();

            int events =
                ((Number) result.get("event_count")).intValue();

            if (incidents != 240) {
                throw new IllegalStateException(
                    "Expected 240 demo incidents but generated "
                    + incidents
                    + ". Check seeded services and accounts."
                );
            }

            System.out.println("==============================");
            System.out.println("NEXUS HISTORICAL DATA IMPORTED");
            System.out.println("Incidents: " + incidents);
            System.out.println("Audit events: " + events);
            System.out.println("==============================");

        } catch (Exception ex) {
            throw new IllegalStateException(
                "Historical import failed. Changes rolled back.", ex
            );
        }
    }
}
