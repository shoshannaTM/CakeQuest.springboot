package com.cakeplanner.cake_planner.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {
    @GetMapping("/profile/update")
    public String showUpdateForm(Model model) {
        // Optionally add a user object here
        // model.addAttribute("user", new User());
        model.addAttribute("showMessage", true);  // Boolean to control visibility
        return "userForm";
    }

    //example of manipulating the model based on where it was clicked into from. FIXME
   /* @GetMapping("/update-info")
    public String showUpdateForm(Model model) {
        model.addAttribute("formTitle", "Update Your Info");
        model.addAttribute("buttonLabel", "Save Changes");
        // add existing user to pre-fill the form
        model.addAttribute("user", currentUser);
        return "userForm";
    }

    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {
        model.addAttribute("formTitle", "Create a New Account");
        model.addAttribute("buttonLabel", "Sign Up");
        model.addAttribute("user", new User()); // empty form
        return "userForm";
    }*/
}
