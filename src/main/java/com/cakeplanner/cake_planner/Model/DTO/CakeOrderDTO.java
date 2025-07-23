package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.Recipe;

import java.time.LocalDateTime;

public class CakeOrderDTO {

    private int cakeId;
    private String cakeName;
    private LocalDateTime dueDate;

    private Recipe cakeRecipe;
    private double cakeMultiplier;

    private Recipe fillingRecipe;
    private double fillingMultiplier;

    private Recipe frostingRecipe;
    private double frostingMultiplier;

    private String dietaryRestriction;
    private String decorationNotes;

    public CakeOrderDTO() {
    }

    public CakeOrderDTO(int cakeId, String cakeName, LocalDateTime dueDate,
                        Recipe cakeRecipe, double cakeMultiplier,
                        Recipe fillingRecipe, double fillingMultiplier,
                        Recipe frostingRecipe, double frostingMultiplier,
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

    public int getCakeId() {
        return cakeId;
    }

    public void setCakeId(int cakeId) {
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

    public Recipe getCakeRecipe() {
        return cakeRecipe;
    }

    public void setCakeRecipe(Recipe cakeRecipe) {
        this.cakeRecipe = cakeRecipe;
    }

    public double getCakeMultiplier() {
        return cakeMultiplier;
    }

    public void setCakeMultiplier(double cakeMultiplier) {
        this.cakeMultiplier = cakeMultiplier;
    }

    public Recipe getFillingRecipe() {
        return fillingRecipe;
    }

    public void setFillingRecipe(Recipe fillingRecipe) {
        this.fillingRecipe = fillingRecipe;
    }

    public double getFillingMultiplier() {
        return fillingMultiplier;
    }

    public void setFillingMultiplier(double fillingMultiplier) {
        this.fillingMultiplier = fillingMultiplier;
    }

    public Recipe getFrostingRecipe() {
        return frostingRecipe;
    }

    public void setFrostingRecipe(Recipe frostingRecipe) {
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
        return cakeRecipe.getRecipeName() + " - " + fillingRecipe.getRecipeName() + " - " + frostingRecipe.getRecipeName();
    }
}
