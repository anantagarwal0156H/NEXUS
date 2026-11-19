package com.nexus.domain; import jakarta.persistence.*;
@Entity @Table(name="incident_categories") public class Category{@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(nullable=false,unique=true) public String name; protected Category(){} public Category(String name){this.name=name;}}
