package com.cdsi.emr.personnel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface PersonnelRepository extends JpaRepository<Personnel, Long>{
    Optional<Personnel> findByUsername(String username);
    //Optional<Personnel> findByEmail(String email);
    Optional<Personnel> findByUsernameOrEmail(String username, String email);
    Set<Personnel> findAllByUserType(String userType);
    List<Personnel> findByIdIn(List<Long> doctorIds);
    Set<Personnel> findAllByStatusAndUserTypeIn(String status, String[] userTypes);
    List<Personnel> findAllByStaffSupervisorId(long id);
}
