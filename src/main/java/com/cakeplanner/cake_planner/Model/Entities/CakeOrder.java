package com.cakeplanner.cake_planner.Model.Entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "cake_order")
public class CakeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cake_id")
    private Long cakeId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cake_name", columnDefinition = "TEXT")
    private String cakeName;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cake_user_recipe_id")
    private UserRecipe cakeRecipe;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "filling_user_recipe_id")
    private UserRecipe fillingRecipe;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "frosting_user_recipe_id")
    private UserRecipe frostingRecipe;

    @Column(name = "cake_multiplier")
    private double cakeMultiplier;

    @Column(name = "filling_multiplier")
    private double fillingMultiplier;

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
                     UserRecipe cakeRecipe, UserRecipe fillingRecipe,
                     UserRecipe frostingRecipe, double cakeMultiplier,
                     double fillingMultiplier, double frostingMultiplier,
                     String dietaryRestriction, String decorationNotes,
                     ShoppingList shoppingList, Set<CakeTask> tasks) {
        this.user = user;
        this.cakeName = cakeName;
        this.dueDate = dueDate;
        this.cakeRecipe = cakeRecipe;
        this.fillingRecipe = fillingRecipe;
        this.frostingRecipe = frostingRecipe;
        this.cakeMultiplier = cakeMultiplier;
        this.fillingMultiplier = fillingMultiplier;
        this.frostingMultiplier = frostingMultiplier;
        this.dietaryRestriction = dietaryRestriction;
        this.decorationNotes = decorationNotes;
        this.shoppingList = shoppingList;
        this.tasks = tasks;
    }

    public CakeOrder(Long cakeId, User user, String cakeName,
                     LocalDateTime dueDate, UserRecipe cakeRecipe,
                     UserRecipe fillingRecipe, UserRecipe frostingRecipe,
                     double cakeMultiplier, double fillingMultiplier,
                     double frostingMultiplier, String dietaryRestriction,
                     String decorationNotes, ShoppingList shoppingList,
                     Set<CakeTask> tasks) {
        this.cakeId = cakeId;
        this.user = user;
        this.cakeName = cakeName;
        this.dueDate = dueDate;
        this.cakeRecipe = cakeRecipe;
        this.fillingRecipe = fillingRecipe;
        this.frostingRecipe = frostingRecipe;
        this.cakeMultiplier = cakeMultiplier;
        this.fillingMultiplier = fillingMultiplier;
        this.frostingMultiplier = frostingMultiplier;
        this.dietaryRestriction = dietaryRestriction;
        this.decorationNotes = decorationNotes;
        this.shoppingList = shoppingList;
        this.tasks = tasks;
    }

    public CakeOrder(User user, String cakeName, LocalDateTime dueDate,
                     UserRecipe cakeRecipe, UserRecipe fillingRecipe,
                     UserRecipe frostingRecipe, double cakeMultiplier,
                     double fillingMultiplier, double frostingMultiplier,
                     String dietaryRestriction, String decorationNotes) {
        this.user = user;
        this.cakeName = cakeName;
        this.dueDate = dueDate;
        this.cakeRecipe = cakeRecipe;
        this.fillingRecipe = fillingRecipe;
        this.frostingRecipe = frostingRecipe;
        this.cakeMultiplier = cakeMultiplier;
        this.fillingMultiplier = fillingMultiplier;
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

    public UserRecipe getCakeRecipe() {
        return cakeRecipe;
    }

    public void setCakeRecipe(UserRecipe cakeRecipe) {
        this.cakeRecipe = cakeRecipe;
    }

    public UserRecipe getFillingRecipe() {
        return fillingRecipe;
    }

    public void setFillingRecipe(UserRecipe fillingRecipe) {
        this.fillingRecipe = fillingRecipe;
    }

    public UserRecipe getFrostingRecipe() {
        return frostingRecipe;
    }

    public void setFrostingRecipe(UserRecipe frostingRecipe) {
        this.frostingRecipe = frostingRecipe;
    }

    public double getCakeMultiplier() {
        return cakeMultiplier;
    }

    public void setCakeMultiplier(double cakeMultiplier) {
        this.cakeMultiplier = cakeMultiplier;
    }

    public double getFillingMultiplier() {
        return fillingMultiplier;
    }

    public void setFillingMultiplier(double fillingMultiplier) {
        this.fillingMultiplier = fillingMultiplier;
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

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public Set<CakeTask> getTasks() {
        return tasks;
    }

    public void setTasks(Set<CakeTask> tasks) {
        this.tasks = tasks;
    }
}
