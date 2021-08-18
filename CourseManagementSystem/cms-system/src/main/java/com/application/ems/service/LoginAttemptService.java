package com.application.ems.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service     //our service is technically a cache
public class LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPT = 5;
    private static final int  ATTEMPT_INCREMENT = 1;
    //class from guave dependecy, we are defining key and value for the cashe
    private LoadingCache<String, Integer> loginAttemptCache;


    // this is how casche are inizilize.... All were doing here is inizilazing the cache. this is what the documentation says to do
    public LoginAttemptService(){
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() { //maximumSize at any given time we will only have 100 caches (users)
                    public Integer load(String key){
                        return 0;
                    }
                });
    }

    //remove the user from the cache
    public void evictUserFromLoginAttemptCache(String username){
        loginAttemptCache.invalidate(username);
    }

    //add user to the cache
    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;

        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);//increments the login attempts
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username,attempts);
    }

    //check if they exceeded the number of attempts they are allowed too
    public boolean hasExceededMaxAttempt(String username)   {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPT;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}






















