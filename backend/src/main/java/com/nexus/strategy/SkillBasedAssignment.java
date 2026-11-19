package com.nexus.strategy; import java.util.*;
public final class SkillBasedAssignment implements AssignmentStrategy {public String name(){return "SKILL";} public Comparator<Candidate> ranking(){return Comparator.comparingInt(Candidate::skill).reversed().thenComparingInt(Candidate::workload).thenComparingLong(Candidate::id);}}
