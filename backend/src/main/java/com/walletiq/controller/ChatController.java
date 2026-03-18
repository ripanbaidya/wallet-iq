package com.walletiq.controller;

import com.walletiq.dto.chat.*;
import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.ChatService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(
    name = "Chat",
    description = "RAG-powered finance assistant for querying and analyzing user expenses"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing token",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
})
public class ChatController {

    private final ChatService chatService;

    @Operation(
        summary = "Get chat sessions",
        description = "Fetches all chat sessions created by the authenticated user, ordered by most recent activity."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Sessions fetched successfully",
            content = @Content(schema = @Schema(implementation = ChatSessionResponse.class))
        )
    })
    @GetMapping("/sessions")
    public ResponseEntity<ResponseWrapper<List<ChatSessionResponse>>> getSessions() {
        return ResponseUtil.ok(
            "Sessions fetched successfully",
            chatService.getSessions()
        );
    }

    @Operation(
        summary = "Create chat session",
        description = "Creates a new chat session. If no title is provided, a default title 'New Chat' is assigned."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Session created successfully",
            content = @Content(schema = @Schema(implementation = ChatSessionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/sessions")
    public ResponseEntity<ResponseWrapper<ChatSessionResponse>> createSession(
        @Valid @RequestBody CreateSessionRequest request
    ) {
        return ResponseUtil.created(
            "Session created successfully",
            chatService.createSession(request)
        );
    }

    @Operation(
        summary = "Delete chat session",
        description = "Deletes a chat session along with all associated messages."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Session deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Session not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteSession(

        @Parameter(description = "Unique identifier of the chat session", required = true)
        @PathVariable UUID id
    ) {
        chatService.deleteSession(id);
        return ResponseUtil.ok("Session deleted successfully", null);
    }

    @Operation(
        summary = "Get session messages",
        description = "Fetches all messages for a specific chat session in chronological order."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Messages fetched successfully",
            content = @Content(schema = @Schema(implementation = ChatMessageResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Session not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/sessions/{id}/messages")
    public ResponseEntity<ResponseWrapper<List<ChatMessageResponse>>> getMessages(

        @Parameter(description = "Unique identifier of the chat session", required = true)
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok(
            "Messages fetched successfully",
            chatService.getMessages(id)
        );
    }

    @Operation(
        summary = "Query AI finance assistant",
        description = "Processes a user query using RAG-based analysis and returns financial insights."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Query answered successfully",
            content = @Content(schema = @Schema(implementation = ChatQueryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Session not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/sessions/{id}/query")
    public ResponseEntity<ResponseWrapper<ChatQueryResponse>> query(

        @Parameter(description = "Unique identifier of the chat session", required = true)
        @PathVariable UUID id,

        @Valid @RequestBody ChatQueryRequest request
    ) {
        return ResponseUtil.ok(
            "Query answered successfully",
            chatService.query(id, request)
        );
    }
}