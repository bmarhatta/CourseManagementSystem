package com.application.ems.listener;

import com.application.ems.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

//this is whats going to fire everytime a user fails to log into the application
@Component
@AllArgsConstructor
public class AuthenticationFailureListener {
    private LoginAttemptService loginAttemptService;

    //this method adds user to cache once they fail a log in attempt
    @EventListener//AuthenticationFailureBadCredentialsEvent is the event that is fired when a user fails to logins
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event){
        Object principal = event.getAuthentication().getPrincipal(); //this is a string but getPrincipal only returns a object
        if(principal instanceof String){ //
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
