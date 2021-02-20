package com.cdsi.emr.personnel;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cdsi.emr.config.data.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_username_personnel", columnNames = "username"),
        @UniqueConstraint(name = "unique_email_personnel", columnNames = "email")
})
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data public class Personnel extends Auditable implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = " is mandatory.")
    private String username;
    @NotBlank(message = " is mandatory.")
    @JsonIgnore private String password;
    @NotBlank(message = " is mandatory.")
    private String firstName;
    @NotBlank(message = " is mandatory.")
    private String lastName;
    @Size(min = 1, max = 1)
    private String gender;
    private String address;
    private String contactNumber;
    @Email(message = "Invalid email.")
    private String email;
    private String status = "ACTIVE";
    private String userType = "DOCTOR";  // "DOCTOR" or "STAFF"
    private long staffSupervisorId;

    private String credentials;
    private String licenseNumber;
    private String specialization;
    private String ptrNumber;
    private String sNumber;
    @PastOrPresent
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.of(9999, 12, 31);

    //@ElementCollection(fetch = FetchType.EAGER)
    //private Collection<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        //for (String role : roles) {
        //    authorities.add(new SimpleGrantedAuthority(role));
        //}
        authorities.add(new SimpleGrantedAuthority(this.userType));
        return authorities;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
