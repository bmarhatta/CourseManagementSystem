package com.application.ems.enumeration;


import static com.application.ems.constant.Authority.*;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities){
        this.authorities=authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }

}

//The "Three Dots" in java is called the Variable Arguments or varargs.
// It allows the method to accept zero or multiple arguments.
// Varargs are very helpful if you don't know how many arguments you will have to pass in the method.