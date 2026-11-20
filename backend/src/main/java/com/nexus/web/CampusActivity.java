package com.nexus.web;
import com.nexus.service.Accounts;
import com.nexus.domain.Rules.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class CampusActivity {
    private final Accounts accounts; private final JdbcTemplate jdbc;
    public CampusActivity(Accounts accounts,JdbcTemplate jdbc){this.accounts=accounts;this.jdbc=jdbc;}
    @GetMapping("/activity") public List<Map<String,Object>> activity(Principal p){
        accounts.require(accounts.current(p.getName()),Role.ADMINISTRATOR);
        return jdbc.queryForList("SELECT id, incident_id AS \"incidentId\", \"at\", action FROM audit_logs WHERE private_entry=false ORDER BY \"at\" DESC,id DESC LIMIT 15");
    }
}
