package com.walletiq.mapper;

import com.walletiq.entity.User;
import com.walletiq.dto.user.UserProfileResponse;

public final class UserMapper {

    public static UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .active(user.isActive())
            .isEmailVerified(user.isEmailVerified())
            .build();
    }
}
