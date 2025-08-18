package com.cakeplanner.cake_planner.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final boolean exposeSpecificMessageToUser;

    public CustomAuthenticationFailureHandler(boolean exposeSpecificMessageToUser) {
        this.exposeSpecificMessageToUser = exposeSpecificMessageToUser;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException ex)
            throws IOException, ServletException {

        String reason;
        if (ex instanceof UsernameNotFoundException) {
            reason = "No account found with that email.";
        } else if (ex instanceof BadCredentialsException) {
            reason = "Incorrect password.";
        } else if (ex instanceof LockedException) {
            reason = "Your account is locked.";
        } else if (ex instanceof DisabledException) {
            reason = "Your account is disabled.";
        } else if (ex instanceof AccountExpiredException) {
            reason = "Your account has expired.";
        } else if (ex instanceof CredentialsExpiredException) {
            reason = "Your password has expired.";
        } else {
            reason = "Login failed. Please try again.";
        }

        // Always store the specific reason for debugging
        request.getSession().setAttribute("LOGIN_ERROR_DETAIL", reason);

       // String userMsg = exposeSpecificMessageToUser
         //       ? reason
        //        : "Incorrect email or password.";

        String redirect = "/login?error=" + URLEncoder.encode(reason, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + redirect);
    }
}
