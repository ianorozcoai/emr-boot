package com.cdsi.emr.medication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "generics", path = "generics")
public interface EMRGenericsLookupRepository extends JpaRepository<EMRGenericsLookup, Long> {

	List<EMRGenericsLookup> findByGenericName(String genericName);
	
	@Query("SELECT DISTINCT g.genericName FROM EMRGenericsLookup g")
	List<String> findDistinctByGenericName();
}
