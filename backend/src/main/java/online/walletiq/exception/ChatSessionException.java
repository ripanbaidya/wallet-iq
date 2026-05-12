package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for chat session related failures.
 * <p>This exception is used when operations involving chat sessions fail,
 * such as creating, retrieving, or managing user chat interactions.
 */
public class ChatSessionException extends BusinessException {

    public ChatSessionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatSessionException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
