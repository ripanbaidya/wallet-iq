package online.walletiq.constant;

/**
 * Centralized constants used for configuring and documenting
 * OpenAPI / Swagger metadata across the application.
 */
public final class OpenAPIConstant {

    /**
     * Prevents instantiation of this utility class.
     */
    private OpenAPIConstant() {
    }

    /**
     * Standard authentication description displayed in API documentation.
     * Explains how clients should provide the JWT Bearer token when accessing
     * protected endpoints.
     */
    public static final String AUTH_DESCRIPTION = """
        <b>Note:</b> All protected endpoints require a JWT Bearer token.<br>
        Include it in the request header:<br>
        <code>Authorization: Bearer &lt;JWT_TOKEN&gt;</code><br><br>
        
        You can obtain the token from the <b>Authentication → Login</b> endpoint.
        """;

    /**
     * High-level description of the WalletIQ API used in Swagger UI.
     */
    public static final String API_DESCRIPTION = """
        <b>WalletIQ</b> is a RAG-based expense tracking system that helps users manage
        expenses, budgets, goals, and recurring transactions, while providing analytical dashboards.
        <br>
        Users can also leverage AI-powered insights and recommendations based on their own financial data.
        
        %s
        """;
}