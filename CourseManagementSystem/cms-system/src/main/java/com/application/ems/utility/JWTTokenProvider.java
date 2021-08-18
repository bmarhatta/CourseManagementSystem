package com.application.ems.utility;

import static com.application.ems.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

import com.application.ems.model.UserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {

    //keep this secure. we can implement a secure way to store it latter
    @Value("${jwt.secret}")
    private String secret;

    //generate token
    public String generateJwtToken(UserPrincipal userPrincipal){
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(GET_CMS)
                .withAudience(GET_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    //gets authorities from the token
    public List<GrantedAuthority> getAuthorities (String token){
        //get all the claims
        String[] claims = getClaimsFromToken(token);
        //creat object of simplegrantedauthority for everySingle claim
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    //get authentication from the user if the the token was verified .
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request){
        //setting up information about this user
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username,null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }


    //cheack to see if the token valid
    public boolean isTokenValid(String username, String token){
        JWTVerifier verifier = getJWTVerifier();
        //apache commons lang 3.0
        //this method checks for null or empty string... if anything is there it will return true
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    //gets subject
    public String getSubject (String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    //check the date to make sure its not after the expiration date of the token
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(GET_CMS).build();
        }catch (JWTVerificationException exception){
            //we use a custom string so end user cannot see the promblem if one came up
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    //returns array of claims as string
    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for(GrantedAuthority grantedAuthority : user.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }



}

