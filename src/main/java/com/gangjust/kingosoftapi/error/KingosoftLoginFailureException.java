package com.gangjust.kingosoftapi.error;

public class KingosoftLoginFailureException extends Exception {
    public KingosoftLoginFailureException(String message) {
        super(message);
    }

    public KingosoftLoginFailureException() {
        super();
    }

    public KingosoftLoginFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public KingosoftLoginFailureException(Throwable cause) {
        super(cause);
    }

    protected KingosoftLoginFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
