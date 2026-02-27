package com.impostor.impostor_api.repository;

import com.impostor.impostor_api.domain.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class RoomRepository {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<UUID, Assignment> assignments = new ConcurrentHashMap<>();
    private final List<Vote> votes = new CopyOnWriteArrayList<>();

    // ================= ROOMS =================

    public Room save(Room room) {
        rooms.put(room.getCode(), room);
        return room;
    }

    public Optional<Room> findByCode(String code) {
        return Optional.ofNullable(rooms.get(code));
    }

    // ================= ASSIGNMENTS =================

    public void saveAssignment(Assignment assignment) {
        assignments.put(assignment.getPlayerId(), assignment);
    }

    public Optional<Assignment> findAssignment(UUID playerId) {
        return Optional.ofNullable(assignments.get(playerId));
    }

    // ================= VOTES =================

    public void saveVote(Vote vote) {
        votes.add(vote);
    }

    public List<Vote> findVotesByRoomAndRound(UUID roomId, int round) {
        return votes.stream()
                .filter(v ->
                        v.getRoomId().equals(roomId) &&
                                v.getRoundNumber() == round
                )
                .toList();
    }

    public boolean alreadyVoted(UUID roomId, int round, UUID voterId) {
        return votes.stream().anyMatch(v ->
                v.getRoomId().equals(roomId) &&
                        v.getRoundNumber() == round &&
                        v.getVoterId().equals(voterId)
        );
    }
}