package com.nexus.service;
import com.nexus.domain.*;
import com.nexus.domain.Rules.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component public class CampusWorkforce {
    private final Accounts accounts; private final IncidentService svc; private final boolean enabled; private final String password;
    public CampusWorkforce(Accounts a,IncidentService s,@Value("${NEXUS_EXPAND_TECHNICIANS:false}") boolean enabled,@Value("${NEXUS_TECH_PASSWORD:}") String password){accounts=a;svc=s;this.enabled=enabled;this.password=password;}
    @EventListener(ApplicationReadyEvent.class) @Transactional public void expand(){
        if(!enabled)return;
        if(password.length()<10||password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)throw new IllegalArgumentException("Set NEXUS_TECH_PASSWORD to 10–72 bytes before workforce expansion");
        String[][] team={{"Network Support 03","NETWORK","HARDWARE"},{"Network Support 04","NETWORK","CLOUD"},{"Electrical Support 05","ELECTRICAL","HARDWARE"},{"Electrical Support 06","ELECTRICAL","HVAC"},{"Water Support 07","PLUMBING","FACILITIES"},{"Water Support 08","PLUMBING","FACILITIES"},{"Software Support 09","SOFTWARE","APPLICATION","AUTHENTICATION"},{"Systems Support 10","DATABASE","STORAGE","CLOUD"},{"Facilities Support 11","FACILITIES","HVAC","HARDWARE"},{"Security Support 12","SECURITY","AUTHENTICATION"}};
        for(int i=0;i<team.length;i++){String email="tech"+(i+3)+"@nexus.demo";if(accounts.repo.findByEmail(email).isPresent())continue;Technician t=(Technician)accounts.register("[Demo] "+team[i][0],email,password,Role.TECHNICIAN);t.capacity=8;for(int k=1;k<team[i].length;k++)t.skills.put(team[i][k],5);}
    }
}
