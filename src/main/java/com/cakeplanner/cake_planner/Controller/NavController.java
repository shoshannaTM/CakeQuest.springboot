package com.cakeplanner.cake_planner.Controller;
import com.cakeplanner.cake_planner.Entities.DummyCakes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class NavController {
    @GetMapping("/")
    public String showCakes(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");

        List<DummyCakes> cakes = new ArrayList<>();

        cakes.add(new DummyCakes(
                "Sophie's Unicorn Cake",
                "Vanilla",
                "Strawberry Jam",
                "Buttercream",
                LocalDateTime.now().plusDays(2).format(formatter),
                25,
                1
        ));

        cakes.add(new DummyCakes(
                "Dad's Retirement Cake",
                "Chocolate",
                "Salted Caramel",
                "Ganache",
                LocalDateTime.now().plusDays(5).format(formatter),
                60,
                2
        ));

        cakes.add(new DummyCakes(
                "Wedding Cake",
                "Red Velvet",
                "Cream Cheese",
                "Fondant",
                LocalDateTime.now().plusWeeks(1).format(formatter),
                90,
                3
        ));

        model.addAttribute("cakes", cakes);
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
