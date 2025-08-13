package com.cakeplanner.cake_planner.Model.Entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cake_order")
public class CakeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cake_id")
    private int cakeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cake_name", columnDefinition = "TEXT")
    private String cakeName;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "cake_recipe_id")
    private Recipe cakeRecipe;

    @Column(name = "cake_multiplier")
    private double cakeMultiplier;

    @ManyToOne
    @JoinColumn(name = "filling_recipe_id")
    private Recipe fillingRecipe;

    @Column(name = "filling_multiplier")
    private double fillingMultiplier;

    @ManyToOne
    @JoinColumn(name = "frosting_recipe_id")
    private Recipe frostingRecipe;

    @Column(name = "frosting_multiplier")
    private double frostingMultiplier;

    @Column(name = "dietary_restriction", columnDefinition = "TEXT")
    private String dietaryRestriction;

    @Column(name = "decoration_notes", columnDefinition = "TEXT")
    private String decorationNotes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shopping_list_id")
    private ShoppingList shoppingList;

    @OneToMany(
            mappedBy = "cakeOrder",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private java.util.Set<CakeTask> tasks = new java.util.HashSet<>();
    public CakeOrder() {
    }

    public CakeOrder(User user, String cakeName, LocalDateTime dueDate,
                     Recipe cakeRecipe, double cakeMultiplier, Recipe fillingRecipe,
                     double fillingMultiplier, Recipe frostingRecipe, double frostingMultiplier,
                     String dietaryRestriction, String decorationNotes) {
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public ShoppingList getShoppingList() {return shoppingList;}

    public void setShoppingList(ShoppingList shoppingList) {this.shoppingList = shoppingList;}

    public Set<CakeTask> getTasks() {
        return tasks;
    }

    public void setTasks(Set<CakeTask> tasks) {
        this.tasks = tasks;
    }
}
