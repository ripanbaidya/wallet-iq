package com.walletiq.backend.controller;

import com.walletiq.backend.payload.response.UserProfileResponse;
import com.walletiq.backend.payload.response.success.ResponseWrapper;
import com.walletiq.backend.service.UserService;
import com.walletiq.backend.util.ResponseUtil;
import com.walletiq.backend.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<UserProfileResponse>> getProfile() {
        return ResponseUtil.ok("User profile fetched successfully",
            userService.getUserProfile(SecurityUtils.getCurrentUserEmail()));
    }
}
