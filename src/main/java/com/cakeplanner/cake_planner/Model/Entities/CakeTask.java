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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "task_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @ManyToOne
    @JoinColumn(name = "id")
    private ShoppingList shoppingList;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = true)
    private Recipe recipe;

    @Column(name = "dietary_restriction", columnDefinition = "TEXT", nullable = true)
    private String dietaryRestriction;

    @Column(name = "decoration_notes", columnDefinition = "TEXT", nullable = true)
    private String decorationNotes;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    public CakeTask() {}

    public CakeTask(User user, String name, TaskType taskType,
                    ShoppingList shoppingList, String dietaryRestriction,
                    LocalDateTime dueDate) {
        this.user = user;
        this.name = name;
        this.taskType = taskType;
        this.shoppingList = shoppingList;
        this.dietaryRestriction = dietaryRestriction;
        this.dueDate = dueDate;
    }

    public CakeTask(User user, String name,
                    TaskType taskType, Recipe recipe,
                    String dietaryRestriction, LocalDateTime dueDate) {
        this.user = user;
        this.name = name;
        this.taskType = taskType;
        this.recipe = recipe;
        this.dietaryRestriction = dietaryRestriction;
        this.dueDate = dueDate;
    }

    public CakeTask(User user, String name,
                    TaskType taskType, String dietaryRestriction,
                    String decorationNotes, LocalDateTime dueDate) {
        this.user = user;
        this.name = name;
        this.taskType = taskType;
        this.dietaryRestriction = dietaryRestriction;
        this.decorationNotes = decorationNotes;
        this.dueDate = dueDate;
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
}

