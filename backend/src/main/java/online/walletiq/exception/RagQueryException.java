package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for RAG query processing failures.
 * <p>This exception represents errors that occur during Retrieval-Augmented
 * Generation (RAG) query processing, such as query execution, context
 * retrieval, or response generation failures.
 */
public class RagQueryException extends BusinessException {

    public RagQueryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RagQueryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RagQueryException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}