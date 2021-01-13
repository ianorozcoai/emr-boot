package com.cdsi.emr.vaccination;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Data public class VaccineName {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
}

@Repository
interface VaccineNameRepository extends CrudRepository<VaccineName, Long> {}
