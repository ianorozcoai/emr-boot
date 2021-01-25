package com.cdsi.emr.password;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cdsi.emr.exception.EmrException;
import com.cdsi.emr.mail.EhrMail;
import com.cdsi.emr.mail.EmailDto;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.personnel.PersonnelRepository;
import com.cdsi.emr.util.SecureRandomStringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private static final String EMAIL_HEADER_TEMPLATE = "Dear %s,\\n\\n";
    private static final String EMAIL_CONTENT_TEMPLATE = "We have received a request to reset your password.\\n\\n" +
            "Go to this link to <a href='%s'>reset your password</a>.\\n\\n" +
            "If you did not request to reset your password, please contact EHR Admin - %s";
    private static final String RESET_PASSWORD_URL_TEMPLATE = "%s/reset-password?token=%s";

    private static final String EHR_ADMIN_CONTACT_NUMBER = "(63) 1234123";

    private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final EhrMail ehrMail;

    public ChangePasswordServiceImpl (
            final PersonnelRepository personnelRepository,
            final PasswordEncoder passwordEncoder,
            final ForgotPasswordTokenRepository forgotPasswordTokenRepository,
            final EhrMail ehrMail
            ) {
        this.personnelRepository = personnelRepository;
        this.passwordEncoder = passwordEncoder;
        this.forgotPasswordTokenRepository = forgotPasswordTokenRepository;
        this.ehrMail = ehrMail;
    }

    @Override
    public void changePassword(String username, ChangePasswordRequestDto changePasswordRequest, ChangePasswordType requestType) {
        log.info("Changing password of Personnel {}...", username);
        Optional<Personnel> personnel = this.personnelRepository.findByUsername(username);

        if ( personnel.isPresent()) {
            if (ChangePasswordType.DEFAULT == requestType) {
                if (!this.passwordEncoder.matches(changePasswordRequest.getOldPassword(), personnel.get().getPassword())) {
                    log.error("Old password NOT match.");
                    throw new EmrException(HttpStatus.BAD_REQUEST, "Old password NOT match.");
                }
            }

            personnel.get().setPassword(this.passwordEncoder.encode(changePasswordRequest.getNewPassword()));

            log.info("Saving updated Personnel in Database and in Security Context");
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    this.personnelRepository.save(personnel.get()),
                    personnel.get().getPassword(),
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Password updated successfully of Personnel {} - {}, {}",
                    personnel.get().getUsername(),
                    personnel.get().getLastName(),
                    personnel.get().getFirstName());

        } else {
            log.error("Personnel NOT found.");
            throw new EmrException(HttpStatus.NOT_FOUND, "User NOT found.");
        }
    }

    @Override
    public ForgotPasswordToken generateForgotPasswordToken(ForgotPasswordRequestDto forgotPasswordRequestDto) {
        log.info("Generate forgot password token. REQUEST: {}", forgotPasswordRequestDto.getRequest());
        Optional<Personnel> personnel = this.personnelRepository.findByUsernameOrEmail(
                forgotPasswordRequestDto.getRequest(), forgotPasswordRequestDto.getRequest());

        if (personnel.isPresent()) {
            ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
            forgotPasswordToken.setToken(SecureRandomStringUtils.randomAlphanumeric());
            forgotPasswordToken.setEmail(personnel.get().getEmail());
            forgotPasswordToken.setUsername(personnel.get().getUsername());
            forgotPasswordToken.setCreationDate(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            forgotPasswordToken.setTokenStatus("A");

            this.forgotPasswordTokenRepository.save(forgotPasswordToken);
            log.info("Forgot password token saved.");
            return forgotPasswordToken;
        } else {
            log.error("Personnel NOT found.");
            throw new EmrException(HttpStatus.NOT_FOUND, "User NOT found.");
        }
    }

    @Override
    public void sendGeneratedTokenToUserEmail(ForgotPasswordToken forgotPasswordToken, String appUrl) {
        EmailDto email = new EmailDto();
        email.setSender("cdsinoreply@gmail.com");
        email.setRecipients(new String[] {forgotPasswordToken.getEmail()});
        email.setSubject("EHR Forgot Password");

        String body =
                String.format(EMAIL_HEADER_TEMPLATE, forgotPasswordToken.getUsername()) +
                String.format(EMAIL_CONTENT_TEMPLATE,
                        String.format(RESET_PASSWORD_URL_TEMPLATE, appUrl, forgotPasswordToken.getToken()),
                        EHR_ADMIN_CONTACT_NUMBER);

        email.setBody(body);
        this.ehrMail.sendEmail(email);
    }
}
