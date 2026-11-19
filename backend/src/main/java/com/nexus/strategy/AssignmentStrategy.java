package com.nexus.strategy;
import java.util.*;
/** Strategy: runtime selection changes ordering but never eligibility constraints. */
public interface AssignmentStrategy {
 record Candidate(long id,String name,int skill,int workload,int capacity,boolean available){public String exclusion(){return !available?"Unavailable":skill<=0?"Missing category skill":workload>=capacity?"At capacity":"";}public boolean eligible(){return exclusion().isEmpty();}}
 record Decision(String strategy,List<Candidate> ranked,List<Candidate> excluded,Long selected){ }
 String name(); Comparator<Candidate> ranking();
 default Decision decide(List<Candidate> all){var ranked=all.stream().filter(Candidate::eligible).sorted(ranking()).toList();return new Decision(name(),ranked,all.stream().filter(c->!c.eligible()).toList(),ranked.isEmpty()?null:ranked.getFirst().id());}
}
