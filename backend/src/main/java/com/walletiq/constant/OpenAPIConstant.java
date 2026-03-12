package com.walletiq.constant;

public final class OpenAPIConstant {

    private OpenAPIConstant() {
    }

    public static final String AUTH_DESCRIPTION = """
        Authentication
        
        **Note**: All protected endpoints require a **JWT Bearer Token**.
        
        Include the token in the <b>Authorization<b> header: 
        
        `Authorization: Bearer <JWT_TOKEN>`
        
        You can obtain the token from the **Authentication → Login** endpoint.
        """;

    public static final String API_DESCRIPTION = """
        **WalletIQ** is a **RAG-powered Wallet Management Platform** designed to help users
        track expenses, manage financial data, and gain insights using AI.
        
        %s
        """;
}
