package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Repositories.CakeOrderRepository;
import com.cakeplanner.cake_planner.Model.Repositories.CakeTaskRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CakeOrderService {

    @Autowired
    CakeOrderRepository cakeOrderRepository;

    @Autowired
    CakeTaskRepository cakeTaskRepository;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    SpoonacularService spoonacularService;

    public CakeOrderDTO save(CakeOrder cakeOrder) {
        buildShoppingList(recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getCakeRecipe().getRecipeId()),
                          recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getFillingRecipe().getRecipeId()),
                          recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getFrostingRecipe().getRecipeId()),
                          cakeOrder);
        cakeOrderRepository.save(cakeOrder);
        createTasksForCake(cakeOrder);
        return cakeOrderToDTO(cakeOrder);
    }

    public void buildShoppingList(List<RecipeIngredient> cakeIngredients,
                                        List<RecipeIngredient> fillingIngredients,
                                        List<RecipeIngredient> frostingIngredients,
                                        CakeOrder cakeOrder) {

        Map<String, ShoppingListItem> itemMap = new HashMap<>();
        ShoppingList shoppingList = new ShoppingList(cakeOrder, new ArrayList<>());

        // Add entire cake recipe to map
        for (RecipeIngredient ri : cakeIngredients) {
            String name = ri.getIngredient().getIngredientName().toLowerCase();
            double multiplied = ri.getQuantity() * cakeOrder.getCakeMultiplier();
            ShoppingListItem listItem = new ShoppingListItem(name, multiplied, ri.getUnit(), shoppingList);
            itemMap.put(name, listItem);
        }

        // Check map for filling recipe ingredients
        for (RecipeIngredient ri : fillingIngredients) {
            String name = ri.getIngredient().getIngredientName().toLowerCase();
            double multiplied = ri.getQuantity() * cakeOrder.getFillingMultiplier();

            if (itemMap.containsKey(name)) {
                ShoppingListItem existingListItem = itemMap.get(name);
                String unitA = existingListItem.getUnit();
                String unitB = ri.getUnit();

                if (unitB.equalsIgnoreCase(unitA)) {
                    double newAmount = existingListItem.getAmount() + multiplied;
                    existingListItem.setAmount(newAmount);
                    itemMap.put(name, existingListItem);
                } else {
                    double conversionRate = spoonacularService.getConversionRate(
                            name, unitB, unitA); // convert ri unit to match existing
                    double normalizedAmount = multiplied * conversionRate;
                    double newAmount = existingListItem.getAmount() + normalizedAmount;
                    existingListItem.setAmount(newAmount);
                    itemMap.put(name, existingListItem);
                }
            } else {
                ShoppingListItem listItem = new ShoppingListItem(name, multiplied, ri.getUnit(), shoppingList);
                itemMap.put(name, listItem);
            }
        }

        // Check map for frosting recipe ingredients
        for (RecipeIngredient ri : frostingIngredients) {
            String name = ri.getIngredient().getIngredientName().toLowerCase();
            double multiplied = ri.getQuantity() * cakeOrder.getFrostingMultiplier();

            if (itemMap.containsKey(name)) {
                ShoppingListItem existingListItem = itemMap.get(name);
                String unitA = existingListItem.getUnit();
                String unitB = ri.getUnit();

                if (unitB.equalsIgnoreCase(unitA)) {
                    double newAmount = existingListItem.getAmount() + multiplied;
                    existingListItem.setAmount(newAmount);
                    itemMap.put(name, existingListItem);
                } else {
                    double conversionRate = spoonacularService.getConversionRate(
                            name, unitB, unitA); // convert ri unit to match existing
                    double normalizedAmount = multiplied * conversionRate;
                    double newAmount = existingListItem.getAmount() + normalizedAmount;
                    existingListItem.setAmount(newAmount);
                    itemMap.put(name, existingListItem);
                }
            } else {
                ShoppingListItem listItem = new ShoppingListItem(name, multiplied, ri.getUnit(), shoppingList);
                itemMap.put(name, listItem);
            }
        }

        shoppingList.setItems(new ArrayList<>(itemMap.values()));
        cakeOrder.setShoppingList(shoppingList);
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

   public void createTasksForCake(CakeOrder cakeOrder){
        List<CakeTask> cakeTasks = new ArrayList<>();

        String shopTaskName = "Shop the ingredients for " + cakeOrder.getCakeName();
          CakeTask shopForCake = new CakeTask(cakeOrder.getUser(), cakeOrder, shopTaskName, TaskType.SHOP,
                                           cakeOrder.getShoppingList(), cakeOrder.getDietaryRestriction(),
                                           cakeOrder.getDueDate().minusDays(3));
        cakeTasks.add(shopForCake);

        String bakeCakeTaskName = "Bake & Chill Cakes for " + cakeOrder.getCakeName();
        CakeTask bakeCake = new CakeTask(cakeOrder.getUser(), cakeOrder, bakeCakeTaskName, TaskType.BAKE,
                                         cakeOrder.getCakeRecipe(), cakeOrder.getDietaryRestriction(),
                                         cakeOrder.getDueDate().minusDays(2));
        cakeTasks.add(bakeCake);

        String makeFillingCakeTaskName = "Make & Chill Filling for " + cakeOrder.getCakeName();
        CakeTask makeFilling = new CakeTask(cakeOrder.getUser(), cakeOrder, makeFillingCakeTaskName, TaskType.MAKE_FILLING,
                                            cakeOrder.getFillingRecipe(), cakeOrder.getDietaryRestriction(),
                                            cakeOrder.getDueDate().minusDays(1));
       cakeTasks.add(makeFilling);

        String makeFrostingCakeTaskName = "Make Frosting for " + cakeOrder.getCakeName();
        CakeTask makeFrosting = new CakeTask(cakeOrder.getUser(), cakeOrder, makeFrostingCakeTaskName, TaskType.MAKE_FROSTING,
                                             cakeOrder.getFrostingRecipe(), cakeOrder.getDietaryRestriction(),
                                             cakeOrder.getDueDate().minusHours(5));
        cakeTasks.add(makeFrosting);

        String assembleAndDecorateCakeTaskName = "Assemble & Decorate " + cakeOrder.getCakeName();
        CakeTask assembleAndDecorate = new CakeTask(cakeOrder.getUser(), cakeOrder, assembleAndDecorateCakeTaskName,
                                                    TaskType.DECORATE,cakeOrder.getDietaryRestriction(),
                                                    cakeOrder.getDecorationNotes(), cakeOrder.getDueDate().minusHours(2));
        cakeTasks.add(assembleAndDecorate);

        for(CakeTask ct: cakeTasks){
            cakeTaskRepository.save(ct);
        }
    }
}
