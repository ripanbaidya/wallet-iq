package com.walletiq.backend.mapper;

import com.walletiq.backend.entity.User;
import com.walletiq.backend.dto.user.UserProfileResponse;

public final class UserMapper {

    public static UserProfileResponse mapToUserProfileResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getFullName(),
            user.getEmail());
    }
}
