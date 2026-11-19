package com.nexus.strategy; import java.util.*;
public final class LoadBasedAssignment implements AssignmentStrategy {public String name(){return "LOAD";} public Comparator<Candidate> ranking(){return Comparator.comparingInt(Candidate::workload).thenComparing(Comparator.comparingInt(Candidate::skill).reversed()).thenComparingLong(Candidate::id);}}
