package online.walletiq.controller;

import online.walletiq.dto.app.AppInfoResponse;
import online.walletiq.dto.success.ResponseWrapper;
import online.walletiq.service.ApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor()
@Tag(name = "Application", description = "Endpoints for application information")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/info")
    public ResponseEntity<ResponseWrapper<AppInfoResponse>> getAppInfo() {
        return ResponseEntity.ok(ResponseWrapper.ok(
            "Application info retrieved successfully",
            applicationService.getAppInfo())
        );
    }

}
