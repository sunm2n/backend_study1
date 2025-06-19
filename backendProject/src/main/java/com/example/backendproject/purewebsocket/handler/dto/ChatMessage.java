package com.example.backendproject.purewebsocket.handler.dto;


public class ChatMessage {

    private String roomId;
    private String message;
    private String from;

    public String getRoomID() {
        return roomId;
    }

    public void setRoomID(String roomID) {
        this.roomId = roomID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
