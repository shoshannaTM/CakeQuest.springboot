package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Repositories.CakeOrderRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CakeOrderService {

    @Autowired
    CakeOrderRepository cakeOrderRepository;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    SpoonacularService spoonacularService;

    public CakeOrderDTO save(CakeOrder cakeOrder) {
        buildShoppingList(cakeOrder);
        cakeOrderRepository.save(cakeOrder);
        return cakeOrderToDTO(cakeOrder);
    }

    public void buildShoppingList(CakeOrder cakeOrder){
       List<RecipeIngredient> cakeRecipeIngredients = recipeIngredientRepository.
               findRecipeIngredientsByRecipeId(cakeOrder.getCakeRecipe().getRecipeId());
       for(RecipeIngredient ri: cakeRecipeIngredients){
           System.out.println(ri.getQuantity() + " " + ri.getUnit() + " " + ri.getIngredient().getIngredientName());
       }
        System.out.println( "multiplied: ");
        for(RecipeIngredient ri: cakeRecipeIngredients){
            double multQuantity = (ri.getQuantity() * cakeOrder.getCakeMultiplier());
            ri.setQuantity(multQuantity);
            System.out.println(ri.getQuantity() + " " + ri.getUnit() + " " + ri.getIngredient().getIngredientName());
        }

        List<RecipeIngredient> fillingRecipeIngredients = recipeIngredientRepository.
                findRecipeIngredientsByRecipeId(cakeOrder.getFillingRecipe().getRecipeId());

        for(RecipeIngredient ri: fillingRecipeIngredients){
            System.out.println(ri.getQuantity() + " " + ri.getUnit() + " " + ri.getIngredient().getIngredientName());
        }
        System.out.println( "multiplied: ");
        for(RecipeIngredient ri: fillingRecipeIngredients){
            double multQuantity = (ri.getQuantity() * cakeOrder.getFillingMultiplier());
            ri.setQuantity(multQuantity);
            System.out.println(ri.getQuantity() + " " + ri.getUnit() + " " + ri.getIngredient().getIngredientName());
        }

        List<RecipeIngredient> frostingRecipeIngredients = recipeIngredientRepository.
                findRecipeIngredientsByRecipeId(cakeOrder.getFrostingRecipe().getRecipeId());
        for(RecipeIngredient ri: frostingRecipeIngredients){
            System.out.println(ri.getQuantity() + " " + ri.getUnit() + " " + ri.getIngredient().getIngredientName());
        }
        System.out.println( "multiplied: ");
        for(RecipeIngredient ri: fillingRecipeIngredients){
            double multQuantity = (ri.getQuantity() * cakeOrder.getFrostingMultiplier());
            ri.setQuantity(multQuantity);
            System.out.println(ri.getQuantity() + " " + ri.getUnit() + " " + ri.getIngredient().getIngredientName());
        }

    }

   public ShoppingList getShoppingList(List<RecipeIngredient> cakeIngredients,
                                        List<RecipeIngredient> fillingIngredients,
                                        List<RecipeIngredient> frostingIngredients, CakeOrder cakeOrder){
        List<ShoppingListItem> items = new ArrayList<>();
        ShoppingList shoppingList = new ShoppingList(cakeOrder, items);
        for(RecipeIngredient ri: cakeIngredients){
            if(fillingIngredients.contains(ri.getIngredient())){
                int index = fillingIngredients.indexOf(ri.getIngredient());
                RecipeIngredient matching = fillingIngredients.get(index);
                if(ri.getUnit().matches(matching.getUnit())){
                    double newAmount = ri.getQuantity() + matching.getQuantity();
                    ShoppingListItem shoppingListItem = new ShoppingListItem(ri.getIngredient().getIngredientName(),
                                                                            newAmount, ri.getUnit(), shoppingList);
                    items.add(shoppingListItem);
                } else {
                    double conversionRate = spoonacularService.getConversionRate(ri.getIngredient().getIngredientName(),
                            matching.getUnit(), ri.getUnit());
                    double newAmount = matching.getQuantity() * conversionRate;
                    ShoppingListItem shoppingListItem = new ShoppingListItem(ri.getIngredient().getIngredientName(),
                            newAmount, ri.getUnit(), shoppingList);
                    items.add(shoppingListItem);

                }
            } else {
                ShoppingListItem shoppingListItem = new ShoppingListItem(ri.getIngredient().getIngredientName(),
                        ri.getQuantity(), ri.getUnit(), shoppingList);
                items.add(shoppingListItem);
            }
        }
        return shoppingList;
    }

    public List<CakeOrderDTO> getCakeDTOs(User user) {
        List<CakeOrderDTO> cakeDTOs = new ArrayList<>();
        List<CakeOrder> cakes = cakeOrderRepository.findByUser(user);
        for(CakeOrder cakeOrder: cakes){
            CakeOrderDTO cakeDTO = cakeOrderToDTO(cakeOrder);
            cakeDTOs.add(cakeDTO);
        }

        return cakeDTOs;
    }

    public CakeOrderDTO cakeOrderToDTO(CakeOrder cakeOrder){
        CakeOrderDTO cakeDTO = new CakeOrderDTO(cakeOrder.getCakeId(), cakeOrder.getCakeName(), cakeOrder.getDueDate(),
                cakeOrder.getCakeRecipe(), cakeOrder.getCakeMultiplier(),
                cakeOrder.getFillingRecipe(), cakeOrder.getFillingMultiplier(),
                cakeOrder.getFrostingRecipe(), cakeOrder.getFrostingMultiplier(),
                cakeOrder.getDietaryRestriction(), cakeOrder.getDecorationNotes());
        return cakeDTO;
    }

    // Shopping CakeTask -> due 3 days before
    // Cake baking CakeTask -> due 2 days before
    // Make Filling CakeTask -> due 1 day before
    // Make Frosting CakeTask -> due 5 hours before
    // Assemble and Decorate -> due 2 hours before

   public List<CakeTask> createTasksForCake(CakeOrder cakeOrder){
        List<CakeTask> cakeCakeTasks = new ArrayList<>();

        String shopTaskName = "Shop the ingredients for " + cakeOrder.getCakeName();
          CakeTask shopForCake = new CakeTask(cakeOrder.getUser(), shopTaskName, TaskType.SHOP,
                                           cakeOrder.getShoppingList(), cakeOrder.getDietaryRestriction(),
                                           cakeOrder.getDueDate().minusDays(3));
        cakeCakeTasks.add(shopForCake);

        String bakeCakeTaskName = "Bake & Chill Cakes for " + cakeOrder.getCakeName();
        CakeTask bakeCake = new CakeTask(cakeOrder.getUser(), bakeCakeTaskName, TaskType.BAKE,
                                         cakeOrder.getCakeRecipe(), cakeOrder.getDietaryRestriction(),
                                         cakeOrder.getDueDate().minusDays(2));
        cakeCakeTasks.add(bakeCake);

        String makeFillingCakeTaskName = "Make & Chill Filling for " + cakeOrder.getCakeName();
        CakeTask makeFilling = new CakeTask(cakeOrder.getUser(), makeFillingCakeTaskName, TaskType.MAKE_FILLING,
                                            cakeOrder.getFillingRecipe(), cakeOrder.getDietaryRestriction(),
                                            cakeOrder.getDueDate().minusDays(1));
       cakeCakeTasks.add(makeFilling);

        String makeFrostingCakeTaskName = "Make Frosting for " + cakeOrder.getCakeName();
        CakeTask makeFrosting = new CakeTask(cakeOrder.getUser(), makeFrostingCakeTaskName, TaskType.MAKE_FROSTING,
                                             cakeOrder.getFrostingRecipe(), cakeOrder.getDietaryRestriction(),
                                             cakeOrder.getDueDate().minusHours(5));
        cakeCakeTasks.add(makeFrosting);

        String assembleAndDecorateCakeTaskName = "Assemble & Decorate" + cakeOrder.getCakeName();
        CakeTask assembleAndDecorate = new CakeTask(cakeOrder.getUser(), assembleAndDecorateCakeTaskName,
                                                    TaskType.DECORATE,cakeOrder.getDietaryRestriction(),
                                                    cakeOrder.getDecorationNotes(), cakeOrder.getDueDate().minusHours(2));
        cakeCakeTasks.add(assembleAndDecorate);

        return cakeCakeTasks;
    }
}
