package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Repositories.CakeOrderRepository;
import com.cakeplanner.cake_planner.Model.Repositories.CakeTaskRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;

@Service
public class CakeOrderService {

    @Autowired
    CakeOrderRepository cakeOrderRepository;

    @Autowired
    CakeTaskRepository cakeTaskRepository;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    SpoonacularService spoonacularService;

    public void createCakeOrderFromForm(String cakeName, LocalDateTime dueDate, int cakeRecipeId,
                                        double cakeMultiplier, int fillingRecipeId, double fillingMultiplier,
                                        int frostingRecipeId, double frostingMultiplier, String dietaryRestriction,
                                        String decorationNotes, User user){
        Recipe cakeRecipe = recipeRepository.findRecipeByRecipeId(cakeRecipeId);
        Recipe fillingRecipe = recipeRepository.findRecipeByRecipeId(fillingRecipeId);
        Recipe frostingRecipe = recipeRepository.findRecipeByRecipeId(frostingRecipeId);
        CakeOrder cakeOrder = new CakeOrder(user, cakeName, dueDate, cakeRecipe, cakeMultiplier, fillingRecipe, fillingMultiplier,
                frostingRecipe, frostingMultiplier, dietaryRestriction, decorationNotes);
        save(cakeOrder);
    }

    public void save(CakeOrder cakeOrder) {
        buildShoppingList(recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getCakeRecipe().getRecipeId()),
                          recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getFillingRecipe().getRecipeId()),
                          recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getFrostingRecipe().getRecipeId()),
                          cakeOrder);
        cakeOrderRepository.save(cakeOrder);

        List<CakeTask> cakeTasks = new ArrayList<>();
        cakeTasks.add(createPantryTask(cakeOrder));
        cakeTasks.add(createShoppingTask(cakeOrder));
        cakeTasks.add(createBakingTask(cakeOrder));
        cakeTasks.add(createFillingTask(cakeOrder));
        cakeTasks.add(createFrostingTask(cakeOrder));
        cakeTasks.add(createDecoratingTask(cakeOrder));

        for(CakeTask ct: cakeTasks){
            cakeTaskRepository.save(ct);
        }
    }

    public void buildShoppingList(List<RecipeIngredient> cakeIngredients,
                                        List<RecipeIngredient> fillingIngredients,
                                        List<RecipeIngredient> frostingIngredients,
                                        CakeOrder cakeOrder) {

        Map<String, ShoppingListItem> itemMap = new HashMap<>();

        ShoppingList shoppingList = cakeOrder.getShoppingList();

        // If no shopping list exists yet, create and attach it
        if (shoppingList == null) {
            shoppingList = new ShoppingList(cakeOrder, new ArrayList<>());
            cakeOrder.setShoppingList(shoppingList);
        }

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
            shoppingList.getItems().clear();
            shoppingList.getItems().addAll(itemMap.values());
    }

    public List<CakeOrderDTO> getCakeDTOs(User user) {
        List<CakeOrderDTO> cakeDTOs = new ArrayList<>();
        List<CakeOrder> cakes = cakeOrderRepository.findByUser(user);
        for(CakeOrder cakeOrder: cakes){
            CakeOrderDTO cakeDTO = cakeOrderIdToDTO(cakeOrder.getCakeId());
            cakeDTOs.add(cakeDTO);
        }
        return cakeDTOs;
    }

    public CakeOrderDTO cakeOrderIdToDTO(int id){
        Optional<CakeOrder> cakeOrderOptional = cakeOrderRepository.findById(id);

        if (cakeOrderOptional.isEmpty()) {
            CakeOrderDTO empty = new CakeOrderDTO();
            return empty;
        }
        CakeOrder cakeOrder = cakeOrderOptional.get();
        CakeOrderDTO cakeDTO = new CakeOrderDTO(cakeOrder.getCakeId(), cakeOrder.getCakeName(), cakeOrder.getDueDate(),
                cakeOrder.getCakeRecipe(), cakeOrder.getCakeMultiplier(),
                cakeOrder.getFillingRecipe(), cakeOrder.getFillingMultiplier(),
                cakeOrder.getFrostingRecipe(), cakeOrder.getFrostingMultiplier(),
                cakeOrder.getDietaryRestriction(), cakeOrder.getDecorationNotes());
        return cakeDTO;
    }

   public CakeTask createPantryTask(CakeOrder cakeOrder) {
       String pantryTaskName = "Check Your Pantry For Ingredients For " + cakeOrder.getCakeName();
       CakeTask pantry = new CakeTask(cakeOrder.getUser(), cakeOrder, pantryTaskName, TaskType.SHOP_PANTRY,
               cakeOrder.getShoppingList(), cakeOrder.getDietaryRestriction(),
               cakeOrder.getDueDate().minusDays(3), false);
       return pantry;
   }

    public CakeTask createShoppingTask(CakeOrder cakeOrder) {
        String shopTaskName = "Buy Ingredients For " + cakeOrder.getCakeName();
        CakeTask shopping = new CakeTask(cakeOrder.getUser(), cakeOrder, shopTaskName, TaskType.SHOP_STORE,
                cakeOrder.getShoppingList(), cakeOrder.getDietaryRestriction(),
                cakeOrder.getDueDate().minusDays(3), false);
        return shopping;
    }

    public CakeTask createBakingTask(CakeOrder cakeOrder) {
        String bakeTaskName = "Bake & Chill Cakes for " + cakeOrder.getCakeName();
        CakeTask bake = new CakeTask(cakeOrder.getUser(), cakeOrder, bakeTaskName, TaskType.BAKE,
                cakeOrder.getCakeRecipe(), cakeOrder.getDietaryRestriction(),
                cakeOrder.getDueDate().minusDays(2), false);
        return bake;
    }

    public CakeTask createFillingTask(CakeOrder cakeOrder) {
        String fillingTaskName = "Make & Chill Filling for " + cakeOrder.getCakeName();
        CakeTask filling = new CakeTask(cakeOrder.getUser(), cakeOrder,fillingTaskName, TaskType.MAKE_FILLING,
                cakeOrder.getFillingRecipe(), cakeOrder.getDietaryRestriction(),
                cakeOrder.getDueDate().minusDays(1), false);
        return filling;
    }

    public CakeTask createFrostingTask(CakeOrder cakeOrder) {
        String frostingTaskName = "Make Frosting for " + cakeOrder.getCakeName();
        CakeTask frosting = new CakeTask(cakeOrder.getUser(), cakeOrder, frostingTaskName, TaskType.MAKE_FROSTING,
                cakeOrder.getFrostingRecipe(), cakeOrder.getDietaryRestriction(),
                cakeOrder.getDueDate().minusHours(5), false);
        return frosting;
    }

    public CakeTask createDecoratingTask(CakeOrder cakeOrder) {
        String decorateTaskName = "Assemble & Decorate " + cakeOrder.getCakeName();
        CakeTask decorate = new CakeTask(cakeOrder.getUser(), cakeOrder, decorateTaskName,
                TaskType.DECORATE, cakeOrder.getDietaryRestriction(),
                cakeOrder.getDecorationNotes(), cakeOrder.getDueDate().minusHours(2), false);
        return decorate;
    }
}
