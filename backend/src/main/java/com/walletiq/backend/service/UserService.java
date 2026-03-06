package com.walletiq.backend.service;

import com.walletiq.backend.payload.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getUserProfile(String email);

}