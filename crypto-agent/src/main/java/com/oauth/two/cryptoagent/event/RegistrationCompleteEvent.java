package com.oauth.two.cryptoagent.event;

import com.oauth.two.cryptoagent.entity.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;


public class RegistrationCompleteEvent extends ApplicationEvent {

    private UserDetail user;
    private String applicationUlr;

    public RegistrationCompleteEvent(UserDetail user, String applicationUlr) {
        super(user);

        this.user =user;
        this.applicationUlr=applicationUlr;
    }

    public UserDetail getUser() {
        return user;
    }

    public void setUser(UserDetail user) {
        this.user = user;
    }

    public String getApplicationUlr() {
        return applicationUlr;
    }

    public void setApplicationUlr(String applicationUlr) {
        this.applicationUlr = applicationUlr;
    }
}
