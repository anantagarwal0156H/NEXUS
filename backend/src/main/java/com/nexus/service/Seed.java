package com.nexus.service;
import com.nexus.domain.*;import com.nexus.domain.Rules.*;import org.springframework.beans.factory.annotation.Value;import org.springframework.boot.ApplicationArguments;import org.springframework.boot.ApplicationRunner;import org.springframework.stereotype.Component;import org.springframework.transaction.annotation.Transactional;import java.util.*;
@Component public class Seed implements ApplicationRunner{
 private final Accounts accounts;private final IncidentService svc;private final boolean demo;private final String password,adminPassword;
 public Seed(Accounts a,IncidentService s,@Value("${nexus.demo}") boolean d,@Value("${nexus.demo-password}") String p,@Value("${nexus.admin-password}") String ap){accounts=a;svc=s;demo=d;password=p;adminPassword=ap;}
 @Override @Transactional public void run(ApplicationArguments args){if(!svc.settings().existsById("assignment"))svc.settings().save(new Setting("assignment","SKILL"));for(Priority p:Priority.values())if(!svc.policies().existsById(SlaPolicy.key(null,p)))svc.policies().save(new SlaPolicy(null,p,Rules.defaultTarget(p)));for(String c:List.of("HARDWARE","SOFTWARE","NETWORK","DATABASE","CLOUD","SECURITY","AUTHENTICATION","APPLICATION","STORAGE"))if(svc.categories().findAll().stream().noneMatch(x -> x.name.equals(c))) svc.categories().save(new Category(c));if(svc.services().count()==0){ITService network=svc.services().save(new ITService("Core network",3));ITService auth=svc.services().save(new ITService("Identity & access",3));auth.providers.add(network.id);ITService db=svc.services().save(new ITService("Database cluster",3));db.providers.add(network.id);for(String name:List.of("Enterprise email","ERP portal","Payment gateway","Cloud storage","Employee applications")){ITService s=svc.services().save(new ITService(name,name.equals("Payment gateway")?3:2));s.providers.add(auth.id);if(!name.equals("Enterprise email"))s.providers.add(db.id);}}
 if(demo){if(password.length()<10)throw new IllegalStateException("Set DEMO_PASSWORD (10+ characters) for demo mode");if(accounts.repo.findByEmail("admin@nexus.demo").isEmpty())accounts.register("Demo administrator","admin@nexus.demo",password,Role.ADMINISTRATOR);if(accounts.repo.findByEmail("employee@nexus.demo").isEmpty())accounts.register("Demo employee","employee@nexus.demo",password,Role.EMPLOYEE);if(accounts.repo.findByEmail("employee2@nexus.demo").isEmpty())accounts.register("Demo employee 2","employee2@nexus.demo",password,Role.EMPLOYEE);if(accounts.repo.findByEmail("tech@nexus.demo").isEmpty()){Technician t=(Technician)accounts.register("Alex Morgan","tech@nexus.demo",password,Role.TECHNICIAN);for(Category c:svc.categories().findAll())t.skills.put(c.name,5);}if(accounts.repo.findByEmail("tech2@nexus.demo").isEmpty()){Technician t=(Technician)accounts.register("Jordan Lee","tech2@nexus.demo",password,Role.TECHNICIAN);for(Category c:svc.categories().findAll())t.skills.put(c.name,3);}}
 else if(accounts.repo.count()==0&&!adminPassword.isBlank())accounts.register("System administrator","admin@nexus.local",adminPassword,Role.ADMINISTRATOR);
    if(demo && Boolean.parseBoolean(
        System.getenv().getOrDefault("NEXUS_SEED_HISTORY", "false"))) {
        DemoHistory.populate(
            accounts.repo,
            svc.incidents(),
            svc.services(),
            svc.categories(),
            svc.audit(),
            svc.settings()
        );
    }

 }
}
