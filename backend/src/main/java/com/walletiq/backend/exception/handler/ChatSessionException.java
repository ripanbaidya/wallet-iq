package com.walletiq.backend.exception.handler;

import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.exception.BaseException;

public class ChatSessionException extends BaseException {

    public ChatSessionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatSessionException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
