package com.example.backend_staffoji_game.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .authorizeRequests(
                        authorizeRequests -> {
                            authorizeRequests.requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).authenticated();
                            authorizeRequests.anyRequest().permitAll();

                        })
                .oauth2Login(oauth2Login -> oauth2Login
                        .defaultSuccessUrl("http://localhost:8083/swagger-ui/index.htm", true)
                        .successHandler(authenticationSuccessHandler()))
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
}
