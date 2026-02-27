package com.impostor.impostor_api.controller;

import com.impostor.impostor_api.domain.Assignment;
import com.impostor.impostor_api.domain.Player;
import com.impostor.impostor_api.domain.Room;
import com.impostor.impostor_api.dto.request.CastVoteRequest;
import com.impostor.impostor_api.dto.request.CreateRoomRequest;
import com.impostor.impostor_api.dto.request.JoinRoomRequest;
import com.impostor.impostor_api.dto.response.CreateRoomResponse;
import com.impostor.impostor_api.dto.response.JoinRoomResponse;
import com.impostor.impostor_api.repository.RoomRepository;
import com.impostor.impostor_api.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepo;
    private final GameService gameService;

    public RoomController(RoomRepository roomRepo, GameService gameService) {
        this.roomRepo = roomRepo;
        this.gameService = gameService;
    }

    @PostMapping
    public CreateRoomResponse createRoom(@RequestBody CreateRoomRequest request) {

        Room room = new Room(
                request.getHostNickname(),
                request.getCategory(),
                request.getImpostorCount()
        );

        roomRepo.save(room);
        return new CreateRoomResponse(room.getCode(), room.getHostPlayerId());
    }

    @PostMapping("/{code}/players")
    public JoinRoomResponse join(@PathVariable String code, @RequestBody JoinRoomRequest request) {

        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        if (room.getStatus().name().equals("IN_GAME") || room.getStatus().name().equals("FINISHED")) {
            throw new RuntimeException("No se puede unir: sala no esta en LOBBY");
        }

        Player p = room.addPlayer(request.getNickname());
        roomRepo.save(room);

        return new JoinRoomResponse(p.getId(), p.getNickname());
    }

    @GetMapping("/{code}")
    public Map<String, Object> getRoom(@PathVariable String code) {

        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        List<Map<String, Object>> players = room.getPlayers().stream()
                .map(p -> {
                    Map<String, Object> playerInfo = new HashMap<>();
                    playerInfo.put("id", p.getId());
                    playerInfo.put("nickname", p.getNickname());
                    playerInfo.put("alive", p.isAlive());
                    return playerInfo;
                })
                .toList();

        return Map.of(
                "status", room.getStatus(),
                "category", room.getCategory(),
                "currentRound", room.getCurrentRound(),
                "players", players
        );
    }

    @PostMapping("/{code}/start")
    public Map<String, Object> start(@PathVariable String code, @RequestParam UUID hostPlayerId) {

        gameService.startGame(code, hostPlayerId);

        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        return Map.of(
                "status", room.getStatus(),
                "currentRound", room.getCurrentRound()
        );
    }

    @GetMapping("/{code}/me")
    public Map<String, Object> me(@PathVariable String code, @RequestParam UUID playerId) {

        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        boolean belongs = room.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId));
        if (!belongs) throw new RuntimeException("Jugador no pertenece a esta sala");

        Assignment a = roomRepo.findAssignment(playerId)
                .orElseThrow(() -> new RuntimeException("No hay asignacion (¿ya iniciaste?)"));

        return Map.of(
                "role", a.getRole(),
                "word", a.getWord()
        );
    }

    @PostMapping("/{code}/votes")
    public Map<String, Object> vote(@PathVariable String code,
                                    @RequestParam UUID voterId,
                                    @RequestBody CastVoteRequest body) {

        gameService.vote(code, voterId, body.getVotedId());

        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        return Map.of(
                "message", "Voto registrado",
                "round", room.getCurrentRound()
        );
    }

    @PostMapping("/{code}/round/close")
    public Map<String, Object> closeRound(@PathVariable String code,
                                          @RequestParam UUID hostPlayerId) {

        String result = gameService.closeRound(code, hostPlayerId);

        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        if (room.getStatus().name().equals("FINISHED")) {
            List<Map<String, Object>> reveal = room.getPlayers().stream()
                    .map(p -> {
                        Assignment a = roomRepo.findAssignment(p.getId()).orElse(null);
                        Map<String, Object> playerInfo = new HashMap<>();
                        playerInfo.put("playerId", p.getId());
                        playerInfo.put("nickname", p.getNickname());
                        playerInfo.put("role", (a == null ? "UNKNOWN" : a.getRole().name()));
                        return playerInfo;
                    })
                    .toList();

            return Map.of(
                    "roundClosed", room.getCurrentRound(),
                    "status", room.getStatus(),
                    "winner", room.getWinner(),
                    "secretWord", room.getSecretWord(),
                    "reveal", reveal
            );
        }

        long aliveCount = room.getPlayers().stream().filter(Player::isAlive).count();

        return Map.of(
                "roundClosed", room.getCurrentRound() - 1,
                "status", room.getStatus(),
                "message", result,
                "nextRound", room.getCurrentRound(),
                "aliveCount", aliveCount
        );
    }
}