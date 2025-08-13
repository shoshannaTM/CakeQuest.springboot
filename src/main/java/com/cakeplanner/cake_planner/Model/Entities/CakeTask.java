package com.cakeplanner.cake_planner.Model.Entities;

import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CakeTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private int taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cake_id", nullable = false) // FK → cake_order(cake_id)
    private CakeOrder cakeOrder;

    @Column(name = "task_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private ShoppingList shoppingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(name = "dietary_restriction", columnDefinition = "TEXT", nullable = true)
    private String dietaryRestriction;

    @Column(name = "decoration_notes", columnDefinition = "TEXT", nullable = true)
    private String decorationNotes;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed")
    private Boolean completed;

    public CakeTask() {}

    public CakeTask(User user, CakeOrder cakeOrder, String name, TaskType taskType,
                    ShoppingList shoppingList, String dietaryRestriction,
                    LocalDateTime dueDate, Boolean completed) {
        this.user = user;
        this.cakeOrder = cakeOrder;
        this.name = name;
        this.taskType = taskType;
        this.shoppingList = shoppingList;
        this.dietaryRestriction = dietaryRestriction;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public CakeTask(User user, CakeOrder cakeOrder, String name,
                    TaskType taskType, Recipe recipe,
                    String dietaryRestriction, LocalDateTime dueDate, Boolean completed) {
        this.user = user;
        this.cakeOrder = cakeOrder;
        this.name = name;
        this.taskType = taskType;
        this.recipe = recipe;
        this.dietaryRestriction = dietaryRestriction;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public CakeTask(User user, CakeOrder cakeOrder, String name,
                    TaskType taskType, String dietaryRestriction,
                    String decorationNotes, LocalDateTime dueDate, Boolean completed) {
        this.user = user;
        this.cakeOrder = cakeOrder;
        this.name = name;
        this.taskType = taskType;
        this.dietaryRestriction = dietaryRestriction;
        this.decorationNotes = decorationNotes;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public CakeTask(User user, CakeOrder cakeOrder, String name,
                    TaskType taskType, String dietaryRestriction,
                    LocalDateTime dueDate, Boolean completed) {
        this.user = user;
        this.cakeOrder = cakeOrder;
        this.name = name;
        this.taskType = taskType;
        this.dietaryRestriction = dietaryRestriction;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CakeOrder getCakeOrder() {
        return cakeOrder;
    }

    public void setCakeOrder(CakeOrder cakeOrder) {
        this.cakeOrder = cakeOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
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

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public ShoppingList getShoppingList() {return shoppingList;}

    public void setShoppingList(ShoppingList shoppingList) {this.shoppingList = shoppingList;}

    public Boolean getCompleted() {return completed;}

    public void setCompleted(Boolean completed) {this.completed = completed;}
}

