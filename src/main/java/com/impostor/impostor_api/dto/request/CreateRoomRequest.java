package com.impostor.impostor_api.dto.request;

public class CreateRoomRequest {

    private String hostNickname;
    private String category;
    private int impostorCount;

    public String getHostNickname() { return hostNickname; }
    public String getCategory() { return category; }
    public int getImpostorCount() { return impostorCount; }
}