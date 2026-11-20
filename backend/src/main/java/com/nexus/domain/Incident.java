package com.nexus.domain;
import jakarta.persistence.*; import java.time.*; import com.nexus.domain.Rules.*;
@Entity @Table(name="incidents",indexes={@Index(columnList="reporter_id"),@Index(columnList="technician_id"),@Index(columnList="status")})
public class Incident {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Version public long version;
 @Column(nullable=false,length=180) public String title; @Column(nullable=false,length=8000) public String description;
 @ManyToOne(optional=false) @JoinColumn(name="reporter_id") public Account reporter;
 @ManyToOne @JoinColumn(name="technician_id") public Technician technician;
 @ManyToOne(optional=false) public ITService service; @ManyToOne(optional=false) public Category category;
 public int impact,urgency,criticality,warningMinutes; @Enumerated(EnumType.STRING) public Priority priority;
 @Enumerated(EnumType.STRING) private Status status=Status.NEW;
 public Instant createdAt,responseDue,resolutionDue,acknowledgedAt,resolvedAt,verifiedAt,closedAt;
 @Column(length=8000) public String resolution; @Column(length=2000) public String feedback; public Integer rating;
 public boolean responseWarning,resolutionWarning,responseBreach,resolutionBreach,escalated;
 @Column(length=16000) public String assignmentEvidence;
 @Column(length=40) public String locationCode;
 @Column(length=100) public String locationNote;
 @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="incident_relations",joinColumns=@JoinColumn(name="incident_id")) @Column(name="related_id") public java.util.Set<Long> related=new java.util.HashSet<>();
 protected Incident(){}
 public Incident(String title,String description,Account reporter,ITService service,Category category,int impact,int urgency,Instant now,Target t){this.title=title;this.description=description;this.reporter=reporter;this.service=service;this.category=category;this.impact=impact;this.urgency=urgency;criticality=service.criticality;priority=Rules.priority(impact,urgency,criticality);createdAt=now;responseDue=now.plusSeconds(t.responseMinutes()*60L);resolutionDue=now.plusSeconds(t.resolutionMinutes()*60L);warningMinutes=t.warningMinutes();}
 public Status status(){return status;} public void move(Status to){Rules.transition(status,to);status=to;}
 public void assign(Technician t){if(status!=Status.CLASSIFIED&&status!=Status.ASSIGNED)throw new Rules.InvalidTransition("Assignment allowed before acknowledgement");technician=t;if(status==Status.CLASSIFIED)move(Status.ASSIGNED);}
 public void acknowledge(Instant now){move(Status.ACKNOWLEDGED);acknowledgedAt=now;}
 public void resolve(String text,Instant now){if(text==null||text.isBlank())throw new IllegalArgumentException("Resolution is required");move(Status.RESOLVED);resolution=text;resolvedAt=now;}
 public void verify(String text,Integer stars,Instant now){if(stars==null||stars<1||stars>5)throw new IllegalArgumentException("Rating must be 1–5");move(Status.VERIFIED);feedback=text;rating=stars;verifiedAt=now;}
 public void close(Instant now){move(Status.CLOSED);closedAt=now;}
 public void reopen(){move(Status.IN_PROGRESS);resolvedAt=null;resolution=null;}
}
