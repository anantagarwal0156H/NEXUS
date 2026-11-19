package com.nexus.domain; import jakarta.persistence.*; import java.util.*; import com.nexus.domain.Rules.Role;
@Entity @DiscriminatorValue("TECHNICIAN") public class Technician extends Account{
 @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="technician_skills",joinColumns=@JoinColumn(name="account_id")) @MapKeyColumn(name="category_name") @Column(name="skill_level") public Map<String,Integer> skills=new HashMap<>();
 public boolean available=true; public int capacity=8;
 protected Technician(){} public Technician(String n,String e,String h){super(n,e,h);} public Role role(){return Role.TECHNICIAN;}
}
