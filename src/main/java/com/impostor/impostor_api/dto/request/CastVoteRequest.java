package com.impostor.impostor_api.dto.request;

import java.util.UUID;

public class CastVoteRequest {
    private UUID votedId;

    public UUID getVotedId() {
        return votedId;
    }
    public void setVotedId(UUID votedId) {
        this.votedId = votedId;
    }
}