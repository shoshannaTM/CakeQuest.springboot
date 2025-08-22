package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.EditRecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

//check if recipe already exists by url, adds info to recipe table, ingredients table, and recipeIngredient table
//Return a recipeDTO object (with ingredientDTO included) to controller to display recipe in UI

@Service
public class RecipeService {
    private final RecipeScraperService recipeScraperService;
    private final CakeOrderService cakeOrderService;
    private final RecipeRepository recipeRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final CakeTaskRepository cakeTaskRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final CakeOrderRepository cakeOrderRepository;

    public RecipeService(RecipeScraperService recipeScraperService,
                         CakeOrderService cakeOrderService,
                         RecipeRepository recipeRepository,
                         UserRecipeRepository userRecipeRepository,
                         CakeTaskRepository cakeTaskRepository,
                         RecipeIngredientRepository recipeIngredientRepository,
                         IngredientRepository ingredientRepository,
                         CakeOrderRepository cakeOrderRepository) {
        this.recipeScraperService = recipeScraperService;
        this.cakeOrderService = cakeOrderService;
        this.recipeRepository = recipeRepository;
        this.userRecipeRepository = userRecipeRepository;
        this.cakeTaskRepository = cakeTaskRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.cakeOrderRepository = cakeOrderRepository;
    }
    @Transactional
    public EditRecipeDTO processRecipeForEdit(String recipeUrl, RecipeType recipeType, User user) throws IOException {
        Optional<Recipe> optionalRecipe = recipeRepository.findByRecipeUrl(recipeUrl);
        Recipe recipe;

        if (optionalRecipe.isPresent()) {
            recipe = optionalRecipe.get();
        } else {
            try {
                Recipe scraped = recipeScraperService.scrapeRecipe(recipeUrl);

                boolean hasName = scraped.getBaseRecipeName() != null && !scraped.getBaseRecipeName().isBlank();
                boolean hasIngredients = scraped.getBaseRecipeIngredients() != null && !scraped.getBaseRecipeIngredients().isEmpty();

                if (!hasName && !hasIngredients) {
                    return new EditRecipeDTO(null, "", null, new ArrayList<>(), new ArrayList<>());
                }

                recipe = recipeRepository.save(scraped);
            } catch (Exception e) {
                return new EditRecipeDTO(null, "", null, new ArrayList<>(), new ArrayList<>());
            }
        }

        Optional<UserRecipe> optionalUR = userRecipeRepository.findByUserAndBaseRecipe(user, recipe);

        UserRecipe userRecipe;

        if (optionalUR.isPresent()) {
            userRecipe = optionalUR.get();
        } else {
            UserRecipe ur = new UserRecipe();

            List<RecipeIngredient> recipeIngredients = recipe.getBaseRecipeIngredients();
            List<UserRecipeIngredient> urIngredients = recipeIngToUserRecipeIng(recipeIngredients != null
                    ? recipeIngredients : List.of(), ur);

            ur.setUser(user);
            ur.setBaseRecipe(recipe);
            ur.setRecipeType(recipeType);
            ur.setUserRecipeName(recipe.getBaseRecipeName());
            ur.setUserRecipeInstructions(recipe.getBaseRecipeInstructions());
            ur.setUserRecipeIngredients(urIngredients);

            userRecipe = userRecipeRepository.save(ur);
        }


        List<IngredientDTO> ingredientDTOList = userRecipeIngredientsToDTO(userRecipe.getUserRecipeIngredients());
        List<String> instructionsList = instructionsFromString(userRecipe.getUserRecipeInstructions());

        return new EditRecipeDTO(userRecipe.getUserRecipeId(), userRecipe.getUserRecipeName(),
                                userRecipe.getRecipeType(), ingredientDTOList, instructionsList);
    }

    public List<UserRecipeIngredient> recipeIngToUserRecipeIng(List<RecipeIngredient> recipeIngredients, UserRecipe userRecipe){
        List<UserRecipeIngredient> userIng = new ArrayList<>();
        for(RecipeIngredient ri : recipeIngredients){
            UserRecipeIngredient uri = new UserRecipeIngredient(userRecipe, ri.getIngredient(),
                    ri.getQuantity(), ri.getUnit());
            userIng.add(uri);
        }
        return userIng;
    }

    public List<String> instructionsFromString(String instructionsString) {
        if (instructionsString == null || instructionsString.isBlank()) return List.of();

        List<String> instructions = new ArrayList<>();

        for (String s : instructionsString.split("\\|")) {
            if (!s.isBlank()) instructions.add(s.trim());
        }
        return instructions;
    }


    public RecipeDTO userRecipeToDTO(Long userRecipeId){
        Optional<UserRecipe> optionalUserRecipe = userRecipeRepository.findByUserRecipeId(userRecipeId);

        if(optionalUserRecipe.isEmpty()){
            return null;
        }

        UserRecipe ur = optionalUserRecipe.get();

        List<UserRecipeIngredient> userRecipeIngredients = ur.getUserRecipeIngredients();
        List<IngredientDTO> ingredientDTOList = userRecipeIngredientsToDTO(userRecipeIngredients);

        RecipeDTO recipeDTO = new RecipeDTO(ur.getUserRecipeId(), ur.getUserRecipeName(), ur.getRecipeType(),
                                                ingredientDTOList, ur.getUserRecipeInstructions());
        return recipeDTO;
    }

    public List<IngredientDTO> userRecipeIngredientsToDTO(List<UserRecipeIngredient> userRecipeIngredients) {
        if (userRecipeIngredients == null) return List.of();

        List<IngredientDTO> dtoList = new ArrayList<>();
        for (UserRecipeIngredient uri: userRecipeIngredients) {
            IngredientDTO dto = new IngredientDTO(
                    uri.getIngredient().getIngredientName(),
                    uri.getQuantity(),
                    uri.getUnit()
            );
            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<UserRecipeIngredient> ingredientDTOToUserRecipeIngredient(List<IngredientDTO> ingredientDTOS,
                                                                          UserRecipe ur) {
        List<UserRecipeIngredient> userRecipeIngredients = new ArrayList<>();
        for (IngredientDTO dto: ingredientDTOS) {
            Optional<Ingredient> opt = ingredientRepository.findByIngredientNameIgnoreCase(dto.getName());
            Ingredient ingredient = opt.orElseGet(() -> ingredientRepository.save(new Ingredient(dto.getName())));

            UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(
                    ur,
                    ingredient,
                    dto.getAmount(),
                    dto.getUnit()
            );

            userRecipeIngredients.add(userRecipeIngredient);
        }
        return userRecipeIngredients;
    }


    public List<UserRecipe> usersRecipesByType(User user, RecipeType recipeType){
        List<UserRecipe> userRecipesOfType = userRecipeRepository.findByUserAndRecipeType(user, recipeType);
        return userRecipesOfType;
    }

    public List<RecipeDTO> filterRecipes(User user, String type){
        List<RecipeDTO> displayRecipes = new ArrayList<>();
        if(type.equalsIgnoreCase("all")){
            List<UserRecipe> userRecipes = userRecipeRepository.findByUser(user);
            for (UserRecipe ur : userRecipes) {
                Long id = ur.getUserRecipeId();
                RecipeDTO dto = userRecipeToDTO(id);
                displayRecipes.add(dto);
            }
        } else {
            RecipeType recipeType = RecipeType.valueOf(type.toUpperCase());
            List<UserRecipe> userRecipes = userRecipeRepository.findByUserAndRecipeType(user, recipeType);
            for (UserRecipe ur : userRecipes) {
                Long id = ur.getUserRecipeId();
                RecipeDTO dto = userRecipeToDTO(id);
                displayRecipes.add(dto);
            }
        }
        return displayRecipes;
    }

    public List<RecipeDTO> searchRecipes(User user, String search){
        List<RecipeDTO> displayRecipes = new ArrayList<>();
        List<UserRecipe> userRecipes = userRecipeRepository.findByUserAndUserRecipeNameContainingIgnoreCase(user, search);
        for (UserRecipe ur : userRecipes) {
            Long id = ur.getUserRecipeId();
            RecipeDTO dto = userRecipeToDTO(id);
            displayRecipes.add(dto);
        }
        return displayRecipes;
    }

    @Transactional
    public void updateUserRecipeFromEditForm(EditRecipeDTO form) {
        if (form == null) {
            throw new IllegalArgumentException("form is null");
        }

        final Long userRecipeId = form.getUserRecipeId();
        UserRecipe ur = userRecipeRepository.findById(userRecipeId)
                .orElseThrow(() -> new IllegalArgumentException("UserRecipe not found: " + userRecipeId));

        applyHeaderChanges(ur, form);

        replaceIngredients(ur, form.getIngredients());

        userRecipeRepository.saveAndFlush(ur);

        rebuildShoppingListsForOrdersThatUse(ur);
    }

    public EditRecipeDTO emptyUserRecipeForManual(User user){
        UserRecipe userRecipe = new UserRecipe();
        userRecipe.setUser(user);
        userRecipeRepository.save(userRecipe);
        EditRecipeDTO form = new EditRecipeDTO(userRecipe.getUserRecipeId(),
                "", null, new java.util.ArrayList<>(), new java.util.ArrayList<>());

        return form;
    }

    private void applyHeaderChanges(UserRecipe ur, EditRecipeDTO form) {
        if (form.getRecipeName() != null) {
            ur.setUserRecipeName(form.getRecipeName().trim());
        }
        ur.setUserRecipeInstructions(safeString(form.instructionsToString()));
        if (form.getRecipeType() != null) {
             ur.setRecipeType(form.getRecipeType());
        }
    }
    private void replaceIngredients(UserRecipe ur, List<IngredientDTO> editedIngredients) {
        // Clear existing
        ur.getUserRecipeIngredients().clear();

        if (editedIngredients == null) return;

        for (IngredientDTO dto : editedIngredients) {
            if (!isValidIngredient(dto)) continue;

            // find or create Ingredient by (case-insensitive) name
            String rawName = dto.getName().trim();
            Ingredient ingredient = ingredientRepository
                    .findByIngredientNameIgnoreCase(rawName)
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(rawName)));

            UserRecipeIngredient ing = new UserRecipeIngredient();
            ing.setUserRecipe(ur);
            ing.setIngredient(ingredient);
            ing.setUnit(dto.getUnit().trim());
            ing.setQuantity(dto.getAmount());

            ur.getUserRecipeIngredients().add(ing);
        }
    }

    private boolean isValidIngredient(IngredientDTO dto) {
        if (dto == null) return false;
        if (dto.getName() == null || dto.getName().trim().isEmpty()) return false;
        if (dto.getUnit() == null || dto.getUnit().trim().isEmpty()) return false;
        if (dto.getAmount() == null) return false;
        if (dto.getAmount() <= 0.0) return false;
        return true;
    }

    private String safeString(String s) {
        return (s == null) ? "" : s;
    }

    // inside RecipeService

    @Transactional
    private void rebuildShoppingListsForOrdersThatUse(UserRecipe ur) {
        // Find orders that reference this user recipe in any role
        List<CakeOrder> affected = cakeOrderRepository
                .findAllByCakeRecipeOrFillingRecipeOrFrostingRecipe(ur, ur, ur);

        if (affected == null || affected.isEmpty()) return;

        for (CakeOrder order : affected) {
            List<UserRecipeIngredient> cakeIngredients =
                    (order.getCakeRecipe() != null)
                            ? new ArrayList<>(order.getCakeRecipe().getUserRecipeIngredients())
                            : Collections.emptyList();

            List<UserRecipeIngredient> fillingIngredients =
                    (order.getFillingRecipe() != null)
                            ? new ArrayList<>(order.getFillingRecipe().getUserRecipeIngredients())
                            : Collections.emptyList();

            List<UserRecipeIngredient> frostingIngredients =
                    (order.getFrostingRecipe() != null)
                            ? new ArrayList<>(order.getFrostingRecipe().getUserRecipeIngredients())
                            : Collections.emptyList();

            cakeOrderService.buildShoppingList(cakeIngredients, fillingIngredients, frostingIngredients, order);
            cakeOrderRepository.save(order);

            // Repoint incomplete SHOP tasks to the refreshed list
            List<CakeTask> shopTasks =
                    cakeTaskRepository.findAllByCakeOrderAndTaskTypeInAndCompletedFalse(
                            order, Arrays.asList(TaskType.SHOP_PANTRY, TaskType.SHOP_STORE));

            for (CakeTask t : shopTasks) {
                t.setShoppingList(order.getShoppingList());
            }
            if (!shopTasks.isEmpty()) {
                cakeTaskRepository.saveAll(shopTasks);
            }
        }
    }

    @Transactional
    public void removeFromUserRecipes(Long userRecipeId, User user) {
        if(userRecipeRepository.existsByUserAndUserRecipeId(user, userRecipeId))
        userRecipeRepository.deleteByUserAndUserRecipeId(user, userRecipeId);
    }
}
