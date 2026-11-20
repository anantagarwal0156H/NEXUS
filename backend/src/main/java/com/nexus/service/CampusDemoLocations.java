package com.nexus.service;
import com.nexus.domain.CampusCatalog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component public class CampusDemoLocations {
    private final JdbcTemplate jdbc; private final boolean enabled;
    public CampusDemoLocations(JdbcTemplate jdbc,@Value("${NEXUS_MAP_DEMO:false}") boolean enabled){this.jdbc=jdbc;this.enabled=enabled;}
    @EventListener(ApplicationReadyEvent.class) @Transactional public void mapDemo(){
        if(!enabled)return;
        var sites=CampusCatalog.SITES.stream().filter(s->s.lat()!=null).toList();
        StringBuilder sql=new StringBuilder("UPDATE incidents SET location_code=CASE MOD(id,").append(sites.size()).append(") ");
        java.util.List<Object> args=new java.util.ArrayList<>();
        for(int i=0;i<sites.size();i++){sql.append("WHEN ").append(i).append(" THEN ? ");args.add(sites.get(i).code());}
        sql.append("END, location_note='Fictional demo location', version=version+1 WHERE location_code IS NULL AND title LIKE '[Demo] Archive / %'");
        System.out.println("Demo incident locations mapped: "+jdbc.update(sql.toString(),args.toArray()));
    }
}
