package com.walletiq.service;

import com.walletiq.dto.user.UserProfileResponse;
import com.walletiq.enums.Role;
import com.walletiq.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service layer contract for managing user profile operations.
 * <p>This interface defines APIs for both end-user and admin-level operations
 * related to user management such as profile retrieval, updates, and user analytics.</p>
 */
public interface UserService {

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return {@link UserProfileResponse} containing user details
     * @throws UserException if the current user cannot be resolved
     */
    UserProfileResponse fetchProfile();

    /**
     * Updates the full name of the currently authenticated user.
     *
     * @param fullName the new full name of the user; must not be blank
     * @return updated {@link UserProfileResponse}
     * @throws UserException if the current user cannot be resolved
     */
    UserProfileResponse updateProfile(String fullName);

    /**
     * Retrieves the profile of a user by their unique identifier.
     *
     * @param id unique identifier of the user
     * @return {@link UserProfileResponse} containing user details
     * @throws UserException if the user with given ID is not found
     */
    UserProfileResponse fetchProfileById(UUID id);

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable pagination and sorting information
     * @return {@link Page} of {@link UserProfileResponse}
     */
    Page<UserProfileResponse> fetchAllUsers(Pageable pageable);

    /**
     * Counts total users based on role and active status.
     *
     * @param role   role of the users to filter
     * @param active active status filter (true = active users, false = inactive users)
     * @return total count of matching users
     */
    long countTotalUsers(Role role, boolean active);
}