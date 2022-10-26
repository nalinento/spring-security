package com.oauth.two.cryptoagent.event.listner;

import com.oauth.two.cryptoagent.entity.UserDetail;
import com.oauth.two.cryptoagent.event.RegistrationCompleteEvent;
import com.oauth.two.cryptoagent.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
public class RegistrationCompleteEventListner implements
        ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // create the verification token user with link
        UserDetail user =event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user,token);

        // send mail the user
        String url = event.getApplicationUlr() + "verifyRegistration?token="
                +token;
        log.info("Click the verify your account{}" ,url);

    }
}
