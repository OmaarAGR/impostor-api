package com.impostor.impostor_api.repository;

import com.impostor.impostor_api.domain.Player;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PlayerRepository {

    private final Map<UUID, Player> players = new HashMap<>();

    public void save(Player p){
        players.put(p.getId(), p);
    }

    public Optional<Player> findById(UUID id){
        return Optional.ofNullable(players.get(id));
    }

    public List<Player> findByRoom(UUID roomId){
        return players.values()
                .stream()
                .filter(p -> p.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }
}