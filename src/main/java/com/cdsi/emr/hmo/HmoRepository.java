package com.cdsi.emr.hmo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HmoRepository extends JpaRepository<Hmo, Long> {
	
	public List<Hmo> findAllByOrderByHmoName();
	
}
