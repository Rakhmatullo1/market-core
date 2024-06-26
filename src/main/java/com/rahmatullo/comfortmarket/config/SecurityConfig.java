package com.rahmatullo.comfortmarket.config;

import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.CustomAccessDeniedHandler;
import com.rahmatullo.comfortmarket.service.exception.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationConfig applicationConfig;
    private final AuthFilter authFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                .csrf(AbstractHttpConfigurer::disable)
                .cors(x->x.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("*"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowedHeaders(List.of("*"));
                    return configuration;
                }))
                .authorizeHttpRequests(r->
                        {
                            r.requestMatchers("/auth/**", "/swagger-ui/**").permitAll();
                            r.requestMatchers(HttpMethod.GET,"/users/**").hasAnyAuthority(UserRole.ADMIN.name() ,UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers("/users/func").hasAuthority(UserRole.WORKER.name());
                            r.requestMatchers("/users/**").hasAnyAuthority(UserRole.ADMIN.name(), UserRole.OWNER.name());
                            r.requestMatchers("/category/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers( HttpMethod.GET, "/premise/**" ).hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers("/premise/{id}/add-product/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers("/premise/**").hasAuthority(UserRole.OWNER.name());
                            r.requestMatchers(HttpMethod.GET, "/products/assets/photos/**").permitAll();
                            r.requestMatchers("/products/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers("/invoice").hasAnyAuthority(UserRole.OWNER.name(), UserRole.WORKER.name());
                            r.requestMatchers("/invoice/make-decision/**").hasAuthority(UserRole.OWNER.name());
                            r.requestMatchers("/product_info/**").hasAnyAuthority(UserRole.OWNER.name(), UserRole.ADMIN.name());
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("PATCH");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.setAllowedOrigins(List.of("*"));
        corsConfig.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
