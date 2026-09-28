package tn.esprit.medicareaitigerback.security;

import tn.esprit.medicareaitigerback.services.implementaions.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final SecurityUtils securityUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler, SecurityUtils securityUtils,
                          UserDetailsServiceImpl userDetailsService) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.securityUtils = securityUtils;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationJwtTokenFilter authenticationJwtTokenFilter() {
        return new AuthenticationJwtTokenFilter(userDetailsService, securityUtils);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);

        http.authorizeHttpRequests()
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                // Public auth endpoints
                .requestMatchers(
                        "/api/v1/agent/auth/**",
                        "/api/v1/doctor/auth/**",
                        "/api/v1/client/auth/**"
                ).permitAll()
                // Bootstrap
                .requestMatchers(
                        "/api/v1/agent/role/create",
                        "/api/v1/agent/manageUser/registerAgent",
                        "/api/v1/seed/**"
                ).permitAll()
                // Swagger
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/configuration/**"
                ).permitAll()
                // Role-based
                .requestMatchers("/api/v1/agent/manageUser/**").hasRole("AGENT")
                .requestMatchers("/api/v1/agent/role/**").hasRole("AGENT")
                .requestMatchers("/api/v1/agent/reason/**").hasRole("AGENT")
                .requestMatchers("/api/v1/client/**").hasAnyRole("PATIENT", "AGENT")
                .requestMatchers("/api/v1/doctor/**").hasAnyRole("DOCTOR", "AGENT")
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
