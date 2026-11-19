package com.nexus.event; import org.springframework.stereotype.Component; import org.springframework.context.event.EventListener; import com.nexus.data.AuditRepository; import com.nexus.domain.Audit; import java.util.List;
@Component public class EventListeners{private final AuditRepository audit;private final List<NotificationChannel> channels;public EventListeners(AuditRepository a,List<NotificationChannel> c){audit=a;channels=c;}
 @EventListener public void audit(IncidentEvent e){audit.save(new Audit(e.incidentId(),e.at(),e.actor(),e.action(),e.previousState(),e.newState(),e.detail(),e.privateEntry()));}
 @EventListener public void notifyUsers(IncidentEvent e){if(!e.privateEntry())for(Long u:e.recipients())for(NotificationChannel c:channels)c.deliver(e,u);}
}
