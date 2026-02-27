package com.impostor.impostor_api.domain;

import java.util.UUID;

public class Assignment {

    private UUID roomId;
    private UUID playerId;
    private Role role;
    private String word;

    public Assignment(UUID roomId, UUID playerId, Role role, String word) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.role = role;
        this.word = word;
    }

    public UUID getRoomId() { return roomId; }
    public UUID getPlayerId() { return playerId; }
    public Role getRole() { return role; }
    public String getWord() { return word; }
}