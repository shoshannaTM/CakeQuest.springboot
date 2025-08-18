// src/main/java/com/cakeplanner/cake_planner/Security/SecurityConfig.java
package com.cakeplanner.cake_planner.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Toggle this in application-dev.properties only
    @Value("${app.debug-auth:false}")
    private boolean exposeSpecificLoginErrors;

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // must load by email and throw UsernameNotFoundException when not found
        provider.setPasswordEncoder(encoder);
        provider.setHideUserNotFoundExceptions(false); // <- IMPORTANT
        return provider;
    }

    @Bean
    public SecurityFilterChain security(HttpSecurity http,
                                        DaoAuthenticationProvider authProvider) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/signup", "/login", "/welcome",
                                "/styles.css", "/images/**", "/manifest.json",
                                "/js/main.js", "/js/sw.js"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/welcome"))
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(new CustomAuthenticationFailureHandler(exposeSpecificLoginErrors))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/welcome")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )
                .authenticationProvider(authProvider);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // defaults to strength 10; fine
    }
}
