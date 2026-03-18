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
    responseCode = "401",
    description = "Unauthorized",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
public @interface UnauthorizedResponse {
}
