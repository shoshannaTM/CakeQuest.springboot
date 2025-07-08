package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/welcome")
    public String home(HttpSession session) {
        return "welcome";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/welcome";
    }

    @GetMapping("/profile/update")
    public String showUpdateForm(Model model) {
        model.addAttribute("mode", "update");
        return "userForm";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User()); // form needs empty user
        model.addAttribute("signUpMessage", true);
        model.addAttribute("mode", "signup");
        return "userForm";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute("user") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user); // your service layer logic

        return "redirect:/"; // or another success page
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User()); // form needs empty user
        return "login";
    }

    /*
    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("user") User loginAttempt, Model model, HttpSession session) {

        if (loginAttempt == null) {
            System.out.println("💥 loginAttempt is null");
            return "login";
        }

        System.out.println("📥 Email: " + loginAttempt.getEmail());
        System.out.println("🔐 Password: " + loginAttempt.getPassword());

        User existingUser = userService.findByEmail(loginAttempt.getEmail());

        if (existingUser != null && passwordEncoder.matches(loginAttempt.getPassword(), existingUser.getPassword())) {
            // login success
            session.setAttribute("loggedInUser", existingUser);
            return "redirect:/";
        } else {
            // login failed
            model.addAttribute("loginError", "Invalid email or password");
            return "login";
        }
    }*/
}
