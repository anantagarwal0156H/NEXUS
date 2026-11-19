package com.nexus.domain;
import java.time.*;
import java.util.*;
/** Pure Java business rules; independent of Spring or the database. */
public final class Rules {
 private Rules(){}
 public enum Role {EMPLOYEE, TECHNICIAN, ADMINISTRATOR}
 public enum Priority {P1,P2,P3,P4}
 public enum Status {NEW,CLASSIFIED,ASSIGNED,ACKNOWLEDGED,IN_PROGRESS,RESOLVED,VERIFIED,CLOSED;
  public boolean active(){return ordinal()<RESOLVED.ordinal();}}
 public static int score(int impact,int urgency,int criticality){for(int v:new int[]{impact,urgency,criticality}) if(v<1||v>3)throw new IllegalArgumentException("Factors must be 1–3");return impact*urgency*criticality;}
 public static Priority priority(int i,int u,int c){int v=score(i,u,c);return v>=18?Priority.P1:v>=9?Priority.P2:v>=4?Priority.P3:Priority.P4;}
 public static void transition(Status from,Status to){if(!(to.ordinal()==from.ordinal()+1 || (from==Status.RESOLVED&&to==Status.IN_PROGRESS))) throw new InvalidTransition(from+" → "+to+" is not allowed");}
 public static class InvalidTransition extends IllegalStateException{public InvalidTransition(String message){super(message);}}
 public record Target(int responseMinutes,int resolutionMinutes,int warningMinutes){public Target{if(responseMinutes<1||resolutionMinutes<responseMinutes||warningMinutes<1)throw new IllegalArgumentException("Invalid SLA targets");}}
 public static Target defaultTarget(Priority p){return switch(p){case P1->new Target(5,30,2);case P2->new Target(15,120,5);case P3->new Target(60,480,15);case P4->new Target(240,1440,60);};}
 public enum Sla {OK,WARNING,BREACHED,MET}
 public static Sla sla(Instant now,Instant deadline,Instant completed,int warningMinutes){if(completed!=null)return completed.isAfter(deadline)?Sla.BREACHED:Sla.MET;if(!now.isBefore(deadline))return Sla.BREACHED;return !now.isBefore(deadline.minus(Duration.ofMinutes(warningMinutes)))?Sla.WARNING:Sla.OK;}
 private static Set<String> tokens(String s){Set<String> t=new HashSet<>(Arrays.asList(s.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")));t.removeAll(Set.of("","a","the","is","to","and","of","my","i","in","on"));return t;}
 public static double similarity(String a,String b){Set<String> x=tokens(a),y=tokens(b),union=new HashSet<>(x);union.addAll(y);x.retainAll(y);return union.isEmpty()?0:(double)x.size()/union.size();}
 public static boolean duplicate(String a,String b){return similarity(a,b)>=.35;}
 public static Set<Long> reachable(Map<Long,Set<Long>> graph,long source){Set<Long> seen=new LinkedHashSet<>();Deque<Long> queue=new ArrayDeque<>();seen.add(source);queue.add(source);while(!queue.isEmpty())for(long next:graph.getOrDefault(queue.remove(),Set.of()))if(seen.add(next))queue.add(next);seen.remove(source);return seen;}
}
