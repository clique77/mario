package com.kulikov.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

    @NotBlank(message = "Username cant be null")
    @Size(min = 6, max = 32, message = "Username must have at least 6 chars")
    private String username;

    @NotBlank(message = "Password cant be null")
    @Size(min = 8, max = 72, message = "Password must have at least 8 chars")
    private String password;

    @NotBlank(message = "Email cant be null")
    @Email(message = "Not valid type of email")
    @Size(max = 128, message = "Email cant be bigger then 128 chars")
    private String email;

    public UserDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
