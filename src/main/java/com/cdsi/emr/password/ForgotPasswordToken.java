package com.cdsi.emr.password;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Entity
@Data
public class ForgotPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "varchar(300)", nullable = false)
    private String token;

    @Column(columnDefinition = "varchar(1) default 'A'", nullable = false)
    private String tokenStatus;
}
