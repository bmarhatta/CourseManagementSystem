package com.application.ems.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_00; //5DAYS
    public static final String TOKEN_PREFIX= "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String GET_CMS = "This was provided by Cogent's Course Management System!";
    public static final String GET_ADMINISTRATION = "User Management";
    public static final String AUTHORITIES = "AUTHORITIES";
    public static final String FORBIDDEN_MESSAGE ="You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have any permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/user/login", "/user/register","/user/image/**","/user/resetPassword/**"};
}
