package com.walletiq.service.impl;

import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.enums.Role;
import com.walletiq.exception.UserException;
import com.walletiq.mapper.UserMapper;
import com.walletiq.dto.user.UserProfileResponse;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.UserService;
import com.walletiq.util.MaskingUtil;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.walletiq.util.MaskingUtil.maskEmail;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Fetch current user profile
     */
    @Override
    public UserProfileResponse fetchProfile() {
        User user = currentUser();

        return UserMapper.toResponse(user);
    }

    /**
     * Fetch user profile by ID
     */
    @Override
    public UserProfileResponse fetchProfileById(UUID id) {
        return userRepository.findById(id)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Update user profile
     */
    @Override
    public UserProfileResponse updateProfile(String fullName) {
        User user = currentUser();

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
            userRepository.save(user);
        }

        return UserMapper.toResponse(user);
    }

    // Admin endpoints

    /**
     * Fetch all users with pagination
     */
    @Override
    public Page<UserProfileResponse> fetchAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    /**
     * Count the number of active users (Role.USER)
     */
    @Override
    public long countTotalUsers() {
        return userRepository.countByRoleAndActive(Role.USER, true);
    }


    // Helper methods

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

}
