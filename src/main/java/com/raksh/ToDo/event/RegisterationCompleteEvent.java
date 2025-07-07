package com.raksh.ToDo.event;

import com.raksh.ToDo.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class RegisterationCompleteEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;

    public RegisterationCompleteEvent(User user,String applicationUrl){
        super(user);
        this.user=user;
        this.applicationUrl=applicationUrl;
    }
}
