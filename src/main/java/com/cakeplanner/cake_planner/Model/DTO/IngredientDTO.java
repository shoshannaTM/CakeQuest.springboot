package com.cakeplanner.cake_planner.Model.DTO;

public class IngredientDTO {
    private String name;
    private double amount;
    private String unit;

    public IngredientDTO() {
    }

    public IngredientDTO(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
