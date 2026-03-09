package com.walletiq.backend.service;

import com.walletiq.backend.dto.user.UserProfileResponse;

import java.util.List;

public interface UserService {

    UserProfileResponse getUserProfile(String email);

    List<UserProfileResponse> getAllUsers();

}