package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;

import java.time.LocalDateTime;

public class CakeTaskDTO {

    private int taskId;
    private String name;
    private TaskType taskType;
    private Integer recipeId;
    private Integer shoppingListId;
    private String dietaryRestriction;
    private String decorationNotes;
    private LocalDateTime dueDate;

    public CakeTaskDTO() {}

    public CakeTaskDTO(int taskId, String name, TaskType taskType, Integer shoppingListId,
                       String dietaryRestriction, LocalDateTime dueDate) {
        this.taskId = taskId;
        this.name = name;
        this.taskType = taskType;
        this.shoppingListId = shoppingListId;
        this.dietaryRestriction = dietaryRestriction;
        this.dueDate = dueDate;
    }

    public CakeTaskDTO(int taskId, String name, TaskType taskType, Integer recipeId,
                       String dietaryRestriction, String decorationNotes,
                       LocalDateTime dueDate) {
        this.taskId = taskId;
        this.name = name;
        this.taskType = taskType;
        this.recipeId = recipeId;
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

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public Integer getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(Integer shoppingListId) {
        this.shoppingListId = shoppingListId;
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

