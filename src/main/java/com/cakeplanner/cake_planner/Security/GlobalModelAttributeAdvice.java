package com.cakeplanner.cake_planner.Security;

import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

@Autowired
    private UserService userService;
    @ModelAttribute("user")
    public User addUserToModel(Principal principal) {
        if (principal != null) {
            return userService.findByEmail(principal.getName());
        }
        return null;
    }
}


