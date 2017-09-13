package com.example.sunray.ftpforandroid.FTPUtil;

/**
 * Created by sunray on 2017-9-5.
 */

public class FTPRuntimeException extends RuntimeException {
    public FTPRuntimeException() {
        super();
    }

    public FTPRuntimeException(String message) {
        super(message);
    }

    public FTPRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FTPRuntimeException(Throwable cause) {
        super(cause);
    }
}
