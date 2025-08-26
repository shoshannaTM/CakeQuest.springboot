package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.EditRecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RecipeTransactionService {
    private final RecipeRepository recipeRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final IngredientRepository ingredientRepository;
    private final CakeOrderRepository cakeOrderRepository;
    private final CakeTaskRepository cakeTaskRepository;
    public RecipeTransactionService(RecipeRepository recipeRepository,
                                    UserRecipeRepository userRecipeRepository,
                                    IngredientRepository ingredientRepository,
                                    CakeOrderRepository cakeOrderRepository,
                                    CakeTaskRepository cakeTaskRepository) {
        this.recipeRepository = recipeRepository;
        this.userRecipeRepository = userRecipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.cakeOrderRepository = cakeOrderRepository;
        this.cakeTaskRepository = cakeTaskRepository;
    }

  public record DeleteResult(boolean deleted, long openTasks, long ordersUpdated) {}

    @Transactional
    public DeleteResult deleteIfNoActiveTasks(Long userRecipeId) {

        UserRecipe ur = userRecipeRepository.findForUpdate(userRecipeId).orElse(null);
        if (ur == null) return new DeleteResult(true, 0, 0); // already gone

        // 1) Hard stop if any INCOMPLETE task still references this recipe
        long openTasks = cakeTaskRepository.countByUserRecipeAndCompletedIsFalse(ur);
        if (openTasks > 0) {
            return new DeleteResult(false, openTasks, 0);
        }

        // 2) Detach COMPLETED tasks that reference this recipe
        //    (Optionally: archive a snapshot of the recipe text here before nulling)
        List<CakeTask> allTasks = cakeTaskRepository.findByUserRecipe(ur);
        for (CakeTask t : allTasks) {
            t.setUserRecipe(null);
        }

        // 3) Detach orders that still reference this recipe
        List<CakeOrder> orders = cakeOrderRepository
                .findAllByCakeRecipeOrFillingRecipeOrFrostingRecipe(ur, ur, ur);

        long touched = 0;
        for (CakeOrder o : orders) {
            // Defensive check: there shouldn't be any open tasks for this recipe now.
            boolean hasOpenForThisOrder = o.getTasks().stream()
                    .anyMatch(t -> ur.equals(t.getUserRecipe()) && Boolean.FALSE.equals(t.getCompleted()));
            if (hasOpenForThisOrder) {
                // This shouldn’t happen since we counted globally, but bail safely if it does.
                throw new IllegalStateException("Found open tasks after initial check.");
            }

            if (ur.equals(o.getCakeRecipe()))     { o.setCakeRecipe(null);     touched++; }
            if (ur.equals(o.getFillingRecipe()))  { o.setFillingRecipe(null);  touched++; }
            if (ur.equals(o.getFrostingRecipe())) { o.setFrostingRecipe(null); touched++; }
        }

        // Flush FK nulling before delete to avoid constraint violation on delete
        cakeTaskRepository.flush();
        cakeOrderRepository.flush();

            userRecipeRepository.delete(ur);
            userRecipeRepository.flush();

        return new DeleteResult(true, 0, touched);
        }

    @Transactional
    public void removeFromUserRecipes(User user, Long userRecipeId){
        if(userRecipeRepository.existsByUserAndUserRecipeId(user, userRecipeId)){
            userRecipeRepository.deleteByUserAndUserRecipeId(user, userRecipeId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Recipe saveBaseRecipe(Recipe scraped) {
        return recipeRepository.save(scraped);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EditRecipeDTO emptyUserRecipeForManual(User user, @Nullable RecipeType recipeType) {
        UserRecipe ur = new UserRecipe();
        ur.setUser(user);
        ur.setRecipeType(recipeType);
        ur.setUserRecipeName("");
        ur.setUserRecipeInstructions("");
        ur.setUserRecipeIngredients(new ArrayList<>());
        ur = userRecipeRepository.saveAndFlush(ur);

        return new EditRecipeDTO(
                ur.getUserRecipeId(),
                ur.getUserRecipeName(),
                ur.getRecipeType(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserRecipe getOrCreateUserRecipe(User user, Recipe recipe, RecipeType recipeType) {
        return userRecipeRepository.findByUserAndBaseRecipe(user, recipe)
                .orElseGet(() -> {
                    UserRecipe ur = new UserRecipe();

                    List<RecipeIngredient> recipeIngredients =
                            recipe.getBaseRecipeIngredients() != null
                                    ? recipe.getBaseRecipeIngredients() : List.of();

                    List<UserRecipeIngredient> urIngredients =
                            recipeIngToUserRecipeIng(recipeIngredients, ur);

                    ur.setUser(user);
                    ur.setBaseRecipe(recipe);
                    ur.setRecipeType(recipeType);
                    ur.setUserRecipeName(recipe.getBaseRecipeName());
                    ur.setUserRecipeInstructions(recipe.getBaseRecipeInstructions());
                    ur.setUserRecipeIngredients(urIngredients);

                    return userRecipeRepository.save(ur);
                });
    }

    private List<UserRecipeIngredient> recipeIngToUserRecipeIng(List<RecipeIngredient> recipeIngredients,
                                                                UserRecipe userRecipe) {
        List<UserRecipeIngredient> userIng = new ArrayList<>();
        for (RecipeIngredient ri : recipeIngredients) {
            UserRecipeIngredient uri = new UserRecipeIngredient(
                    userRecipe,
                    ri.getIngredient(),
                    ri.getQuantity(),
                    ri.getUnit()
            );
            userIng.add(uri);
        }
        return userIng;
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
}
