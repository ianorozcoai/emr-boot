package com.cdsi.emr.password;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.exception.EmrException;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.UXMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("/users")
public class PasswordController {

    private final ChangePasswordService changePasswordService;
    private final ForgotPasswordTokenRepository forgotPassRepo;

    public PasswordController(
            final ChangePasswordService changePasswordService,
            final ForgotPasswordTokenRepository forgotPassRepo
            ) {
        this.changePasswordService = changePasswordService;
        this.forgotPassRepo = forgotPassRepo;
    }

    /*########### Change Password - DEFAULT ########### */

    @GetMapping("/changePassword")
    public String getUserChangePasswordPage(Model model){
        model.addAttribute("changepassDto", new ChangePasswordRequestDto());
        return "dashboard/change_password";
    }

    @GetMapping("/emrChangePassword")
    public String getUserEMRChangePasswordPage(Model model){
        model.addAttribute("changepassDto", new ChangePasswordRequestDto());
        return "emr/emr_change_password";
    }

    @PostMapping({"/changePassword", "/emrChangePassword"})
    public String submitChangePassword(Authentication auth
            ,@Valid ChangePasswordRequestDto changepassDto
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ,HttpServletRequest request
            ){
        if (errors.hasErrors()) {
            model.addAttribute("personnel", new Personnel());
            model.addAttribute("changepassDto", changepassDto);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            if (request.getServletPath().equalsIgnoreCase("/emrChangePassword")) {
                return "emr/emr_change_password";
            } else {
                return "dashboard/change_password";
            }
        }

        Personnel loggedUser = (Personnel) auth.getPrincipal();
        try {
            if (changepassDto.getNewPassword() != null
                    && changepassDto.getNewPassword().equals(changepassDto.getConfirmNewPassword())) {
                this.changePasswordService.changePassword(loggedUser.getUsername(), changepassDto, ChangePasswordType.DEFAULT);
                redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Password updated successfully."));
            } else {
                // Backend handling of Confirm Password
                log.error("Confirm password NOT match.");
                throw new EmrException(HttpStatus.BAD_REQUEST, "Confirm password NOT match.");
            }

        } catch (EmrException ex) {
            log.error("Bad Request. Error - {}, REQUEST - username {}", ex.getMessage(), loggedUser.getUsername());
            redirect.addFlashAttribute("uxmessage", new UXMessage("ERROR", ex.getMessage()));

        } catch (Exception e) {
            log.error("Internal Server Error. ERROR - {}, REQUEST - username {}", e.toString(), loggedUser.getUsername());
            redirect.addFlashAttribute("uxmessage", new UXMessage("ERROR", "Internal Server Error. Please try again later."));
        }

        if (request.getServletPath().equalsIgnoreCase("/emrChangePassword")) {
            return "redirect:/emrChangePassword";
        } else {
            return "redirect:/changePassword";
        }

    }



    /*########### Change Password - RESET ########### */

    @GetMapping("/forgot-password")
    public String getUserForgotPasswordPage(Model model){
        model.addAttribute("forgot-password", new ForgotPasswordRequestDto());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity submitForgotPassword(@RequestBody ForgotPasswordRequestDto request, HttpServletRequest req) {
        ResponseEntity response;
        try {
            ForgotPasswordToken token = this.changePasswordService.generateForgotPasswordToken(request);
            String appUrl = req.getScheme() + "://" + req.getServerName();
            this.changePasswordService.sendGeneratedTokenToUserEmail(token, appUrl);

            response = ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(
                    HttpStatus.OK.value(), "Success", "Please check your email to reset your password."
                    ));

        } catch (EmrException ex) {
            log.error("Bad Request. Error - {}, REQUEST - username/email {}", ex.getMessage(), request.getRequest());
            response = ResponseEntity.status(ex.getStatus()).body(new ResponseDto(
                    ex.getStatus().value(), ex.getMessage(), "Please check your spelling."
                    ));

        } catch (Exception e) {
            log.error("Internal Server Error. REQUEST - username/email {}", request.getRequest());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null
                    ));
        }
        return response;
    }

    @GetMapping("/reset-password")
    public String getUserResetPasswordPage(Model model, @RequestParam("token") String token){
        String returnPage;
        try {
            Optional forgotPasswordToken = this.forgotPassRepo.findByTokenAndTokenStatus(token, "A");

            if (forgotPasswordToken.isPresent()) {
                model.addAttribute("reset-token", token);
                model.addAttribute("change-password", new ChangePasswordRequestDto());
                returnPage = "reset-password";
            } else {
                returnPage = "error-reset-password-link";
            }

        } catch (Exception e) {
            log.error("Cannot process reset password. Error - {} {}", e.getMessage(), e);
            returnPage = "error-reset-password-link";
        }

        return returnPage;
    }

    @PutMapping("/reset-password")
    public ResponseEntity submitResetPassword(@RequestBody ChangePasswordRequestDto request) {
        ResponseEntity response;
        try {
            Optional<ForgotPasswordToken> forgotPasswordToken =
                    this.forgotPassRepo.findByTokenAndTokenStatus(request.getToken(), "A");

            if (forgotPasswordToken.isPresent()) {
                this.changePasswordService.changePassword(forgotPasswordToken.get().getUsername(),
                        request, ChangePasswordType.RESET);

                response = ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(
                        HttpStatus.OK.value(), "Success", null
                        ));

            } else {
                throw new EmrException(HttpStatus.NOT_FOUND, "Invalid reset password token.");
            }

        } catch (EmrException ex) {
            log.error("Bad Request. Error - {}, REQUEST - token {}", ex.getMessage(), request.getToken());
            response = ResponseEntity.status(ex.getStatus()).body(new ResponseDto(
                    ex.getStatus().value(), ex.getMessage(), null
                    ));

        } catch (Exception e) {
            log.error("Internal Server Error. REQUEST - token {}", request.getToken());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null
                    ));
        }
        return response;
    }
}
