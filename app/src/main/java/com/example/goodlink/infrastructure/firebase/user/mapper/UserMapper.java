package com.example.goodlink.infrastructure.firebase.user.mapper;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.user.User;
import com.example.goodlink.infrastructure.firebase.user.dto.UserDto;

public final class UserMapper {

    private UserMapper() {}

    @NonNull
    public static User toDomain(@NonNull String userId, UserDto dto) {
        if (dto == null) return new User(userId, "", "");
        return new User(userId, dto.name, dto.email);
    }

    @NonNull
    public static UserDto toDto(@NonNull User user) {
        return new UserDto(user.getName(), user.getEmail());
    }
}