package com.walletiq.annotation.api;

import com.walletiq.dto.error.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "400",
    description = "Invalid Request Payload",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
public @interface BadRequestResponse {
}
