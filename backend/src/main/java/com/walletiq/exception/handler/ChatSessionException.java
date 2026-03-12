package com.walletiq.exception.handler;

import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.BaseException;

public class ChatSessionException extends BaseException {

    public ChatSessionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatSessionException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
