package com.walletiq.backend.service.impl;

import com.walletiq.backend.entity.User;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.exception.UserException;
import com.walletiq.backend.payload.response.UserProfileResponse;
import com.walletiq.backend.repository.UserRepository;
import com.walletiq.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return new UserProfileResponse(user.getId(), user.getFullName(),
            user.getEmail());
    }

}
