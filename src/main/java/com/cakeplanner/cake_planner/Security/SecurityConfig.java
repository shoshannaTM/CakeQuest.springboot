package com.cakeplanner.cake_planner.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // FIXME, only like this for development
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup", "/login", "/welcome", "/styles.css", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                //redirects unauthenticated users to welcome instead of default login
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/welcome"))
                )
                .formLogin(form -> form
                        .loginPage("/login")                     // your custom login page
                        .loginProcessingUrl("/login")            // Spring handles POST here
                        .failureUrl("/login?error=true")         // PRG style redirect on error
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/welcome")
                );
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
