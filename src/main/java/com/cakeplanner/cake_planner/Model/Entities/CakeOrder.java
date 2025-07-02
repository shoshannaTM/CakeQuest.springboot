package com.cakeplanner.cake_planner.Model.Entities;

import com.cakeplanner.cake_planner.Model.Entities.Enums.Status;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "cake_order")
public class CakeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cake_id")
    private int cakeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User baker;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "cake_recipe_id")
    private Recipe cakeRecipe;

    @ManyToOne
    @JoinColumn(name = "filling_recipe_id")
    private Recipe fillingRecipe;

    @ManyToOne
    @JoinColumn(name = "frosting_recipe_id")
    private Recipe frostingRecipe;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    public CakeOrder() {
    }

    public CakeOrder(User baker, String notes,
                     Status status, Recipe cakeRecipe,
                     Recipe fillingRecipe, Recipe frostingRecipe,
                     Date dueDate) {
        this.baker = baker;
        this.notes = notes;
        this.status = status;
        this.cakeRecipe = cakeRecipe;
        this.fillingRecipe = fillingRecipe;
        this.frostingRecipe = frostingRecipe;
        this.dueDate = dueDate;
    }

    public int getCakeId() {
        return cakeId;
    }

    public void setCakeId(int cakeId) {
        this.cakeId = cakeId;
    }

    public User getBaker() {
        return baker;
    }

    public void setBaker(User baker) {
        this.baker = baker;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Recipe getCakeRecipe() {
        return cakeRecipe;
    }

    public void setCakeRecipe(Recipe cakeRecipe) {
        this.cakeRecipe = cakeRecipe;
    }

    public Recipe getFillingRecipe() {
        return fillingRecipe;
    }

    public void setFillingRecipe(Recipe fillingRecipe) {
        this.fillingRecipe = fillingRecipe;
    }

    public Recipe getFrostingRecipe() {
        return frostingRecipe;
    }

    public void setFrostingRecipe(Recipe frostingRecipe) {
        this.frostingRecipe = frostingRecipe;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
