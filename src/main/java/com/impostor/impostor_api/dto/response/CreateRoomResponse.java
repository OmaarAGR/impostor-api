package com.impostor.impostor_api.dto.response;

import java.util.UUID;

public class CreateRoomResponse {

    private String roomCode;
    private UUID hostPlayerId;

    public CreateRoomResponse(String roomCode, UUID hostPlayerId) {
        this.roomCode = roomCode;
        this.hostPlayerId = hostPlayerId;
    }

    public String getRoomCode() { return roomCode; }
    public UUID getHostPlayerId() { return hostPlayerId; }
}