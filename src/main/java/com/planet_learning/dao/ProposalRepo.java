package com.planet_learning.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.planet_learning.entity.Proposal;
import com.planet_learning.utils.ProposalStatusEnum;

@Repository
public interface ProposalRepo extends JpaRepository<Proposal, Long>
{
	@Query("""
			SELECT p FROM Proposal p 
			WHERE p.vendor.id = :vendorId
			AND p.status = :status
			""")
	Optional<Proposal> findByVendorIdAndStatus(
			@Param("vendorId") Long vendorId,
			@Param("status") ProposalStatusEnum status);
	
	@Query("""
			SELECT p FROM Proposal p 
			WHERE p.rfp.id = :rfpId 
			AND p.vendor.id = :vendorId
			AND p.status = :status
			""")
	Optional<Proposal> findByRfpIdAndVendorIdAndStatus(
			@Param("rfpId") Long rfpId,
			@Param("vendorId") Long vendorId,
			@Param("status") ProposalStatusEnum status);
	
	@Query("""
			SELECT p FROM Proposal p 
			WHERE p.rfp.id = :rfpId 
			AND p.status = :status
			AND p.vendor.id IN :vendorIds
			""")
	List<Proposal> findAllByRfpIdAndVendorIdsAndStatus(
			@Param("rfpId") Long rfpId,
			@Param("status") ProposalStatusEnum status,
			@Param("vendorIds") List<Long> vendorIds);
}