package com.cdsi.emr.password;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {
    Optional<ForgotPasswordToken> findByTokenAndTokenStatus(String token, String tokenStatus);
}
