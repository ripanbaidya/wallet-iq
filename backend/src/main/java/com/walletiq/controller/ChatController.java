package com.walletiq.controller;

import com.walletiq.dto.chat.*;
import com.walletiq.dto.success.ResponseWrapper;
import com.walletiq.service.ChatService;
import com.walletiq.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Chat", description = "RAG-powered finance assistant for querying and analyzing user expenses")
public class ChatController {

    private final ChatService chatService;

    @Operation(
        summary = "Get chat sessions",
        description = "Fetches all chat sessions created by the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sessions fetched successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/sessions")
    public ResponseEntity<ResponseWrapper<List<ChatSessionResponse>>> getSessions() {
        return ResponseUtil.ok("Sessions fetched", chatService.getSessions());
    }

    @Operation(
        summary = "Create chat session",
        description = "Creates a new chat session for interacting with the AI finance assistant."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Session created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/sessions")
    public ResponseEntity<ResponseWrapper<ChatSessionResponse>> createSession(
        @Valid @RequestBody CreateSessionRequest request
    ) {
        return ResponseUtil.created("Session created", chatService.createSession(request));
    }

    @Operation(
        summary = "Delete chat session",
        description = "Deletes an existing chat session along with its messages."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteSession(
        @Parameter(description = "Unique identifier of the chat session", required = true)
        @PathVariable UUID id
    ) {
        chatService.deleteSession(id);
        return ResponseUtil.ok("Session deleted", null);
    }

    @Operation(
        summary = "Get session messages",
        description = "Fetches all chat messages within a specific chat session."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Messages fetched successfully"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/sessions/{id}/messages")
    public ResponseEntity<ResponseWrapper<List<ChatMessageResponse>>> getMessages(
        @Parameter(description = "Unique identifier of the chat session", required = true)
        @PathVariable UUID id
    ) {
        return ResponseUtil.ok("Messages fetched", chatService.getMessages(id));
    }

    @Operation(
        summary = "Query AI finance assistant",
        description = "Sends a query to the AI finance assistant. The assistant analyzes the user's financial data using RAG and returns insights."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Query answered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "404", description = "Session not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/sessions/{id}/query")
    public ResponseEntity<ResponseWrapper<ChatQueryResponse>> query(
        @Parameter(description = "Unique identifier of the chat session", required = true)
        @PathVariable UUID id,
        @Valid @RequestBody ChatQueryRequest request
    ) {
        return ResponseUtil.ok("Query answered", chatService.query(id, request));
    }
}