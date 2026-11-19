package com.nexus.domain; import jakarta.persistence.*; import com.nexus.domain.Rules.Role;
@Entity @DiscriminatorValue("EMPLOYEE") public class Employee extends Account{protected Employee(){} public Employee(String n,String e,String h){super(n,e,h);} public Role role(){return Role.EMPLOYEE;}}
