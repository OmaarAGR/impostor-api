package com.impostor.impostor_api.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String msg) {
        super(msg);
    }
}