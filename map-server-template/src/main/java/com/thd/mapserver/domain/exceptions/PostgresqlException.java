package com.thd.mapserver.domain.exceptions;

public class PostgresqlException extends PoiConnectionException {

    private static final long serialVersionUID = 1L;

    public PostgresqlException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

