package com.walletiq.backend.service.impl;

import com.walletiq.backend.entity.User;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.exception.UserException;
import com.walletiq.backend.mapper.UserMapper;
import com.walletiq.backend.payload.response.UserProfileResponse;
import com.walletiq.backend.repository.UserRepository;
import com.walletiq.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return UserMapper.mapToUserProfileResponse(user);
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserMapper::mapToUserProfileResponse)
            .toList();
    }

}
