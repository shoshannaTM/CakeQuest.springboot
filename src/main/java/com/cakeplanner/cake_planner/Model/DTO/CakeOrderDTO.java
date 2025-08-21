package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.CakeTask;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.ShoppingList;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipe;

import java.time.LocalDateTime;

public class CakeOrderDTO {
    private Long cakeId;
    private String cakeName;
    private LocalDateTime dueDate;
    private UserRecipe cakeRecipe;
    private double cakeMultiplier;
    private UserRecipe fillingRecipe;
    private double fillingMultiplier;
    private UserRecipe frostingRecipe;
    private double frostingMultiplier;
    private String dietaryRestriction;
    private String decorationNotes;

    public CakeOrderDTO() {
    }

    public CakeOrderDTO(Long cakeId, String cakeName, LocalDateTime dueDate,
                        UserRecipe cakeRecipe, double cakeMultiplier,
                        UserRecipe fillingRecipe, double fillingMultiplier,
                        UserRecipe frostingRecipe, double frostingMultiplier,
                        String dietaryRestriction, String decorationNotes) {
        this.cakeId = cakeId;
        this.cakeName = cakeName;
        this.dueDate = dueDate;
        this.cakeRecipe = cakeRecipe;
        this.cakeMultiplier = cakeMultiplier;
        this.fillingRecipe = fillingRecipe;
        this.fillingMultiplier = fillingMultiplier;
        this.frostingRecipe = frostingRecipe;
        this.frostingMultiplier = frostingMultiplier;
        this.dietaryRestriction = dietaryRestriction;
        this.decorationNotes = decorationNotes;
    }

    public Long getCakeId() {
        return cakeId;
    }

    public void setCakeId(Long cakeId) {
        this.cakeId = cakeId;
    }

    public String getCakeName() {
        return cakeName;
    }

    public void setCakeName(String cakeName) {
        this.cakeName = cakeName;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public UserRecipe getCakeRecipe() {
        return cakeRecipe;
    }

    public void setCakeRecipe(UserRecipe cakeRecipe) {
        this.cakeRecipe = cakeRecipe;
    }

    public double getCakeMultiplier() {
        return cakeMultiplier;
    }

    public void setCakeMultiplier(double cakeMultiplier) {
        this.cakeMultiplier = cakeMultiplier;
    }

    public UserRecipe getFillingRecipe() {
        return fillingRecipe;
    }

    public void setFillingRecipe(UserRecipe fillingRecipe) {
        this.fillingRecipe = fillingRecipe;
    }

    public double getFillingMultiplier() {
        return fillingMultiplier;
    }

    public void setFillingMultiplier(double fillingMultiplier) {
        this.fillingMultiplier = fillingMultiplier;
    }

    public UserRecipe getFrostingRecipe() {
        return frostingRecipe;
    }

    public void setFrostingRecipe(UserRecipe frostingRecipe) {
        this.frostingRecipe = frostingRecipe;
    }

    public double getFrostingMultiplier() {
        return frostingMultiplier;
    }

    public void setFrostingMultiplier(double frostingMultiplier) {
        this.frostingMultiplier = frostingMultiplier;
    }

    public String getDietaryRestriction() {
        return dietaryRestriction;
    }

    public void setDietaryRestriction(String dietaryRestriction) {
        this.dietaryRestriction = dietaryRestriction;
    }

    public String getDecorationNotes() {
        return decorationNotes;
    }

    public void setDecorationNotes(String decorationNotes) {
        this.decorationNotes = decorationNotes;
    }

    public String getFlavorSummary() {
        return cakeRecipe.getUserRecipeName() + " - "
                + fillingRecipe.getUserRecipeName() + " - "
                + frostingRecipe.getUserRecipeName();
    }
}
