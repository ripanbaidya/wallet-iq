package com.walletiq.controller;

import com.walletiq.dto.notification.NotificationResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.NotificationService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<NotificationResponse>>> getAll() {
        return ResponseUtil.ok("Notifications fetched successfully ",
            notificationService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteById(@PathVariable UUID id) {
        notificationService.deleteById(id);
        return ResponseUtil.ok("Notification deleted successfully");
    }

    @DeleteMapping
    public ResponseEntity<ResponseWrapper<Void>> deleteAll() {
        notificationService.deleteAll();
        return ResponseUtil.ok("All notifications deleted successfully.");
    }
}