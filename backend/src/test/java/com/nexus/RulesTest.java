package com.nexus;
import com.nexus.domain.Rules;import com.nexus.domain.Rules.*;import com.nexus.strategy.*;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;import java.time.*;import java.util.*;
class RulesTest{
 @Test void priority(){assertEquals(Priority.P1,Rules.priority(3,3,2));assertEquals(Priority.P2,Rules.priority(3,3,1));assertEquals(Priority.P3,Rules.priority(2,2,2));assertEquals(Priority.P4,Rules.priority(1,1,3));assertThrows(IllegalArgumentException.class,()->Rules.score(0,1,1));}
 @Test void assignment(){var a=List.of(new AssignmentStrategy.Candidate(1,"A",5,4,5,true),new AssignmentStrategy.Candidate(2,"B",3,0,5,true),new AssignmentStrategy.Candidate(3,"C",0,0,5,true));assertEquals(1L,new SkillBasedAssignment().decide(a).selected());assertEquals(2L,new LoadBasedAssignment().decide(a).selected());assertEquals(1,new SkillBasedAssignment().decide(a).excluded().size());assertNull(new LoadBasedAssignment().decide(List.of(a.get(2))).selected());}
 @Test void transitions(){assertDoesNotThrow(()->Rules.transition(Status.RESOLVED,Status.IN_PROGRESS));assertThrows(Rules.InvalidTransition.class,()->Rules.transition(Status.NEW,Status.CLOSED));}
 @Test void sla(){Instant due=Instant.now();assertEquals(Sla.WARNING,Rules.sla(due.minusSeconds(60),due,null,2));assertEquals(Sla.BREACHED,Rules.sla(due,due,null,2));assertEquals(Sla.MET,Rules.sla(due.plusSeconds(1),due,due,2));assertEquals(Sla.BREACHED,Rules.sla(due.plusSeconds(1),due,due.plusSeconds(1),2));}
 @Test void duplicate(){assertTrue(Rules.duplicate("payment app failed","payment app failed again"));assertFalse(Rules.duplicate("payment app failed","printer driver error"));}
 @Test void graph(){assertEquals(Set.of(2L,3L),Rules.reachable(Map.of(1L,Set.of(2L),2L,Set.of(3L),3L,Set.of(1L)),1));}
}
