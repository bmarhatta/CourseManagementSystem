package com.application.ems.configuration;

import com.application.ems.constant.SecurityConstant;
import com.application.ems.filter.JwtAcessDeniedHandler;
import com.application.ems.filter.JwtAuthenticationEntryPoint;
import com.application.ems.filter.JwtAuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)//allow secuirty on a method level
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private JwtAuthorizationFilter jwtAuthorizationFilter;
    private JwtAcessDeniedHandler jwtAcessDeniedHandler;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;





    //we got to tell witch userdetailservice we are using
    //gotta pass in byteEncoder
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //were telling spring to use our userDetailService...
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    //we have to pass in everything we want spring secuirty to manage
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //cors. specific url to access our api
        http.csrf().disable().cors().and()
                //statesless because we are not tracking anyone in our application. thats what the jwt token is for
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URLS).permitAll() //antmatcher that access all urls
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(jwtAcessDeniedHandler) //this is just going to return unauthrized 401 to the user
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
