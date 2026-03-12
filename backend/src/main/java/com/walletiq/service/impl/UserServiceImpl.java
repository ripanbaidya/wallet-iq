package com.walletiq.service.impl;

import com.walletiq.entity.User;
import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.UserException;
import com.walletiq.mapper.UserMapper;
import com.walletiq.dto.user.UserProfileResponse;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return UserMapper.toResponse(user);
    }

    @Override
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    @Override
    public UserProfileResponse getUserById(UUID id) {
        return userRepository.findById(id)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }


}
