package com.impostor.impostor_api.domain;

import java.util.UUID;

public class Player {

    private UUID id;
    private UUID roomId;
    private String nickname;
    private boolean alive = true;

    public Player(UUID id, UUID roomId, String nickname) {
        this.id = id;
        this.roomId = roomId;
        this.nickname = nickname;
    }

    public UUID getId() { return id; }
    public UUID getRoomId() { return roomId; }
    public String getNickname() { return nickname; }
    public boolean isAlive() { return alive; }

    public void setAlive(boolean alive) { this.alive = alive; }
}