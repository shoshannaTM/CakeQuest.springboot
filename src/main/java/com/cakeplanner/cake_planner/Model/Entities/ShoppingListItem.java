package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.*;
import jakarta.persistence.Id;

@Entity
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String name;
    @Column(name = "amount")
    private double amount;
    @Column(name = "unit")
    private String unit;

    @ManyToOne
    @JoinColumn(name = "shopping_list_id")
    private ShoppingList shoppingList;

    public ShoppingListItem() {}

    public ShoppingListItem(String name, double amount, String unit, ShoppingList shoppingList) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.shoppingList = shoppingList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }
}


