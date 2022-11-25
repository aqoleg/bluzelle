package com.bluzelle;

public class ServerException extends RuntimeException {
    ServerException(String message) {
        super(message);
    }
}