package com.walletiq.service;

import com.walletiq.dto.user.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserProfileResponse fetchProfile();

    UserProfileResponse updateProfile(String fullName);

    // Admin Specific

    UserProfileResponse fetchProfileById(UUID id);

    Page<UserProfileResponse> fetchAllUsers(Pageable pageable);

    long countTotalUsers();

}