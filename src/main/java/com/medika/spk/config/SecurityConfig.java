package com.medika.spk.config;

import com.medika.spk.security.JwtFilter;
import com.medika.spk.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(
                    "/", "/index.html", "/login.html",
                    "/assets/**", "/favicon.ico",
                    "/*.html", "/*.js", "/*.css"
                ).permitAll()
                // Allow reads for all authenticated; restrict writes to ADMIN
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/kriteria/**", "/api/sub-kriteria/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST,   "/api/kriteria/**", "/api/sub-kriteria/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT,    "/api/kriteria/**", "/api/sub-kriteria/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/kriteria/**", "/api/sub-kriteria/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasRole("ADMIN")
                .requestMatchers("/api/laporan/**").hasAnyRole("ADMIN", "HRD")
                .requestMatchers("/api/smart/hitung-semua", "/api/smart/hitung/**").hasAnyRole("ADMIN", "HRD")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
