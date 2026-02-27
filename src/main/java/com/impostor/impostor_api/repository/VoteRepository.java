package com.impostor.impostor_api.repository;

import com.impostor.impostor_api.domain.Vote;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class VoteRepository {

    private final List<Vote> votes = new ArrayList<>();

    public void save(Vote v){
        votes.add(v);
    }

    public boolean alreadyVoted(UUID roomId, int round, UUID voterId){
        return votes.stream().anyMatch(v ->
                v.getRoomId().equals(roomId) &&
                        v.getRoundNumber()==round &&
                        v.getVoterId().equals(voterId)
        );
    }

    public List<Vote> findByRoomAndRound(UUID roomId,int round){
        return votes.stream()
                .filter(v -> v.getRoomId().equals(roomId)&&v.getRoundNumber()==round)
                .collect(Collectors.toList());
    }
}