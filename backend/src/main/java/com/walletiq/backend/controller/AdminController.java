package com.walletiq.backend.controller;

import com.walletiq.backend.payload.response.UserProfileResponse;
import com.walletiq.backend.payload.response.success.ResponseWrapper;
import com.walletiq.backend.service.UserService;
import com.walletiq.backend.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin related operations")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper<List<UserProfileResponse>>> getAllUsers() {
        return ResponseUtil.ok("User's Fetched Successfully", userService.getAllUsers());
    }
}
