package com.cdsi.emr.icd;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface IcdRepository extends JpaRepository<Icd, Long> {
	
	List<Icd> findAllByDescriptionContaining(String keyword);
	
}
