package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.PasswordDTO;
import com.cakeplanner.cake_planner.Model.DTO.SignUpDTO;
import com.cakeplanner.cake_planner.Model.DTO.UpdateDTO;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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

    @GetMapping("/profile/update")
    public String showUpdateForm(Model model) {
        // reuse current user
        model.addAttribute("formObject", model.getAttribute("user"));
        model.addAttribute("mode", "update");
        return "userForm";
    }

    @PostMapping("/profile/update")
    public String handleUpdate(@Valid @ModelAttribute("formObject") UpdateDTO updateDTO,
                               BindingResult result, Model model,  @ModelAttribute("user") User user) {
        if (result.hasErrors()) {
            //FIXME remove
            System.out.println("Binding error on update:");
            result.getAllErrors().forEach(error -> {
                System.out.println(error.toString());
            });
            model.addAttribute("formObject", updateDTO);
            model.addAttribute("mode", "update");
            return "userForm";
        }

        user.setEmail(updateDTO.getEmail());
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        userService.save(user);
        return "profile";
    }

    @GetMapping("/profile/update-password")
    public String showPasswordForm(Model model){
        model.addAttribute("passwordFormObject", new PasswordDTO());
        return "passwordForm";
    }

    @PostMapping("/profile/update-password")
    public String handlePasswordChange(@Valid @ModelAttribute("passwordFormObject") PasswordDTO passwordDTO,
                                       BindingResult result, Model model, @ModelAttribute("user") User user) {
        if (result.hasErrors()) {
            //FIXME remove
            System.out.println("Binding error on password change");
            return "passwordForm";
        }

        if (passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword()) &&
                passwordDTO.getEmail().equals(user.getEmail())) {

            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            userService.save(user); // <- Save updated password!

            model.addAttribute("mode", "newPassword");
            return "login";
        }


        model.addAttribute("error", "Current password or email is incorrect.");
        return "passwordForm";
    }



    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("formObject", new SignUpDTO()); // form needs empty user
        model.addAttribute("signUpMessage", true);
        model.addAttribute("mode", "signup");
        return "userForm";
    }

    @PostMapping("/signup")
    public String handleSignup(@Valid @ModelAttribute("formObject") SignUpDTO signUpDTO, BindingResult result, Model model) {

        //FIXME error printing
        if (result.hasErrors()) {
            //FIXME remove
            System.out.println("Binding error on sign up");
            result.getAllErrors().forEach(error -> {
                System.out.println(error.toString());
            });
            return "welcome";
        }

        User user = new User();
        user.setEmail(signUpDTO.getEmail());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        userService.save(user);

        //FIXME remove
        System.out.println(user.getEmail());
        //Success, go to login
        model.addAttribute("mode", "signup");
        return "login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User()); // form needs empty user
        return "login";
    }
}
