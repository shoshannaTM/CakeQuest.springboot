package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.PasswordDTO;
import com.cakeplanner.cake_planner.Model.DTO.SignUpDTO;
import com.cakeplanner.cake_planner.Model.DTO.UpdateDTO;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

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
    public String showUpdateForm(Model model, @RequestParam(value = "backUrl", required = false) String backUrl) {
        // reuse current user
        model.addAttribute("formObject", model.getAttribute("user"));
        model.addAttribute("mode", "update");
        model.addAttribute("backUrl", backUrl);

        return "userForm";
    }

    @PostMapping("/profile/update")
    public String handleUpdate(@Valid @ModelAttribute("formObject") UpdateDTO updateDTO,
                               BindingResult result,
                               @RequestParam(value = "backUrl", required = false) String backUrl,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               @ModelAttribute("user") User user) {
        if (result.hasErrors()) {
            model.addAttribute("formObject", updateDTO);
            model.addAttribute("mode", "update");
            model.addAttribute("backUrl", backUrl);
            model.addAttribute("error", "There was an error, please try again.");
            return "userForm";
        }

        user.setEmail(updateDTO.getEmail());
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        userService.save(user);
        redirectAttributes.addAttribute("success", "Your profile was updated successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/profile/update-password")
    public String showPasswordForm(Model model,
                                   @RequestParam(value = "backUrl", required = false) String backUrl,
                                   @RequestParam(value = "error", required = false) String error){
        model.addAttribute("passwordFormObject", new PasswordDTO());
        model.addAttribute("backUrl", backUrl);
        model.addAttribute("error", error);

        return "passwordForm";
    }

    @PostMapping("/profile/update-password")
    public String handlePasswordChange(@Valid @ModelAttribute("passwordFormObject") PasswordDTO passwordDTO,
                                       @RequestParam(value = "backUrl", required = false) String backUrl,
                                       BindingResult result,
                                       RedirectAttributes redirectAttributes,
                                       Model model,
                                       @ModelAttribute("user") User user) {
        if (result.hasErrors()) {
            model.addAttribute("error", "There was an error updating your password. Ensure the new password is at least 6 characters long.");
            model.addAttribute("backUrl", backUrl);
            model.addAttribute("mode", "newPassword");
            return "passwordForm";
        }
        if (passwordEncoder.matches(passwordDTO.getCurrentPassword(),
                user.getPassword()) && passwordDTO.getEmail().equalsIgnoreCase(user.getEmail())) {

            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            userService.save(user); // <- Save updated password!

            redirectAttributes.addAttribute("mode", "newPassword");
            redirectAttributes.addAttribute("backUrl", backUrl);
            redirectAttributes.addAttribute("success", "Your password was updated successfully.");
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "There was an error updating your password. Ensure the new password is at least 6 characters long.");
            model.addAttribute("mode", "newPassword");
            model.addAttribute("backUrl", backUrl);
            return "passwordForm";
        }
    }



    @GetMapping("/signup")
    public String showSignupForm(Model model, @RequestParam(value = "backUrl", required = false) String backUrl) {
        model.addAttribute("formObject", new SignUpDTO());
        model.addAttribute("mode", "signup");
        model.addAttribute("backUrl", backUrl);

        return "userForm";
    }

    @PostMapping("/signup")
    public String handleSignup(@Valid @ModelAttribute("formObject") SignUpDTO signUpDTO,
                               BindingResult result,
                               @RequestParam(value = "backUrl", required = false) String backUrl,
                               RedirectAttributes redirectAttributes,
                               Model model){

        if (result.hasErrors()) {
            model.addAttribute("error", "There was an error signing up. Ensure you've entered a valid email and your password is at least 6 characters long.");
            model.addAttribute("backUrl", backUrl);
            model.addAttribute("mode", "signup");
            return "userForm";
        }

        User user = new User();
        user.setEmail(signUpDTO.getEmail());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        userService.save(user);

        redirectAttributes.addAttribute("mode", "signup");
        redirectAttributes.addAttribute("backUrl", "/welcome"); // or actual origin
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "mode", required = false) String mode,
                                @RequestParam(value = "error", required = false) String error) {

        model.addAttribute("user", new User());

        if (mode != null && !mode.isEmpty()){
            model.addAttribute("mode", mode);
        }

        if (error != null) {
            model.addAttribute("error", "Incorrect email or password.");
        }

        return "login";
    }
}
