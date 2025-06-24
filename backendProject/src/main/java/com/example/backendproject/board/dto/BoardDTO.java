package com.example.backendproject.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long id;
    private String title;
    private String content;

    private String username;

    private Long user_id;

    private LocalDateTime created_date;
    private LocalDateTime updated_date;

    private String batchkey;

    public BoardDTO(Long id, String title, String content,String username, Long user_id,  LocalDateTime created_date, LocalDateTime updated_date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.user_id = user_id;
        this.created_date = created_date;
        this.updated_date = updated_date;
    }
}