package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/welcome")
    public String home(Model model, HttpSession session){
        session.invalidate();
        return "welcome";
    }
    @GetMapping("/profile/update")
    public String showUpdateForm(Model model, HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        model.addAttribute("user", loggedInUser);
        model.addAttribute("mode", "update");

        return "userForm";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User()); // Must match th:object
        model.addAttribute("signUpMessage", true);
        model.addAttribute("mode", "signup");
        return "userForm";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute("user") User user) {
        userService.save(user); // your service layer logic
        return "redirect:/login"; // or another success page
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User()); // matches th:object="${user}"
        return "login"; // your Thymeleaf template name
    }
    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("user") User loginAttempt, Model model, HttpSession session) {
        User existingUser = userService.findByEmail(loginAttempt.getEmail());

        if (existingUser != null && existingUser.getPassword().equals(loginAttempt.getPassword())) {
            // login successful (FIXME later hash passwords!)
            session.setAttribute("loggedInUser", existingUser);
            return "redirect:/";
        } else {
            // login failed
            model.addAttribute("loginError", "Invalid email or password");
            return "loginForm";
        }
    }


}
