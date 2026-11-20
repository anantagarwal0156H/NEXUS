package com.nexus.service;

import com.nexus.domain.ITService;
import com.nexus.domain.Setting;
import com.nexus.domain.Rules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/** Additive, illustrative campus-access model. Never removes existing provider links. */
@Service
public class CampusTopologyV6 {
    private final IncidentService svc;
    private final boolean auto;
    public CampusTopologyV6(IncidentService svc, @Value("${NEXUS_V6_TOPOLOGY:false}") boolean auto) {this.svc=svc;this.auto=auto;}
    public static final String[][] LINKS={
        {"Electrical supply","Core network"},{"Electrical supply","Campus Wi-Fi"},
        {"Core network","Campus Wi-Fi"},{"Core network","Identity & access"},{"Core network","Database cluster"},
        {"Campus Wi-Fi","ERP portal"},{"Campus Wi-Fi","Employee applications"},{"Campus Wi-Fi","Enterprise email"},{"Campus Wi-Fi","Cloud storage"},{"Campus Wi-Fi","Payment gateway"},
        {"Identity & access","ERP portal"},{"Database cluster","ERP portal"},
        {"Electrical supply","Classroom equipment"},{"Campus Wi-Fi","Classroom equipment"},
        {"Electrical supply","Air conditioning"},{"Electrical supply","Water and plumbing"},
        {"Electrical supply","Hostel facilities"},{"Water and plumbing","Hostel facilities"},{"Air conditioning","Hostel facilities"},{"Campus Wi-Fi","Hostel facilities"}
    };
    @Transactional public Map<String,Object> apply(){return merge();}
    @EventListener(ApplicationReadyEvent.class) @Transactional public void startup(){if(auto){var result=merge();System.out.println("NEXUS illustrative campus topology: "+result);}}
    private Map<String,Object> merge(){
        var all=svc.services().findAll(); Map<String,ITService> byName=new HashMap<>(); Map<Long,Set<Long>> graph=new HashMap<>();
        for(var s:all){byName.put(s.name,s);for(Long p:s.providers)graph.computeIfAbsent(p,k->new HashSet<>()).add(s.id);}
        int added=0,existing=0; List<String> skipped=new ArrayList<>();
        for(var link:LINKS){var p=byName.get(link[0]);var t=byName.get(link[1]);String label=link[0]+" -> "+link[1];
            if(p==null||t==null){skipped.add(label+" (missing service)");continue;}
            if(t.providers.contains(p.id)){existing++;continue;}
            if(p.id.equals(t.id)||Rules.reachable(graph,t.id).contains(p.id)){skipped.add(label+" (cycle)");continue;}
            t.providers.add(p.id);graph.computeIfAbsent(p.id,k->new HashSet<>()).add(t.id);added++;
        }
        svc.settings().save(new Setting("v6-demo-topology","ILLUSTRATIVE_CAMPUS_ACCESS"));
        return Map.of("added",added,"existing",existing,"skipped",skipped,"model","ILLUSTRATIVE_CAMPUS_ACCESS");
    }
}
