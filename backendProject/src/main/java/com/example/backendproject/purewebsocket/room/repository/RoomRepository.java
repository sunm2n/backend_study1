package com.example.backendproject.purewebsocket.room.repository;

import com.example.backendproject.purewebsocket.room.entitiy.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<ChatRoom, Long> {


    Optional<ChatRoom> findByRoomId(String roomId);
}
