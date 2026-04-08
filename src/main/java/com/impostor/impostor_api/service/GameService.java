package com.impostor.impostor_api.service;

import com.impostor.impostor_api.domain.*;
import com.impostor.impostor_api.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final RoomRepository roomRepo;
    private final WordBank wordBank;

    public GameService(RoomRepository roomRepo, WordBank wordBank) {
        this.roomRepo = roomRepo;
        this.wordBank = wordBank;
    }

    public void startGame(String code, UUID hostPlayerId) {
        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        if (room.getStatus() != RoomStatus.LOBBY)
            throw new RuntimeException("No se puede iniciar: sala no esta en LOBBY");

        if (!room.getHostPlayerId().equals(hostPlayerId))
            throw new RuntimeException("Solo el host puede iniciar");

        List<Player> players = room.getPlayers();
        long aliveCount = players.stream().filter(Player::isAlive).count();

        if (aliveCount < 3)
            throw new RuntimeException("Minimo 3 jugadores vivos para iniciar");

        if (!wordBank.exists(room.getCategory()))
            throw new RuntimeException("Categoria no existe");

        String secretWord = wordBank.random(room.getCategory());
        room.setSecretWord(secretWord);

        List<Player> alivePlayers = players.stream().filter(Player::isAlive).collect(Collectors.toList());
        Collections.shuffle(alivePlayers);

        Player impostor = alivePlayers.get(0);

        for (Player p : alivePlayers) {
            if (p.getId().equals(impostor.getId())) {
                roomRepo.saveAssignment(new Assignment(room.getId(), p.getId(), Role.IMPOSTOR, null));
            } else {
                roomRepo.saveAssignment(new Assignment(room.getId(), p.getId(), Role.CIVIL, secretWord));
            }
        }

        room.setStatus(RoomStatus.IN_GAME);
        room.setCurrentRound(1);

        roomRepo.save(room);
    }

    public void vote(String code, UUID voterId, UUID votedId) {
        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        if (room.getStatus() != RoomStatus.IN_GAME)
            throw new RuntimeException("No se puede votar: sala no en IN_GAME");

        if (votedId == null) throw new RuntimeException("votedId es obligatorio");

        Player voter = room.getPlayers().stream()
                .filter(p -> p.getId().equals(voterId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Votante no pertenece a la sala"));

        Player voted = room.getPlayers().stream()
                .filter(p -> p.getId().equals(votedId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Votado no pertenece a la sala"));

        if (!voter.isAlive()) throw new RuntimeException("Votante esta muerto/inactivo");
        if (!voted.isAlive()) throw new RuntimeException("No se puede votar por un muerto/inactivo");

        int round = room.getCurrentRound();

        if (roomRepo.alreadyVoted(room.getId(), round, voterId))
            throw new RuntimeException("Doble voto en la misma ronda");

        roomRepo.saveVote(new Vote(room.getId(), round, voterId, votedId));
    }

    public String closeRound(String code, UUID hostPlayerId) {
        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        if (room.getStatus() != RoomStatus.IN_GAME)
            throw new RuntimeException("No se puede cerrar ronda: sala no esta en IN_GAME");

        if (!room.getHostPlayerId().equals(hostPlayerId))
            throw new RuntimeException("Solo el host puede cerrar ronda");

        int round = room.getCurrentRound();
        List<Vote> votes = roomRepo.findVotesByRoomAndRound(room.getId(), round);

        if (votes.isEmpty())
            throw new RuntimeException("No se puede cerrar ronda sin votos");

        Map<UUID, Long> count = votes.stream()
                .collect(Collectors.groupingBy(Vote::getVotedId, Collectors.counting()));

        long maxVotes = count.values().stream().mapToLong(v -> v).max().orElse(0);

        List<UUID> top = count.entrySet().stream()
                .filter(e -> e.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .toList();

        if (top.size() > 1) {
            room.setCurrentRound(round + 1);
            roomRepo.save(room);
            return "EMPATE - SIGUE";
        }

        UUID expelledId = top.get(0);

        Player expelled = room.getPlayers().stream()
                .filter(p -> p.getId().equals(expelledId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expulsado no existe en sala"));

        expelled.setAlive(false);

        Assignment expelledAssign = roomRepo.findAssignment(expelledId)
                .orElseThrow(() -> new RuntimeException("No hay asignacion (¿no has iniciado el juego?)"));

        if (expelledAssign.getRole() == Role.IMPOSTOR) {
            room.setStatus(RoomStatus.FINISHED);
            room.setWinner("CIVILES");
            roomRepo.save(room);
            return "FINISHED - CIVILES";
        }

        List<Player> alivePlayers = room.getPlayers().stream().filter(Player::isAlive).toList();

        boolean impostorAlive = alivePlayers.stream().anyMatch(p ->
                roomRepo.findAssignment(p.getId())
                        .map(a -> a.getRole() == Role.IMPOSTOR)
                        .orElse(false)
        );

        if (alivePlayers.size() == 2 && impostorAlive) {
            room.setStatus(RoomStatus.FINISHED);
            room.setWinner("IMPOSTORES");
            roomRepo.save(room);
            return "FINISHED - IMPOSTORES";
        }

        room.setCurrentRound(round + 1);
        roomRepo.save(room);
        return "SIGUE";
    }
}