package com.bkwinners.ksnet.dpt.design.appToApp;

public class ParameterException extends Throwable {

    public ParameterException() {
        super();
    }
    public ParameterException(String message) {
        super(message);
    }
    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterException(Throwable cause) {
        super(cause);
    }

    protected ParameterException(String message, Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
