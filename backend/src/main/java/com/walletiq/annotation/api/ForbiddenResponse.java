package com.walletiq.annotation.api;

import com.walletiq.dto.error.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ApiResponse(
    responseCode = "403",
    description = "Forbidden",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
public @interface ForbiddenResponse {
}
