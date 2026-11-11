package com.uxinterfacely.offerletter.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "offer_sends", indexes = {
    @Index(name = "idx_offer_candidate_name", columnList = "candidate_name"),
    @Index(name = "idx_offer_created_at", columnList = "created_at")
})
public class OfferSend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="candidate_name", nullable=false, length=180) private String candidateName;
    @Column(name="job_title", length=180) private String jobTitle;
    @Column(name="recipient_email", length=254) private String recipientEmail;
    @Column(name="date_of_joining") private LocalDate dateOfJoining;
    @Column(name="letter_date") private LocalDate letterDate;
    @Column(length=120) private String department;
    @Column(name="work_location", length=240) private String workLocation;
    @Column(name="role_description", columnDefinition="TEXT") private String roleDescription;
    private Long annualCtc;
    private Integer noticePeriodDays;
    private Boolean includeBond;
    private Integer bondYears;
    private Long bondAmount;
    private Boolean pfIncluded;
    private Long employeePf;
    private Long employerPf;
    private Boolean variablePayIncluded;
    private Long variablePayAmount;
    private Boolean tdsIncluded;
    private Long tdsAmount;
    @Column(name="pdf_url", length=1000) private String pdfUrl;
    @Column(length=30) private String status = "SAVED";
    @Column(name="created_at", nullable=false) private Instant createdAt = Instant.now();

    @PrePersist void prePersist() { if (createdAt == null) createdAt = Instant.now(); if (status == null) status = "SAVED"; }

    public Long getId(){return id;} public String getCandidateName(){return candidateName;} public void setCandidateName(String v){candidateName=v;}
    public String getJobTitle(){return jobTitle;} public void setJobTitle(String v){jobTitle=v;}
    public String getRecipientEmail(){return recipientEmail;} public void setRecipientEmail(String v){recipientEmail=v;}
    public LocalDate getDateOfJoining(){return dateOfJoining;} public void setDateOfJoining(LocalDate v){dateOfJoining=v;}
    public LocalDate getLetterDate(){return letterDate;} public void setLetterDate(LocalDate v){letterDate=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public String getWorkLocation(){return workLocation;} public void setWorkLocation(String v){workLocation=v;}
    public String getRoleDescription(){return roleDescription;} public void setRoleDescription(String v){roleDescription=v;}
    public Long getAnnualCtc(){return annualCtc;} public void setAnnualCtc(Long v){annualCtc=v;}
    public Integer getNoticePeriodDays(){return noticePeriodDays;} public void setNoticePeriodDays(Integer v){noticePeriodDays=v;}
    public Boolean getIncludeBond(){return includeBond;} public void setIncludeBond(Boolean v){includeBond=v;}
    public Integer getBondYears(){return bondYears;} public void setBondYears(Integer v){bondYears=v;}
    public Long getBondAmount(){return bondAmount;} public void setBondAmount(Long v){bondAmount=v;}
    public Boolean getPfIncluded(){return pfIncluded;} public void setPfIncluded(Boolean v){pfIncluded=v;}
    public Long getEmployeePf(){return employeePf;} public void setEmployeePf(Long v){employeePf=v;}
    public Long getEmployerPf(){return employerPf;} public void setEmployerPf(Long v){employerPf=v;}
    public Boolean getVariablePayIncluded(){return variablePayIncluded;} public void setVariablePayIncluded(Boolean v){variablePayIncluded=v;}
    public Long getVariablePayAmount(){return variablePayAmount;} public void setVariablePayAmount(Long v){variablePayAmount=v;}
    public Boolean getTdsIncluded(){return tdsIncluded;} public void setTdsIncluded(Boolean v){tdsIncluded=v;}
    public Long getTdsAmount(){return tdsAmount;} public void setTdsAmount(Long v){tdsAmount=v;}
    public String getPdfUrl(){return pdfUrl;} public void setPdfUrl(String v){pdfUrl=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Instant getCreatedAt(){return createdAt;}
}
