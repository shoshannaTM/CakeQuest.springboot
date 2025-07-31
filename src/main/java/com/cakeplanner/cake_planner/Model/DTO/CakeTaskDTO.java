package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.ShoppingList;

import java.time.LocalDateTime;

public class CakeTaskDTO {

    private int taskId;
    private String name;
    private TaskType taskType;
    private Integer recipeId;
    private ShoppingList shoppingList;
    private String dietaryRestriction;
    private String decorationNotes;
    private LocalDateTime dueDate;
    private Boolean completed;

    public CakeTaskDTO() {}

    public CakeTaskDTO(int taskId, String name, TaskType taskType, ShoppingList shoppingList,
                       String dietaryRestriction, LocalDateTime dueDate, Boolean completed) {
        this.taskId = taskId;
        this.name = name;
        this.taskType = taskType;
        this.shoppingList = shoppingList;
        this.dietaryRestriction = dietaryRestriction;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public CakeTaskDTO(int taskId, String name, TaskType taskType, Integer recipeId,
                       String dietaryRestriction, String decorationNotes,
                       LocalDateTime dueDate, Boolean completed) {
        this.taskId = taskId;
        this.name = name;
        this.taskType = taskType;
        this.recipeId = recipeId;
        this.dietaryRestriction = dietaryRestriction;
        this.decorationNotes = decorationNotes;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public CakeTaskDTO(int taskId, String name, TaskType taskType,
                       String dietaryRestriction, String decorationNotes,
                       LocalDateTime dueDate, Boolean completed) {
        this.taskId = taskId;
        this.name = name;
        this.taskType = taskType;
        this.dietaryRestriction = dietaryRestriction;
        this.decorationNotes = decorationNotes;
        this.dueDate = dueDate;
        this.completed = completed;
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

    public ShoppingList getShoppingList() {return shoppingList;}

    public void setShoppingList(ShoppingList shoppingList) {this.shoppingList = shoppingList;}

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

    public Boolean getCompleted() {return completed;}

    public void setCompleted(Boolean completed) {this.completed = completed;}
}

