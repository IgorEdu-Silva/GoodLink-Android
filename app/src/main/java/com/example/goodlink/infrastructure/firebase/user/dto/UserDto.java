package com.example.goodlink.infrastructure.firebase.user.dto;

public class UserDto {
    public String name;
    public String email;

    public UserDto() {}

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}