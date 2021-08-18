package com.application.ems.filter;

import static com.application.ems.constant.SecurityConstant.*;

import com.application.ems.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component //this bean is gong to fire everytime there is a new request... and its only gonna fire once
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JWTTokenProvider jwtTokenProvider;

    @Override //check to make sure the token/user is valid. once we verify that information we can set the user as the authenticated user
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //checks to see if it is options. if so we pass a http ok value.
        if(request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)){
            response.setStatus(HttpStatus.OK.value());
        } else {
            //otherwise we going to try to grab the authorization header
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            //if the header is null or does not start with bearer. we know this not our authentication header
            if(authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)){
                filterChain.doFilter(request,response);
                return;
            }
            //we are now trying to get the token
            //remove the bearer infront of the token and leave use with the token
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username = jwtTokenProvider.getSubject(token);//this is use attempting to get the username
            //if token is valid, and they do not have authentication in the secuirty context holder
            if(jwtTokenProvider.isTokenValid(username,token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);//gives us all authorties for the user
                Authentication authentication = jwtTokenProvider.getAuthentication(username,authorities,request);//we can then get the authentication
                SecurityContextHolder.getContext().setAuthentication(authentication);//set this user as a authenticated user in the secuirty context user
            } else {
                SecurityContextHolder.clearContext();//clear the context if anything fails.
            }
        }
        filterChain.doFilter(request,response);//we are letting the filter contuniue its cource
    }



}

//if it options. witch is a request that get sent before every request to collect information about the server

//whenever there is a request that goes into the server. the first request is always
//for instance if you request get users, before this is issued. there is a option with the same request but with a different method. the purpose of this is find out informatino about the server
//so when a request comes in with the option method name. we are not going to do anything.

//The SecurityContextHolder is a helper class that provides access to the security context.