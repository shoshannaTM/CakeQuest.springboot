package com.cakeplanner.cake_planner.Model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String currentPassword;
    @NotBlank
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    public PasswordDTO() {
    }

    public PasswordDTO(String email, String currentPassword, String newPassword) {
        this.email = email;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
