package com.application.ems.listener;

import com.application.ems.model.User;
import com.application.ems.model.UserPrincipal;
import com.application.ems.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;


    @EventListener//AuthenticationFailureBadCredentialsEvent is the event that is fired when a user successfully to logins
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal(); //this is a string but getPrincipal only returns a object
        if(principal instanceof UserPrincipal){
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
