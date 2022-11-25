package com.bluzelle;

public class ConnectionException extends RuntimeException {
    ConnectionException(Exception e) {
        super(e);
    }
}