package com.cdsi.emr.mail;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.mail.internet.MimeMessage;

import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cdsi.emr.exception.EmrException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EhrMail {
    private final JavaMailSender emailSender;

    public EhrMail (
            final JavaMailSender emailSender
            ) {
        this.emailSender = emailSender;
    }

    public void sendEmail (EmailDto emailDto) throws EmrException {
        try {
            log.info("Sending email. Timestamp: {} Subject: {}", LocalDateTime.now(ZoneId.of("Asia/Manila")),
                    emailDto.getSubject());

            //            MimeBodyPart mimeBodyMsg = new MimeBodyPart();

            MimeMessage mimeMessage = this.emailSender.createMimeMessage();
            MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

            mimeHelper.setFrom(emailDto.getSender());
            mimeHelper.setTo(emailDto.getRecipients());
            mimeHelper.setSubject(emailDto.getSubject());
            mimeHelper.setText(emailDto.getBody());

            this.emailSender.send(mimeHelper.getMimeMessage());
            log.info("Email sent. Recipients: {}", emailDto.getRecipients());

        } catch (Exception e) {
            log.error("Internal Server Error. Email not send. Recipients: {}", emailDto.getRecipients());
            throw new EmrException(HttpStatus.INTERNAL_SERVER_ERROR, "Email not send.");
        }
    }
}
