package com.walletiq.service;

import com.walletiq.dto.user.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserProfileResponse getUserProfile(String email);

    Page<UserProfileResponse> getAllUsers(Pageable pageable);

    UserProfileResponse getUserById(UUID id);

}