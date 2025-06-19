package com.cakeplanner.cake_planner.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavController {
    @GetMapping("/cakes")
    public String cakes(Model model){
        return "home";
    }
    @GetMapping("/recipes")
    public String recipes(Model model){
        return "recipes";
    }

    @GetMapping("/shopping")
    public String shopping(Model model){
        return "shopping";
    }

    @GetMapping("/profile")
    public String profile(Model model){
        return "profile";
    }
}
