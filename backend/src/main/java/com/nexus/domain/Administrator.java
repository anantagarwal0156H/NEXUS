package com.nexus.domain; import jakarta.persistence.*; import com.nexus.domain.Rules.Role;
@Entity @DiscriminatorValue("ADMIN") public class Administrator extends Account{protected Administrator(){} public Administrator(String n,String e,String h){super(n,e,h);} public Role role(){return Role.ADMINISTRATOR;}}
