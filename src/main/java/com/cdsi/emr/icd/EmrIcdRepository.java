package com.cdsi.emr.icd;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface EmrIcdRepository extends JpaRepository<EmrIcd, Long> {
	
	List<EmrIcd> findAllByDescriptionContaining(String keyword);
	List<EmrIcd> findAllByDescriptionContainingOrCodeContaining(String keyword, String keyword2);
	
}
