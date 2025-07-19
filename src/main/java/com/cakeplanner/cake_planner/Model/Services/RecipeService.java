package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.RecipeIngredient;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Repositories.IngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//check if recipe already exists by url, adds info to recipe table, ingredients table, and recipeIngredient table
//Return a recipeDTO object (with ingredientDTO included) to controller to display recipe in UI

@Service
public class RecipeService {
    @Autowired
    RecipeScraperService recipeScraperService;
    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    IngredientRepository ingredientRepository;
    @Transactional
    public RecipeDTO processRecipeForDisplay(String recipeUrl, RecipeType recipeType, User user) throws IOException {
        // Use url to check if recipe exists in table before scraping

        if(recipeRepository.findRecipeByRecipeUrl(recipeUrl).isPresent()) {
            //FIXME error handling
            System.out.println("Recipe already in table");
            int recipeId = recipeRepository.findRecipeIdByRecipeUrl(recipeUrl);
            Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId);
            List<RecipeIngredient> recipeIngredientsList = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipe.getRecipeId());
            List <IngredientDTO> ingredientDTOList = recipeIngredientsToDTO(recipeIngredientsList);

            RecipeDTO recipeDTO = new RecipeDTO(recipe.getRecipeName(), recipe.getRecipeUrl(),recipe.getInstructions(),
                                                recipe.getRecipeType(), ingredientDTOList);

            return recipeDTO;
        } else {
            RecipeDTO recipeDTO = recipeScraperService.scrapeRecipe(recipeUrl, recipeType);
            saveRecipe(recipeDTO, user);
            saveIngredients(recipeDTO);
            return recipeDTO;
        }

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
            recipe.setUser(user);
            recipeRepository.save(recipe);
            return (recipe.getRecipeId() > 0);
        }


        public void saveIngredients (RecipeDTO recipeDTO){
            int recipeId = recipeRepository.findRecipeIdByRecipeUrl(recipeDTO.getRecipeUrl());
            Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId);
            for (int i = 0; i < recipeDTO.getIngredients().size(); i++) {
                IngredientDTO ingredientDTO = recipeDTO.getIngredients().get(i);
              Ingredient ingredient = ingredientRepository
                        .findByIngredientNameIgnoreCase(ingredientDTO.getName())
                        .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientDTO.getName())));
                RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, ingredientDTO.getAmount(),
                        ingredientDTO.getUnit());
                recipeIngredientRepository.save(recipeIngredient);
            }
        }
}
