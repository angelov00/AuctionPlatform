package com.springproject.auctionplatform.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, BannedUserFilter bannedUserFilter) throws Exception {
        http.authorizeHttpRequests(configurer ->
                configurer
                    .requestMatchers("/auth/register", "/home", "/", "/auction", "/auction/details/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form
                .loginPage("/auth/login")
                .failureUrl("/auth/login?error")
                .defaultSuccessUrl("/home", true)
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .permitAll());

        http.addFilterBefore(bannedUserFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BannedUserFilter bannedUserFilter() {
        return new BannedUserFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
}
