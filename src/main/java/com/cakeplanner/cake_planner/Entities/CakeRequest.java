package com.cakeplanner.cake_planner.Entities;

import com.cakeplanner.cake_planner.Entities.Enums.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "cake_request")
public class CakeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private int requestId;

    @ManyToOne
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "baker_user_id", nullable = false)
    private User baker;

    @Column(name = "requester_notes", columnDefinition = "TEXT")
    private String requesterNotes;

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

    public CakeRequest() {
    }

    public CakeRequest(User requester, User baker,
                       String requesterNotes, Status status,
                       Recipe cakeRecipe, Recipe fillingRecipe,
                       Recipe frostingRecipe) {
        this.requester = requester;
        this.baker = baker;
        this.requesterNotes = requesterNotes;
        this.status = status;
        this.cakeRecipe = cakeRecipe;
        this.fillingRecipe = fillingRecipe;
        this.frostingRecipe = frostingRecipe;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getBaker() {
        return baker;
    }

    public void setBaker(User baker) {
        this.baker = baker;
    }

    public String getRequesterNotes() {
        return requesterNotes;
    }

    public void setRequesterNotes(String requesterNotes) {
        this.requesterNotes = requesterNotes;
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
}
