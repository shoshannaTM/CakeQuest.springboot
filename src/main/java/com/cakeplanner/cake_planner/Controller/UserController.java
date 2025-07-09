package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.SignUpDTO;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    /* FIXME
    @GetMapping("/profile/update")
    public String showUpdateForm(@ModelAttribute("formObject") User user, Model model){
        model.addAttribute("formObject", user);
        model.addAttribute("mode", "update");
        return "userForm";
    }*/

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("formObject", new SignUpDTO()); // form needs empty user
        model.addAttribute("signUpMessage", true);
        model.addAttribute("mode", "signup");
        return "userForm";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute("formObject") SignUpDTO signUpDTO, BindingResult result) {

        //FIXME error printing
        if (result.hasErrors()) {
            System.out.println("Binding error on sign up");
            return "/signup";
        }

        User user = new User();
        user.setEmail(signUpDTO.getEmail());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        userService.save(user);

        System.out.println(user.getEmail());
        //Success, go to home page
        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User()); // form needs empty user
        return "login";
    }

}
