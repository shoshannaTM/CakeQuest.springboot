package com.cakeplanner.cake_planner.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model){
            model.addAttribute("message", "Welcome to Cake Planner!");
            return "layout"; // Spring will look for home.html in your templates folder
    }


}
