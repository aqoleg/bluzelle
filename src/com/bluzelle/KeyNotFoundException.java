package com.bluzelle;

public class KeyNotFoundException extends RuntimeException {
    KeyNotFoundException(String key) {
        super("key \"" + key + "\" not found");
    }
}