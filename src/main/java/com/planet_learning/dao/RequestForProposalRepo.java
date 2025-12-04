package com.planet_learning.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planet_learning.entity.RequestForPurposal;

@Repository
public interface RequestForProposalRepo extends JpaRepository<RequestForPurposal, Long>
{

}
