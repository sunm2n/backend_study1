package com.example.backendproject.board.repository;


import lombok.RequiredArgsConstructor;
import com.example.backendproject.board.dto.BoardDTO;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<BoardDTO> boardDTO){

        String sql = "INSERT INTO board (title, content, user_id, created_date, updated_date,batchkey) VALUES (?, ?, ?, ?, ?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                BoardDTO dto = boardDTO.get(i);
                ps.setString(1, dto.getTitle());
                ps.setString(2, dto.getContent());
                ps.setLong(3, dto.getUser_id());
                ps.setString(4, String.valueOf(dto.getCreated_date()));
                ps.setString(5, String.valueOf(dto.getUpdated_date()));
                ps.setString(6, dto.getBatchkey());

            }

            @Override
            public int getBatchSize() {
                return boardDTO.size();
            }
        });
    }

}