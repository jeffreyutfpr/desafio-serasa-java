package com.serasa.desafio.config;

import com.serasa.desafio.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private final AuthenticationEntryPoint authenticationEntryPoint = (request, response, authException) -> {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Unauthenticated\"}");
    };

    private final AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> {
        response.setStatus(403);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Forbidden\"}");
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] whitelist = new String[] {
                "/auth/login",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/actuator/health",
                "/h2-console/**"
        };

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whitelist).permitAll()
                        .requestMatchers(HttpMethod.GET, "/pessoas/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST, "/pessoas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/pessoas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/pessoas/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        var user = User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, AuthenticationProvider provider) throws Exception {
        var auth = http.getSharedObject(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder.class);
        auth.authenticationProvider(provider);
        return auth.build();
    }
}
