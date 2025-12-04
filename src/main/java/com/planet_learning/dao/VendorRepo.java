package com.planet_learning.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.planet_learning.entity.Vendor;

@Repository
public interface VendorRepo extends JpaRepository<Vendor, Long> 
{
	@Query("SELECT v FROM Vendor v WHERE v.id In :ids")
	List<Vendor> findAllVendorsByIds(@Param("ids") List<Long> ids);
	
	Optional<Vendor> findByEmail(String email);
}
