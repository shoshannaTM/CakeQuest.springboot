package com.cakeplanner.cake_planner.Controller;
import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.DTO.CakeTaskDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Services.CakeOrderService;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import com.cakeplanner.cake_planner.Model.Services.CakeTaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class NavController {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");
    private final RecipeService recipeService;
    private final CakeOrderService cakeOrderService;
    private final CakeTaskService cakeTaskService;

    public NavController(RecipeService recipeService, CakeOrderService cakeOrderService, CakeTaskService cakeTaskService) {
        this.recipeService = recipeService;
        this.cakeOrderService = cakeOrderService;
        this.cakeTaskService = cakeTaskService;
    }

    @GetMapping("/")
    public String cakes(@ModelAttribute("user") User user,
                             Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");

        List<CakeOrderDTO> cakes = cakeOrderService.getCakeDTOs(user);

        String backUrl = "?backUrl=/";
        model.addAttribute("backUrl", backUrl);

        Map<CakeOrderDTO, Integer> upcomingCakes = cakeTaskService.getUpcomingCakes(cakes);
        model.addAttribute("upcomingCakes", upcomingCakes);

        Map<CakeOrderDTO, Integer> pastCakes = cakeTaskService.getPastCakes(cakes);
        model.addAttribute("pastCakes", pastCakes);

        List<CakeTaskDTO> tasks = cakeTaskService.getCakeTaskDTOsForUser(user);

        List<CakeTaskDTO> toDoTasks = cakeTaskService.getIncompleteTasks(tasks);
        toDoTasks.sort(Comparator.comparing(CakeTaskDTO::getDueDate));

        model.addAttribute("toDoTasks", toDoTasks);

        return "home";
    }

    @GetMapping("/recipes")
    public String recipes(@RequestParam(name = "type", required = false, defaultValue = "all") String type,
                          @RequestParam(name = "query", required = false) String query,
                          @ModelAttribute("user") User user,
                          Model model) {
        List<RecipeDTO> displayRecipes;

        if (query != null && !query.isBlank()) {
            displayRecipes = recipeService.searchRecipes(user, query);
            model.addAttribute("selectedType", "all");
        } else {
            displayRecipes = recipeService.filterRecipes(user, type);
            model.addAttribute("selectedType", type);
        }

        model.addAttribute("recipes", displayRecipes);
        model.addAttribute("query", query);
        return "recipes";
    }


    @GetMapping("/shopping")
    public String shopping(@ModelAttribute("user") User user,
                           Model model){
        List<CakeTaskDTO> shoppingTasks = cakeTaskService.getIncompleteShoppingTasksForUser(user);
        List<CakeOrderDTO> cakes = cakeOrderService.getCakeDTOs(user);

        model.addAttribute("shoppingTasks", shoppingTasks);
        model.addAttribute("cakes", cakes);

        return "shopping";}

    @GetMapping("/profile")
    public String profile(@RequestParam(value = "success", required = false) String success, Model model){
        if(success != null && !success.isEmpty()) {
            model.addAttribute("success", success);
        }
        return "profile";}
}
