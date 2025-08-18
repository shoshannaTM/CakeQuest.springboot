package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.EditRecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.*;

//check if recipe already exists by url, adds info to recipe table, ingredients table, and recipeIngredient table
//Return a recipeDTO object (with ingredientDTO included) to controller to display recipe in UI

@Service
public class RecipeService {
    @Autowired
    RecipeScraperService recipeScraperService;
    @Autowired
    CakeOrderService cakeOrderService;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    UserRecipeRepository userRecipeRepository;
    @Autowired
    CakeTaskRepository cakeTaskRepository;
    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    CakeOrderRepository cakeOrderRepository;

    @Transactional
    public EditRecipeDTO processRecipeForEdit(String recipeUrl, RecipeType recipeType, User user) throws IOException {
        Optional<Recipe> optionalRecipe = recipeRepository.findRecipeByRecipeUrl(recipeUrl);

        Recipe recipe;
        if (optionalRecipe.isPresent()) {
            recipe = optionalRecipe.get();
            // If user hasn't saved this recipe yet, create UserRecipe link
            if (!userRecipeRepository.existsByUserAndRecipe(user, recipe)) {
                userRecipeRepository.save(new UserRecipe(new UserRecipeId(recipe.getRecipeId(), user.getUserId()), recipe, user));
            }
        } else {
            RecipeDTO recipeDTO = recipeScraperService.scrapeRecipe(recipeUrl, recipeType);
            recipe = new Recipe(recipeDTO.getRecipeType(), recipeDTO.getRecipeName(), recipeDTO.getInstructions(), recipeDTO.getRecipeUrl());
            recipeRepository.save(recipe);
            saveIngredients(recipe, recipeDTO);
            userRecipeRepository.save(new UserRecipe(new UserRecipeId(recipe.getRecipeId(), user.getUserId()), recipe, user));
        }

        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipe.getRecipeId());
        List<IngredientDTO> ingredientDTOList = recipeIngredientsToDTO(recipeIngredients);
        List<String> instructionsList = instructionsFromString(recipe.getInstructions());

        return new EditRecipeDTO(recipe.getRecipeId(), recipe.getRecipeName(), ingredientDTOList, instructionsList);
    }

    public List<String> instructionsFromString(String instructionsString) {
        List<String> instructions = new ArrayList<>();
        for (String s : instructionsString.split("\\|")) {
            if (!s.isBlank()) {
                instructions.add(s.trim());
            }
        }
        return instructions;
    }

    public RecipeDTO recipeToDTO(int recipeId){
        Recipe r = recipeRepository.findRecipeByRecipeId(recipeId);
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipeId);
        List<IngredientDTO> ingredientDTOList = recipeIngredientsToDTO(recipeIngredients);

        RecipeDTO recipeDTO = new RecipeDTO(r.getRecipeName(), r.getRecipeUrl(), r.getInstructions(),
                                            r.getRecipeType(), ingredientDTOList, recipeId);
        return recipeDTO;
    }

    public List<IngredientDTO> recipeIngredientsToDTO(List<RecipeIngredient> recipeIngredients) {
        List<IngredientDTO> dtoList = new ArrayList<>();
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            IngredientDTO dto = new IngredientDTO(
                    recipeIngredient.getIngredient().getIngredientName(),
                    recipeIngredient.getQuantity(),
                    recipeIngredient.getUnit()
            );
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveRecipe (RecipeDTO recipeDTO, User user){
            Recipe recipe = new Recipe(recipeDTO.getRecipeType(), recipeDTO.getRecipeName(),
                    recipeDTO.getInstructions(), recipeDTO.getRecipeUrl());
            recipeRepository.save(recipe);
            return (recipe.getRecipeId() > 0);
        }

    public List<UserRecipe> usersRecipesByType(User user, RecipeType recipeType){
        List<UserRecipe> userRecipesOfType = userRecipeRepository.findByUserAndRecipe_RecipeType(user, recipeType);
        return userRecipesOfType;
    }

    public void saveIngredients(Recipe recipe, RecipeDTO recipeDTO) {
        for (IngredientDTO dto : recipeDTO.getIngredients()) {
            Ingredient ingredient = ingredientRepository
                    .findByIngredientNameIgnoreCase(dto.getName())
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(dto.getName())));
            RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, dto.getAmount(), dto.getUnit());
            recipeIngredientRepository.save(recipeIngredient);
        }
    }

    public List<RecipeDTO> filterRecipes(User user, String type){
        List<RecipeDTO> displayRecipes = new ArrayList<>();
        if(type.equalsIgnoreCase("all")){
            List<UserRecipe> recipes = userRecipeRepository.findByUser(user);
            for (UserRecipe ur : recipes) {
                int recipeId = ur.getRecipe().getRecipeId();
                RecipeDTO dto = recipeToDTO(recipeId);
                displayRecipes.add(dto);
            }
        } else {
            RecipeType recipeType = RecipeType.valueOf(type.toUpperCase());
            List<UserRecipe> recipes = userRecipeRepository.findByUserAndRecipe_RecipeType(user, recipeType);
            for (UserRecipe ur : recipes) {
                int recipeId = ur.getRecipe().getRecipeId();
                RecipeDTO dto = recipeToDTO(recipeId);
                displayRecipes.add(dto);
            }
        }
        return displayRecipes;
    }

    public List<RecipeDTO> searchRecipes(User user, String search){
        List<RecipeDTO> displayRecipes = new ArrayList<>();
        List<UserRecipe> recipes = userRecipeRepository.findByUserAndRecipe_RecipeNameContainingIgnoreCase(user, search);
        for (UserRecipe ur : recipes) {
            int recipeId = ur.getRecipe().getRecipeId();
            RecipeDTO dto = recipeToDTO(recipeId);
            displayRecipes.add(dto);
        }
        return displayRecipes;
    }

    @Transactional
    public void updateRecipeFromEditForm(EditRecipeDTO form) {
        Recipe recipe = recipeRepository.findRecipeByRecipeId(form.getRecipeId());
        recipe.setRecipeName(form.getRecipeName());
        recipe.setInstructions(form.instructionsToString());

        // Load existing ingredients for this recipe
        List<RecipeIngredient> existing =
                recipeIngredientRepository.findRecipeIngredientsByRecipeId(form.getRecipeId());

        // Index existing by normalized name (or by Ingredient id) — no streams
        Map<String, RecipeIngredient> byName = new HashMap<>();
        for (RecipeIngredient ri : existing) {
            String nm = ri.getIngredient().getIngredientName();
            if (nm != null) {
                String key = nm.trim().toLowerCase();
                byName.put(key, ri);
            }
        }

        // Track which existing rows we keep
        Set<RecipeIngredientId> keptIds = new HashSet<>();

        // Upsert rows from form
        List<IngredientDTO> formItems = form.getIngredients();
        if (formItems != null) {
            for (IngredientDTO dto : formItems) {
                String raw = (dto.getName() == null) ? "" : dto.getName().trim();
                if (raw.isEmpty()) continue;
                if (dto.getAmount() == null || dto.getAmount() <= 0.0) continue;

                String key = raw.toLowerCase();

                // Find or create Ingredient (normalize name)
                Ingredient ing;
                Optional<Ingredient> opt = ingredientRepository.findByIngredientNameIgnoreCase(raw);
                if (opt.isPresent()) {
                    ing = opt.get();
                } else {
                    ing = new Ingredient(raw);
                    ing = ingredientRepository.save(ing);
                }

                RecipeIngredient existingRow = byName.get(key);
                if (existingRow != null) {
                    existingRow.setQuantity(dto.getAmount());
                    existingRow.setUnit(dto.getUnit());
                    if (existingRow.getRecipeIngredientId() != null) {
                        keptIds.add(existingRow.getRecipeIngredientId());
                    }
                } else {
                    recipeIngredientRepository.save(
                            new RecipeIngredient(recipe, ing, dto.getAmount(), dto.getUnit())
                    );
                }
            }
        }

        // Remove only rows that were actually deleted in the form — no streams
        List<RecipeIngredient> toRemove = new ArrayList<>();
        for (RecipeIngredient ri : existing) {
            RecipeIngredientId id = ri.getRecipeIngredientId();
            if (id == null || !keptIds.contains(id)) {
                toRemove.add(ri);
            }
        }
        if (!toRemove.isEmpty()) {
            recipeIngredientRepository.deleteAllInBatch(toRemove);
        }

        recipeRepository.save(recipe);

        // Recompute shopping lists for affected orders (null-safe)
        List<CakeOrder> affected = cakeOrderRepository.findAllUsingRecipe(recipe);

        for (CakeOrder order : affected) {
            List<RecipeIngredient> cakeIng;
            if (order.getCakeRecipe() != null) {
                cakeIng = recipeIngredientRepository.findRecipeIngredientsByRecipeId(
                        order.getCakeRecipe().getRecipeId());
            } else {
                cakeIng = Collections.emptyList();
            }

            List<RecipeIngredient> fillingIng;
            if (order.getFillingRecipe() != null) {
                fillingIng = recipeIngredientRepository.findRecipeIngredientsByRecipeId(
                        order.getFillingRecipe().getRecipeId());
            } else {
                fillingIng = Collections.emptyList();
            }

            List<RecipeIngredient> frostingIng;
            if (order.getFrostingRecipe() != null) {
                frostingIng = recipeIngredientRepository.findRecipeIngredientsByRecipeId(
                        order.getFrostingRecipe().getRecipeId());
            } else {
                frostingIng = Collections.emptyList();
            }

            // Consider a merge strategy that preserves "other" items on the list
            cakeOrderService.buildShoppingList(cakeIng, fillingIng, frostingIng, order);
            cakeOrderRepository.save(order);

            // Repoint incomplete shopping tasks
            List<CakeTask> shopTasks =
                    cakeTaskRepository.findAllByCakeOrderAndTaskTypeInAndCompletedFalse(
                            order, Arrays.asList(TaskType.SHOP_PANTRY, TaskType.SHOP_STORE));

            for (CakeTask t : shopTasks) {
                t.setShoppingList(order.getShoppingList());
                cakeTaskRepository.save(t);
            }
        }
    }



    public void deleteRecipe(Integer recipeId) {
        //userRecipeRepository.deleteAllByRecipeId(recipeId);
        //recipeIngredientRepository.deleteAllByRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);
    }
}
