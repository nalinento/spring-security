package com.oauth.two.cryptoagent.controller;


import com.oauth.two.cryptoagent.entity.PasswordModel;
import com.oauth.two.cryptoagent.entity.UserDetail;
import com.oauth.two.cryptoagent.entity.VerificationToken;
import com.oauth.two.cryptoagent.event.RegistrationCompleteEvent;
import com.oauth.two.cryptoagent.model.UserModel;
import com.oauth.two.cryptoagent.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Properties;
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

    private void sentItToMail(String url, String email) {
        String host ="smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session =Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("nalinento@gmail.com","rjxdksbcjsvcfrgj");

            }
        });

        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);

        try {

            message.setFrom(new InternetAddress("Crypto-Agent"));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(email));
            message.setSubject("Confirm");
            message.setText(url);
            Transport.send(message);
            System.out.println("send sucssesfullly");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @PostMapping("/savePassword")
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
        String url = applicationUrl + "savePassword?token="
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
