package com.walletiq.controller;

import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.dto.user.UserProfileResponse;
import com.walletiq.service.UserService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin related operations")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllUsers(
        @Parameter(hidden = true)
        Pageable pageable
    ) {
        var response = userService.getAllUsers(pageable);
        return ResponseUtil.paginated("User's Fetched Successfully", response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseWrapper<UserProfileResponse>> getUserById(
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok("User Fetched Successfully", userService.getUserById(id));
    }
}
