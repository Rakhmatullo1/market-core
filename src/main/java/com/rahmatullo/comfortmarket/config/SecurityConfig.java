package com.rahmatullo.comfortmarket.config;

import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.CustomAccessDeniedHandler;
import com.rahmatullo.comfortmarket.service.exception.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationConfig applicationConfig;
    private final AuthFilter authFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(r->
                        {
                            r.requestMatchers("/auth/**").permitAll();
                            r.requestMatchers(HttpMethod.GET,"/users/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers("/users/**").hasAuthority(UserRole.OWNER.name());
                            r.requestMatchers("/category/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers( "/premise/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers("/premise").hasAuthority(UserRole.OWNER.name());
                            r.requestMatchers("/products/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                        }
                )
                .exceptionHandling(ex->ex.accessDeniedHandler(accessDeniedHandler()).authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(applicationConfig.provider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
