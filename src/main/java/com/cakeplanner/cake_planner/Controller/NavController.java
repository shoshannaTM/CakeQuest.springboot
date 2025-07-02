package com.cakeplanner.cake_planner.Controller;
import com.cakeplanner.cake_planner.Model.Entities.DummyCakes;
import com.cakeplanner.cake_planner.Model.Entities.User;
import jakarta.servlet.http.HttpSession;
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
    public String showCakes(Model model, HttpSession session) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");

        List<DummyCakes> cakes = new ArrayList<>();

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // logged in
            model.addAttribute("user", loggedInUser);
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
        } else {
            // not logged in
            return "redirect:/welcome";
        }
    }

    @GetMapping("/recipes")
    public String recipes(Model model, HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            return "recipes";
        } else {
            // not logged in
            return "redirect:/welcome";
        }
    }

    @GetMapping("/shopping")
    public String shopping(Model model, HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            return "shopping";
        } else {
            // not logged in
            return "redirect:/welcome";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            model.addAttribute("user", loggedInUser);
            return "profile";
        } else {
            // not logged in
            return "redirect:/welcome";
        }
    }
}
