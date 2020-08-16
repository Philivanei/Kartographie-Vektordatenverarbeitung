package com.thd.mapserver.domain.exceptions;

public class PoiConnectionException extends Exception {
    public PoiConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoiConnectionException(Throwable cause) {
        super(cause);
    }
}
