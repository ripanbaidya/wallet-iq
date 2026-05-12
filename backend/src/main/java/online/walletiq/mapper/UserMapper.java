package online.walletiq.mapper;

import online.walletiq.entity.User;
import online.walletiq.dto.user.UserProfileResponse;

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
