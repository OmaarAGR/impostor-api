package com.impostor.impostor_api.service;

import com.impostor.impostor_api.domain.Room;
import com.impostor.impostor_api.dto.request.CreateRoomRequest;
import com.impostor.impostor_api.dto.request.JoinRoomRequest;
import com.impostor.impostor_api.dto.response.CreateRoomResponse;
import com.impostor.impostor_api.dto.response.JoinRoomResponse;
import com.impostor.impostor_api.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RoomRepository roomRepo;

    public RoomService(RoomRepository roomRepo) {
        this.roomRepo = roomRepo;
    }

    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        Room room = new Room(
                request.getHostNickname(),
                request.getCategory(),
                request.getImpostorCount()
        );

        roomRepo.save(room);
        return new CreateRoomResponse(room.getCode(), room.getHostPlayerId());
    }

    public JoinRoomResponse joinPlayer(String code, JoinRoomRequest request) {
        Room room = roomRepo.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sala no existe"));

        var player = room.addPlayer(request.getNickname());
        roomRepo.save(room);

        return new JoinRoomResponse(player.getId(), player.getNickname());
    }
}