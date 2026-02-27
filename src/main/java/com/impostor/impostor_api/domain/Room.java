package com.impostor.impostor_api.domain;

import java.util.*;

public class Room {

    private UUID id;
    private String code;
    private RoomStatus status = RoomStatus.LOBBY;
    private UUID hostPlayerId;

    private String category;
    private int impostorCount = 1;

    private int currentRound = 0;
    private String secretWord;
    private String winner;

    private final List<Player> players = new ArrayList<>();

    public Room(String hostNickname, String category, Integer impostorCount) {
        this.id = UUID.randomUUID();
        this.code = generateCode();
        this.category = category.toUpperCase();
        if (impostorCount != null) this.impostorCount = impostorCount;

        Player host = new Player(UUID.randomUUID(), this.id, hostNickname);
        this.hostPlayerId = host.getId();
        this.players.add(host);
    }

    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();

        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(r.nextInt(chars.length())));

        return sb.toString();
    }

    public Player addPlayer(String nickname) {
        Player p = new Player(UUID.randomUUID(), this.id, nickname);
        players.add(p);
        return p;
    }


    public UUID getId() { return id; }

    public String getCode() { return code; }

    public RoomStatus getStatus() { return status; }

    public void setStatus(RoomStatus status) { this.status = status; }

    public UUID getHostPlayerId() { return hostPlayerId; }

    public String getCategory() { return category; }

    public int getImpostorCount() { return impostorCount; }

    public int getCurrentRound() { return currentRound; }

    public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }

    public String getSecretWord() { return secretWord; }

    public void setSecretWord(String secretWord) { this.secretWord = secretWord; }

    public String getWinner() { return winner; }

    public void setWinner(String winner) { this.winner = winner; }

    public List<Player> getPlayers() { return players; }
}