package com.impostor.impostor_api.repository;

import com.impostor.impostor_api.domain.Assignment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AssignmentRepository {

    private final List<Assignment> assignments = new ArrayList<>();

    public void save(Assignment a){
        assignments.add(a);
    }

    public Optional<Assignment> find(UUID roomId, UUID playerId){
        return assignments.stream()
                .filter(a->a.getRoomId().equals(roomId)&&a.getPlayerId().equals(playerId))
                .findFirst();
    }

    public List<Assignment> findByRoom(UUID roomId){
        return assignments.stream()
                .filter(a->a.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }
}