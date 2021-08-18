package com.application.ems.filter;

import static com.application.ems.constant.SecurityConstant.*;
import com.application.ems.model.HttpResponse;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
//whenever the uesr fails to get authentication. this will fire
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {


    //if the user is not authenticated. and they try to acess the application. this will get trigered.
    //we just overiding this method so we can send a very nice method to the user
    //instead of calling the default method in http403forbiddenentrypoint. it will fire the one below
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        HttpResponse httpResponse = new HttpResponse(FORBIDDEN.value(), FORBIDDEN, FORBIDDEN.getReasonPhrase().toUpperCase(), FORBIDDEN_MESSAGE);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }


}

//we imported httpstatus, and secuirtyconstant to make it look nicer


