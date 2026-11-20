package com.nexus.service;

import com.nexus.domain.*;
import com.nexus.domain.Rules.*;
import com.nexus.data.NoticeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

/** Explicitly enabled fictional history, without publishing email events. */
@Component public class DemoRefinement {
    private final Accounts accounts; private final IncidentService svc; private final NoticeRepository notices;
    private final boolean seed, topology; private final String password; private final Clock clock;
    public DemoRefinement(Accounts a, IncidentService s, NoticeRepository n, Clock c,
        @Value("${NEXUS_V6_DEMO:${NEXUS_V5_DEMO:false}}") boolean seed,
        @Value("${NEXUS_V5_TOPOLOGY:false}") boolean topology,
        @Value("${NEXUS_V6_DEMO_PASSWORD:${NEXUS_V5_DEMO_PASSWORD:}}") String password){this.accounts=a;this.svc=s;this.notices=n;this.clock=c;this.seed=seed;this.topology=topology;this.password=password;}
    private static final String[][] LINKS={{"Electrical supply","Core network"},{"Electrical supply","Campus Wi-Fi"},{"Core network","Campus Wi-Fi"},{"Electrical supply","Classroom equipment"},{"Campus Wi-Fi","Classroom equipment"},{"Electrical supply","Air conditioning"},{"Electrical supply","Hostel facilities"},{"Water and plumbing","Hostel facilities"},{"Air conditioning","Hostel facilities"},{"Campus Wi-Fi","Hostel facilities"}};
    private static final String[][] CASES={
        {"Campus Wi-Fi","NETWORK","Library Wi-Fi disconnects","Access point drops connections when several students join.","Reconfigured access point and confirmed stable connections.","library"},
        {"Campus Wi-Fi","NETWORK","Hostel access point unavailable","Devices cannot obtain an IP address in the corridor.","Restored uplink and verified DHCP leases.","vk"},
        {"Campus Wi-Fi","NETWORK","Slow Wi-Fi during evening study","High latency and intermittent page timeouts.","Adjusted channel allocation and tested throughput.","gautam"},
        {"Classroom equipment","HARDWARE","Projector shows no signal","Presentation source is connected but the display is blank.","Replaced cable and tested projector input.","new-acad"},
        {"Classroom equipment","HARDWARE","Lecture microphone has static","Microphone output is distorted during class.","Replaced faulty audio connector and tested output.","auditorium"},
        {"Classroom equipment","HARDWARE","Lab display intermittently flickers","Screen flickers when switching inputs.","Secured connector and tested multiple input sources.","library"},
        {"Air conditioning","HVAC","Cooling not working in study area","Room remains warm despite the thermostat setting.","Cleaned filters and checked thermostat operation.","library"},
        {"Air conditioning","HVAC","Hostel AC makes unusual noise","Indoor fan vibrates during operation.","Inspected mounting and serviced fan assembly.","gandhi"},
        {"Air conditioning","HVAC","Seminar AC airflow weak","Airflow is reduced across the seating area.","Cleaned return vent and confirmed airflow.","auditorium"},
        {"Electrical supply","ELECTRICAL","Study room socket unavailable","A wall outlet does not supply power.","Qualified staff isolated the circuit and replaced the faulty outlet.","library"},
        {"Water and plumbing","PLUMBING","Water dispenser leaking","Water accumulates beneath dispenser.","Replaced seal and checked for leaks.","mess1"},
        {"Hostel facilities","FACILITIES","Corridor fixture loose","A corridor fitting needs inspection.","Secured fixture and inspected mounting.","meera"},
        {"ERP portal","APPLICATION","ERP course registration fails","The academic portal returns an error while saving a registration.","Checked application logs and restored access in the fictional scenario.","library"},
        {"Employee applications","APPLICATION","Campus app sign-in loops","The application returns to the sign-in screen after authentication.","Corrected session configuration and validated sign-in.","new-acad"},
        {"Core network","NETWORK","Academic network segment unavailable","Several lab devices cannot reach internal services.","Validated uplink and restored network connectivity.","new-acad"},
        {"Identity & access","AUTHENTICATION","Campus account authentication error","Authentication fails for an otherwise valid test account.","Checked identity provider configuration and confirmed test access.","library"}
    };
    @EventListener(ApplicationReadyEvent.class) @Transactional public void run(){
        if(!seed&&!topology)return;
        if(seed&&!svc.settings().existsById("v6-demo-history")&&(password.length()<10||password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72))throw new IllegalArgumentException("Set valid NEXUS_V6_DEMO_PASSWORD before importing fictional data");
        Map<String,ITService> services=new HashMap<>();for(var s:svc.services().findAll())services.put(s.name,s);
        if(topology&&!svc.settings().existsById("v5-demo-topology")){
            Map<Long,Set<Long>> graph=new HashMap<>();for(var s:services.values())for(var p:s.providers)graph.computeIfAbsent(p,z->new HashSet<>()).add(s.id);
            for(var pair:LINKS){var p=services.get(pair[0]);var t=services.get(pair[1]);if(p==null||t==null)throw new IllegalStateException("Missing campus service: "+Arrays.toString(pair));if(p.id.equals(t.id)||Rules.reachable(graph,t.id).contains(p.id))throw new IllegalStateException("Proposed campus topology would create a cycle");graph.computeIfAbsent(p.id,z->new HashSet<>()).add(t.id);t.providers.add(p.id);}
            svc.settings().save(new Setting("v5-demo-topology","ILLUSTRATIVE_NOT_SURVEYED"));
        }
        if(!seed||svc.settings().existsById("v6-demo-history"))return;
        boolean prior=svc.settings().existsById("v5-demo-history");
        Map<String,Category> categories=new HashMap<>();for(var c:svc.categories().findAll())categories.put(c.name,c);
        var students=new ArrayList<Account>();String[] names={"Aarav Sen","Maya Rao","Neel Das","Isha Mehta","Kabir Shah","Diya Khanna","Rohan Nair","Tara Bose","Dev Iyer","Anika Jain"};
        for(int i=0;i<names.length;i++){String email="student"+(i+1)+"@nexus.demo";var a=accounts.repo.findByEmail(email).orElse(null);if(a==null)a=accounts.register("[Demo] "+names[i],email,password,Role.EMPLOYEE);if(a.role()!=Role.EMPLOYEE)throw new IllegalStateException("Demo student email already belongs to another role");if(!a.name.startsWith("[Demo]"))throw new IllegalStateException("Existing account is not marked fictional: "+email);students.add(a);}
        String[][] techs={{"Network specialist","NETWORK","APPLICATION","AUTHENTICATION","DATABASE","SOFTWARE","CLOUD"},{"Classroom AV and applications specialist","HARDWARE","APPLICATION","SOFTWARE"},{"HVAC specialist","HVAC"},{"Electrical specialist","ELECTRICAL"},{"Campus facilities specialist","PLUMBING","FACILITIES"}};
        List<Technician> team=new ArrayList<>();for(int i=0;i<techs.length;i++){String email="campus.tech"+(i+1)+"@nexus.demo";var a=accounts.repo.findByEmail(email).orElse(null);if(a==null){a=accounts.register("[Demo] "+techs[i][0],email,password,Role.TECHNICIAN);Technician t=(Technician)a;t.capacity=8;for(int k=1;k<techs[i].length;k++)t.skills.put(techs[i][k],5);}if(!(a instanceof Technician t))throw new IllegalStateException("Demo technician email already belongs to another role");if(!t.name.startsWith("[Demo]"))throw new IllegalStateException("Existing account is not marked fictional: "+email);for(int k=1;k<techs[i].length;k++)t.skills.putIfAbsent(techs[i][k],5);if(i<2)t.capacity=Math.max(t.capacity,16);team.add(t);}
        Map<Long,Long> load=new HashMap<>();for(Incident old:svc.incidents().findAll())if(old.status().active()&&old.technician!=null)load.merge(old.technician.id,1L,Long::sum);
        Instant now=clock.instant();int total=0;
        for(int k=prior?12:0;k<CASES.length;k++)for(int j=0;j<6;j++){
            String[] x=CASES[k];ITService service=services.get(x[0]);Category category=categories.get(x[1]);if(service==null||category==null)throw new IllegalStateException("Incomplete campus catalogue: "+x[0]);
            Technician t=team.stream().filter(a->a.skills.containsKey(x[1])).min(Comparator.comparingDouble(a->(double)load.getOrDefault(a.id,0L)/Math.max(1,a.capacity))).orElseThrow();Account student=students.get((k+j*3)%students.size());
            boolean open=j==0||j==1;if(open)load.merge(t.id,1L,Long::sum);int impact=(k+j)%3+1,urgency=(k+2*j)%3+1;Target target=svc.target(service,Rules.priority(impact,urgency,service.criticality));
            Instant start=open?now.minusSeconds((j==0?Math.max(1,target.responseMinutes()/2):target.responseMinutes()+5)*60L):now.minus(Duration.ofDays(2+(k*3+j*7)%39)).minusSeconds(k*1200L+j*300L);String title="[Demo] Campus "+(k<12?"V5":"V6")+" / "+x[2]+" / "+(j+1);
            Incident in=svc.incidents().save(new Incident(title,"Fictional demo record. "+x[3],student,service,category,impact,urgency,start,target));in.locationCode=CampusCatalog.require(x[5]).code();in.locationNote="Fictional demo location";
            audit(in,start,student.name,"CREATED",null,Status.NEW,in.title);in.move(Status.CLASSIFIED);audit(in,start,"SYSTEM","CLASSIFIED",Status.NEW,Status.CLASSIFIED,category.name);in.assign(t);audit(in,start,"SYSTEM","ASSIGNED",Status.CLASSIFIED,Status.ASSIGNED,"Fictional specialist assignment: "+t.name);
            if(j!=0){Instant ack=start.plusSeconds((j%4==0?target.responseMinutes()+2:Math.max(1,target.responseMinutes()/2))*60L);in.acknowledge(ack);audit(in,ack,t.name,"ACKNOWLEDGE",Status.ASSIGNED,Status.ACKNOWLEDGED,"Acknowledged request");in.move(Status.IN_PROGRESS);audit(in,ack,t.name,"START",Status.ACKNOWLEDGED,Status.IN_PROGRESS,"Started investigation");if(!open){Instant resolved=start.plusSeconds((j%4==0?target.resolutionMinutes()+15:Math.max(target.responseMinutes()+3,target.resolutionMinutes()/2))*60L);if(resolved.isBefore(ack))resolved=ack.plusSeconds(60);in.resolve(x[4],resolved);audit(in,resolved,t.name,"RESOLVE",Status.IN_PROGRESS,Status.RESOLVED,x[4]);if(j>=3){in.verify("Fictional feedback: confirmed service working.",j==4?4:5,resolved.plusSeconds(600));audit(in,in.verifiedAt,student.name,"VERIFY",Status.RESOLVED,Status.VERIFIED,in.feedback);}if(j>=4){in.close(in.verifiedAt.plusSeconds(120));audit(in,in.closedAt,student.name,"CLOSE",Status.VERIFIED,Status.CLOSED,"Fictional verification complete");}}}
            in.responseBreach=Rules.sla(now,in.responseDue,in.acknowledgedAt,in.warningMinutes)==Sla.BREACHED;in.resolutionBreach=Rules.sla(now,in.resolutionDue,in.resolvedAt,in.warningMinutes)==Sla.BREACHED;in.responseWarning=Rules.sla(now,in.responseDue,in.acknowledgedAt,in.warningMinutes)==Sla.WARNING;in.resolutionWarning=Rules.sla(now,in.resolutionDue,in.resolvedAt,in.warningMinutes)==Sla.WARNING;if(in.priority==Priority.P1&&in.responseBreach){in.escalated=true;audit(in,in.responseDue,"SYSTEM","ESCALATED",null,null,"Fictional critical incident escalation");}if(in.responseBreach)audit(in,in.responseDue,"SYSTEM","RESPONSE_BREACH",null,null,"Fictional SLA response target missed");if(in.resolutionBreach)audit(in,in.resolutionDue,"SYSTEM","RESOLUTION_BREACH",null,null,"Fictional SLA resolution target missed");
            notices.save(new Notice(student.id,in.id,start,"IN_APP","SENT","[Demo] "+title+" was recorded; this is a fictional notification."));total++;
        }
        if(!prior)svc.settings().save(new Setting("v5-demo-history","COMPLETE_72"));svc.settings().save(new Setting("v6-demo-history","COMPLETE_"+total));System.out.println("NEXUS fictional campus history imported: "+total+" records; no outbound emails generated.");
    }
    private void audit(Incident i,Instant at,String actor,String action,Status old,Status next,String detail){svc.audit().save(new Audit(i.id,at,actor,action,old==null?null:old.name(),next==null?null:next.name(),detail,false));}
}
