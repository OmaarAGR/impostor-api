package com.impostor.impostor_api.dto.response;

import java.util.UUID;

public class JoinRoomResponse {

    private UUID playerId;
    private String nickname;

    public JoinRoomResponse(UUID playerId, String nickname) {
        this.playerId = playerId;
        this.nickname = nickname;
    }

    public UUID getPlayerId() { return playerId; }
    public String getNickname() { return nickname; }
}