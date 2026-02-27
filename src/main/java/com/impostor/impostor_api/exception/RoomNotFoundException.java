package com.impostor.impostor_api.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String code) {
        super("Sala no existe: " + code);
    }
}