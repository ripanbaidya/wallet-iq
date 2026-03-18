package com.walletiq.service.impl;

import com.walletiq.dto.user.UserProfileResponse;
import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.enums.Role;
import com.walletiq.exception.UserException;
import com.walletiq.mapper.UserMapper;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.UserService;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Fetch current user profile
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse fetchProfile() {
        User user = currentUser();

        return UserMapper.toResponse(user);
    }

    /**
     * Fetch user profile by ID
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse fetchProfileById(UUID id) {
        return userRepository.findById(id)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Update user profile
     */
    @Override
    @Transactional
    public UserProfileResponse updateProfile(String fullName) {
        User user = currentUser();

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
            userRepository.save(user);
        }

        return UserMapper.toResponse(user);
    }

    // Admin

    /**
     * Fetch all users with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> fetchAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    /**
     * Count the number of active/inactive users based on roles
     */
    @Override
    @Transactional(readOnly = true)
    public long countTotalUsers(Role role, boolean active) {
        return userRepository.countByRoleAndActive(role, active);
    }


    // Helper methods

    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

}
