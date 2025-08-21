package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.ShoppingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShoppingController {
    private ShoppingService shoppingService;

    public ShoppingController(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @PostMapping("/shopping/custom_list")
    public String showUpdateForm(@RequestParam(value = "selectedCakeIds", required = false) List<Long> selectedCakeIds,
                                 RedirectAttributes redirectAttributes,
                                 @ModelAttribute("user") User user) {
        if(selectedCakeIds.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Please select more than one cake to make custom list tasks.");
            return "redirect:/shopping";
        }

        shoppingService.idsToShoppingList(selectedCakeIds, user);
        return "redirect:/shopping";
    }
}

