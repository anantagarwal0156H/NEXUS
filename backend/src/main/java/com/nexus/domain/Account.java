package com.nexus.domain;
import jakarta.persistence.*; import com.nexus.domain.Rules.Role;
@Entity @Table(name="accounts") @Inheritance(strategy=InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name="account_type")
public abstract class Account {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String email;
 @Column(nullable=false) public String name;
 @Column(nullable=false) public String passwordHash;
 public boolean enabled=true;
 protected Account(){} protected Account(String name,String email,String hash){this.name=name;this.email=email;passwordHash=hash;}
 public abstract Role role();
}
