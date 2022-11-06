package com.oauth.two.cryptoagent.controller;


import com.oauth.two.cryptoagent.entity.PasswordModel;
import com.oauth.two.cryptoagent.entity.UserDetail;
import com.oauth.two.cryptoagent.entity.VerificationToken;
import com.oauth.two.cryptoagent.event.RegistrationCompleteEvent;
import com.oauth.two.cryptoagent.model.UserModel;
import com.oauth.two.cryptoagent.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
@Slf4j
public class controller {

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/registration")
    public String userRegistration(@RequestBody UserModel userModel, final HttpServletRequest request){

        UserDetail user = userService.userRegistration(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user,
                applicationUrl(request)));

        return "Success ";
    }
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam(required = false, name="token") String token){
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")){
            return "User successfully validated";
        }
        return "Bad Credential";
    }
    @GetMapping("/resendVerification")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request){
        VerificationToken verificationToken =
                userService.generateNewVerificationToken(oldToken);
        UserDetail user =verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request),verificationToken);
        return "Verification Link sent";
    }
    @PostMapping("/resetpassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,HttpServletRequest request) throws MessagingException {
        UserDetail userDetail = userService.findUseByEmail(passwordModel.getEmail());

        String url =";";
        if (userDetail!=null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(userDetail,token);
            url=passwordResetTokenMail(userDetail,applicationUrl(request),token);
           sentItToMail(url,passwordModel.getEmail());
            System.out.println(passwordModel.getEmail());
        }
        return url;
    }
    @Autowired
    private JavaMailSender javaMailSender;
    private void sentItToMail(String url, String email) throws MessagingException {
       MimeMessage message =javaMailSender.createMimeMessage();
       MimeMessageHelper helper = new MimeMessageHelper(message,true);
       helper.setFrom("nalinento@gmail.com");
       helper.setText(url);
       helper.setTo(email);

       javaMailSender.send(message);
    }


    @PostMapping("savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")){
            return "Invalid token";
        }
        Optional<UserDetail> userDetail =userService.getUserByPasswordResetToken(token);
        if (userDetail.isPresent()){
            userService.changePasswor(userDetail.get(), passwordModel.getNewPassword());
            return "Password Reset Succesfuly";
        }else {
            return "Invalid token";
        }
    }
    @PostMapping("/changepassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        UserDetail userDetail = userService.findUseByEmail(passwordModel.getEmail());
        if (!userService.checkIfValidOldPassword(userDetail,passwordModel.getOldPassword())){
            return "Invalid password";
        }
        userService.changePasswor(userDetail,passwordModel.getNewPassword());
        return "Password change Succesfuly";
    }

    private String passwordResetTokenMail(UserDetail userDetail, String applicationUrl, String token) {
        String url = applicationUrl + "savePasswor?token="
                + token;
        log.info("Click the reset your password{}" ,url);
        return url;
    }

    private void resendVerificationTokenMail(UserDetail user, String applicationUrl,
                                             VerificationToken verificationToken) {
        String url = applicationUrl + "verifyRegistration?token="
                + verificationToken.getToken();
        log.info("Click the verify your account{}" ,url);

    }

    private String applicationUrl(HttpServletRequest request) {

        return " http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                "/"+
                request.getContextPath();
    }
}
