package online.walletiq.service.impl;

import online.walletiq.dto.user.UserProfileResponse;
import online.walletiq.entity.User;
import online.walletiq.enums.ErrorCode;
import online.walletiq.enums.Role;
import online.walletiq.exception.UserException;
import online.walletiq.mapper.UserMapper;
import online.walletiq.repository.UserRepository;
import online.walletiq.service.UserService;
import online.walletiq.util.SecurityUtils;
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
