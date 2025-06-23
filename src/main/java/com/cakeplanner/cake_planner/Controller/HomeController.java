package com.cakeplanner.cake_planner.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model){
            //model.addAttribute("message", "Welcome to Cake Planner!");
            return "home"; // Spring will look for home.html in your templates folder
    }

    @GetMapping("/cakes/cakeForm")
    public String showUpdateForm(Model model) {
        // Optionally add a user object here
        // model.addAttribute("user", new User());
        return "cakeForm";
    }


}
