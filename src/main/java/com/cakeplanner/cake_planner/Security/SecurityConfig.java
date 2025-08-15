package com.cakeplanner.cake_planner.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    @Bean
  public SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
      // CSRF: enable by default; if your login form doesn't post the token yet,
      // temporarily ignore those endpoints (remove this ignore when you add the token).
      .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .ignoringRequestMatchers("/login", "/logout", "/signup") // TEMP if needed
      )

      // Static assets + public pages
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup", "/login", "/welcome", "/styles.css",
                                "/images/**", "/manifest.json", "/js/main.js", "/js/sw.js").permitAll()
                        .anyRequest().authenticated()
                )
                //redirects unauthenticated users to welcome instead of default login
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/welcome"))
                )
                .formLogin(form -> form
                                .loginPage("/login").permitAll()
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/", true)       // <<< after login, always go home
                                .failureUrl("/login?error=true")
                            )
                                            .logout(logout -> logout
                                            .logoutUrl("/logout")
                                            .logoutSuccessUrl("/welcome")
                                                            .deleteCookies("JSESSIONID")
                                                    .invalidateHttpSession(true)
                );
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
