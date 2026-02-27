package com.impostor.impostor_api.domain;

import java.util.UUID;

public class Vote {

    private UUID roomId;
    private int roundNumber;
    private UUID voterId;
    private UUID votedId;

    public Vote(UUID roomId, int roundNumber, UUID voterId, UUID votedId) {
        this.roomId = roomId;
        this.roundNumber = roundNumber;
        this.voterId = voterId;
        this.votedId = votedId;
    }

    public UUID getRoomId() { return roomId; }
    public int getRoundNumber() { return roundNumber; }
    public UUID getVoterId() { return voterId; }
    public UUID getVotedId() { return votedId; }
}