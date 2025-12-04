package com.planet_learning.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.planet_learning.utils.ProposalStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "proposals")
public class Proposal {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name = "rfp_id")
	private RequestForPurposal rfp;
	
	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;
	
	@Column(name = "email_content_raw", columnDefinition = "TEXT")
    private String emailContentRaw;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "parsed_vendor_res", columnDefinition = "josnb")
    private Map<String, Object> parsedVendorRes; // response we got from email
	
	@Column(name = "ai_score")
	private Integer aiScore;
	
	@Column(name = "ai_analysis", columnDefinition = "TEXT")
	private Integer aiAnalysis;
	
	@Enumerated(EnumType.STRING)
	private ProposalStatusEnum status; // status VARCHAR(20), -- 'SENT, RECEIVED, REJECTED, ACCEPTED'
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate()
	{
		createdAt = LocalDateTime.now();
		if(status == null) {
			status = ProposalStatusEnum.SENT;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RequestForPurposal getRfp() {
		return rfp;
	}

	public void setRfp(RequestForPurposal rfp) {
		this.rfp = rfp;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public String getEmailContentRaw() {
		return emailContentRaw;
	}

	public void setEmailContentRaw(String emailContentRaw) {
		this.emailContentRaw = emailContentRaw;
	}
	
	public Integer getAiScore() {
		return aiScore;
	}

	public void setAiScore(Integer aiScore) {
		this.aiScore = aiScore;
	}

	public Integer getAiAnalysis() {
		return aiAnalysis;
	}

	public void setAiAnalysis(Integer aiAnalysis) {
		this.aiAnalysis = aiAnalysis;
	}

	public ProposalStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ProposalStatusEnum status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Map<String, Object> getParsedVendorRes() {
		return parsedVendorRes;
	}

	public void setParsedVendorRes(Map<String, Object> parsedVendorRes) {
		this.parsedVendorRes = parsedVendorRes;
	}
}
