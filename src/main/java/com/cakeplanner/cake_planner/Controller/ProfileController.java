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

    //example of manipulating the model based on where it was clicked into from. FIXME SIGN UP WHUAGJGZD
   /* @GetMapping("/signup")
public String showSignupForm(Model model) {
    model.addAttribute("user", new User()); // Must match th:object
    return "userForm"; // must match your .html file name (no .html extension)
}

@PostMapping("/signup")
public String handleSignup(@ModelAttribute("user") User user) {
    userService.save(user); // your service layer logic
    return "redirect:/login"; // or another success page
}

    }*/
}
