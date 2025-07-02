package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.Entities.DummyCakes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");

    //FIXME
   /* @GetMapping("/")
    public String showCakes(Model model) {
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
*/

    @GetMapping("/cakeForm")
    public String showUpdateForm(Model model) {
        // Optionally add a user object here
        // model.addAttribute("user", new User());
        return "cakeForm";
    }

    //FIXME
    @GetMapping("cakes/{id}")
    public String viewCakeDetails(@PathVariable int id, Model model) {
        // Recreate the dummy cake with the matching ID
        DummyCakes cake = null;

        if (id == 1) {
            cake = new DummyCakes("Sophie's Unicorn Cake", "Vanilla", "Strawberry Jam", "Buttercream", LocalDateTime.now().plusDays(2).format(formatter), 25, 1);
        } else if (id == 2) {
            cake = new DummyCakes("Dad's Retirement Cake", "Chocolate", "Salted Caramel", "Ganache", LocalDateTime.now().plusDays(5).format(formatter), 60, 2);
        } else if (id == 3) {
            cake = new DummyCakes("Wedding Cake", "Red Velvet", "Cream Cheese", "Fondant", LocalDateTime.now().plusWeeks(1).format(formatter), 90, 3);
        }


        model.addAttribute("cake", cake);
        return "cakeDetails";
    }



}
