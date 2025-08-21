package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;

@Service
public class CakeOrderService {
    private final RecipeRepository recipeRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final CakeTaskRepository cakeTaskRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRecipeIngredientRepository userRecipeIngredientRepository;
    private final CakeOrderRepository cakeOrderRepository;
    private final SpoonacularService spoonacularService;

    public CakeOrderService(RecipeRepository recipeRepository,
                            UserRecipeRepository userRecipeRepository,
                            CakeTaskRepository cakeTaskRepository,
                            RecipeIngredientRepository recipeIngredientRepository,
                            IngredientRepository ingredientRepository,
                            UserRecipeIngredientRepository userRecipeIngredientRepository,
                            CakeOrderRepository cakeOrderRepository, SpoonacularService spoonacularService) {
        this.recipeRepository = recipeRepository;
        this.userRecipeRepository = userRecipeRepository;
        this.cakeTaskRepository = cakeTaskRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRecipeIngredientRepository = userRecipeIngredientRepository;
        this.cakeOrderRepository = cakeOrderRepository;
        this.spoonacularService = spoonacularService;
    }

    public void createCakeOrderFromForm(String cakeName, LocalDateTime dueDate, Long cakeRecipeId,
                                        double cakeMultiplier, Long fillingRecipeId, double fillingMultiplier,
                                        Long frostingRecipeId, double frostingMultiplier, String dietaryRestriction,
                                        String decorationNotes, User user){
        Optional<UserRecipe> cakeRecipeOptional = userRecipeRepository.findByUserRecipeId(cakeRecipeId);
        Optional<UserRecipe> fillingRecipeOptional = userRecipeRepository.findByUserRecipeId(fillingRecipeId);
        Optional<UserRecipe> frostingRecipeOptional = userRecipeRepository.findByUserRecipeId(frostingRecipeId);

        if(cakeRecipeOptional.isPresent() && fillingRecipeOptional.isPresent() && frostingRecipeOptional.isPresent()) {
            UserRecipe cakeRecipe = cakeRecipeOptional.get();
            UserRecipe fillingRecipe = fillingRecipeOptional.get();
            UserRecipe frostingRecipe = frostingRecipeOptional.get();

            CakeOrder cakeOrder = new CakeOrder(user, cakeName,dueDate, cakeRecipe, fillingRecipe, frostingRecipe,
                    cakeMultiplier, fillingMultiplier, frostingMultiplier, dietaryRestriction, decorationNotes);

            save(cakeOrder);
        }
    }

    public void save(CakeOrder cakeOrder) {
        buildShoppingList(
                userRecipeIngredientRepository.findAllByUserRecipe_UserRecipeId(cakeOrder.getCakeRecipe().getUserRecipeId()),
                userRecipeIngredientRepository.findAllByUserRecipe_UserRecipeId(cakeOrder.getFillingRecipe().getUserRecipeId()),
                userRecipeIngredientRepository.findAllByUserRecipe_UserRecipeId(cakeOrder.getFrostingRecipe().getUserRecipeId()),
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

    public void buildShoppingList(List<UserRecipeIngredient> cakeIngredients,
                                        List<UserRecipeIngredient> fillingIngredients,
                                        List<UserRecipeIngredient> frostingIngredients,
                                        CakeOrder cakeOrder) {

        Map<String, ShoppingListItem> itemMap = new HashMap<>();

        ShoppingList shoppingList = cakeOrder.getShoppingList();

        // If no shopping list exists yet, create and attach it
        if (shoppingList == null) {
            shoppingList = new ShoppingList(cakeOrder, new ArrayList<>());
            cakeOrder.setShoppingList(shoppingList);
        }

            // Add entire cake recipe to map
            for (UserRecipeIngredient uri : cakeIngredients) {
                String name = uri.getIngredient().getIngredientName().toLowerCase();
                double multiplied = uri.getQuantity() * cakeOrder.getCakeMultiplier();
                ShoppingListItem listItem = new ShoppingListItem(name, multiplied, uri.getUnit(), shoppingList);
                itemMap.put(name, listItem);
            }

            // Check map for filling recipe ingredients
            for (UserRecipeIngredient uri : fillingIngredients) {
                String name = uri.getIngredient().getIngredientName().toLowerCase();
                double multiplied = uri.getQuantity() * cakeOrder.getFillingMultiplier();

                if (itemMap.containsKey(name)) {
                    ShoppingListItem existingListItem = itemMap.get(name);
                    String unitA = existingListItem.getUnit();
                    String unitB = uri.getUnit();

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
                    ShoppingListItem listItem = new ShoppingListItem(name, multiplied, uri.getUnit(), shoppingList);
                    itemMap.put(name, listItem);
                }
            }

            // Check map for frosting recipe ingredients
            for (UserRecipeIngredient uri : frostingIngredients) {
                String name = uri.getIngredient().getIngredientName().toLowerCase();
                double multiplied = uri.getQuantity() * cakeOrder.getFrostingMultiplier();

                if (itemMap.containsKey(name)) {
                    ShoppingListItem existingListItem = itemMap.get(name);
                    String unitA = existingListItem.getUnit();
                    String unitB = uri.getUnit();

                    if (unitB.equalsIgnoreCase(unitA)) {
                        double newAmount = existingListItem.getAmount() + multiplied;
                        existingListItem.setAmount(newAmount);
                        itemMap.put(name, existingListItem);
                    } else {
                        double conversionRate = spoonacularService.getConversionRate(
                                name, unitB, unitA); // convert uri unit to match existing
                        double normalizedAmount = multiplied * conversionRate;
                        double newAmount = existingListItem.getAmount() + normalizedAmount;
                        existingListItem.setAmount(newAmount);
                        itemMap.put(name, existingListItem);
                    }
                } else {
                    ShoppingListItem listItem = new ShoppingListItem(name, multiplied, uri.getUnit(), shoppingList);
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

    public CakeOrderDTO cakeOrderIdToDTO(Long id){
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

    public void deleteById(Long id) {
        cakeOrderRepository.deleteById(id);
    }
}
