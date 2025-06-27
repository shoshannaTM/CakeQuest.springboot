package com.cakeplanner.cake_planner.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShoppingController {


    //FIXME
    @GetMapping("/shopping/1")
    public String showUpdateForm(Model model) {
        return "shoppingList";
    }
}

