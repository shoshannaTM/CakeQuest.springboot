/*package com.cakeplanner.cake_planner.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")                // apply to all routes
                .excludePathPatterns(
                        "/login", "/login/**",    // 👈 exclude both GET and POST
                        "/signup", "/signup/**",
                        "/welcome",
                        "/styles.css",
                        "/images/**"); // except these
    }
}
*/