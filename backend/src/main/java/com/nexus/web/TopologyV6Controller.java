package com.nexus.web;
import com.nexus.service.Accounts;
import com.nexus.service.CampusTopologyV6;
import com.nexus.domain.Rules.Role;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;
@RestController @RequestMapping("/api/admin/topology-v6")
public class TopologyV6Controller {
    private final Accounts accounts; private final CampusTopologyV6 topology;
    public TopologyV6Controller(Accounts accounts,CampusTopologyV6 topology){this.accounts=accounts;this.topology=topology;}
    @PostMapping public Map<String,Object> apply(Principal p){accounts.require(accounts.current(p.getName()),Role.ADMINISTRATOR);return topology.apply();}
}
