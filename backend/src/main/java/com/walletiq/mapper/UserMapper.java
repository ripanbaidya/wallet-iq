package com.walletiq.mapper;

import com.walletiq.entity.User;
import com.walletiq.dto.user.UserProfileResponse;

public final class UserMapper {

    public static UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getFullName(),
            user.getEmail(), user.isActive(), user.isEmailVerified());
    }
}
