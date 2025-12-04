package com.planet_learning.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.planet_learning.utils.RFPStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "rfps")
public class RequestForPurposal 
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "description_raw")
	private String descriptionRaw;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "parsed_json_data", columnDefinition = "jsonb")
	private Map<String, Object> parsedJsonData;
	
	@Column(name = "rfp_email_format")
	private String rfpEmailFormat;
	
	@Enumerated(EnumType.STRING)
	private RFPStatusEnum status; // default OPEN we update this when vendor replies
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate()
	{
		createdAt = LocalDateTime.now();
		if(status == null) {
			status = RFPStatusEnum.OPEN;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescriptionRaw() {
		return descriptionRaw;
	}

	public void setDescriptionRaw(String descriptionRaw) {
		this.descriptionRaw = descriptionRaw;
	}
	
	public Map<String, Object> getParsedJsonData() {
		return parsedJsonData;
	}

	public void setParsedJsonData(Map<String, Object> parsedJsonData) {
		this.parsedJsonData = parsedJsonData;
	}

	public String getRfpEmailFormat() {
		return rfpEmailFormat;
	}

	public void setRfpEmailFormat(String rfpEmailFormat) {
		this.rfpEmailFormat = rfpEmailFormat;
	}

	public RFPStatusEnum getStatus() {
		return status;
	}

	public void setStatus(RFPStatusEnum status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
