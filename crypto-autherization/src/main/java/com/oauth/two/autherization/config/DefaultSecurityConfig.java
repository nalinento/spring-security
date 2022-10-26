package com.oauth.two.autherization.config;

import com.oauth.two.autherization.service.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class DefaultSecurityConfig {


    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http.authorizeRequests(aurhorizeRequest ->aurhorizeRequest.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }
    @Autowired
    public void bindAuthenticationProvider(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(customAuthenticationProvider);
    }
}
