package com.nexus.event;
import java.time.Instant; import java.util.Set;
public record IncidentEvent(Long incidentId,Instant at,String actor,String action,String previousState,String newState,String detail,boolean privateEntry,Set<Long> recipients){}
