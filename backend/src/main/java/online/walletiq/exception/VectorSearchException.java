package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for vector search related failures.
 * <p>This exception occurs when vector search operations fail, such as
 * embedding generation, similarity search, or vector database queries.
 */
public class VectorSearchException extends BusinessException {

    public VectorSearchException(ErrorCode errorCode) {
        super(errorCode);
    }

    public VectorSearchException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public VectorSearchException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}