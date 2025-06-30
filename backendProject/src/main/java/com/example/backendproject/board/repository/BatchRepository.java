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


    public List<BoardDTO> findByBatchKey(String batchKey) {
        String sql = "SELECT b.id, b.title, b.content, b.user_id, b.created_date, b.updated_date, b.batchkey, up.username " +
                "FROM board b " +
                "JOIN `user` u ON b.user_id = u.id " +
                "JOIN user_profile up ON up.user_id = u.id " +
                "WHERE b.batchkey = ?";

        return jdbcTemplate.query(sql, new Object[]{batchKey}, (rs, rowNum) -> {
            BoardDTO dto = new BoardDTO();
            dto.setId(rs.getLong("id"));
            dto.setTitle(rs.getString("title"));
            dto.setContent(rs.getString("content"));
            dto.setUser_id(rs.getLong("user_id"));
            dto.setCreated_date(rs.getTimestamp("created_date") != null ? rs.getTimestamp("created_date").toLocalDateTime() : null);
            dto.setUpdated_date(rs.getTimestamp("updated_date") != null ? rs.getTimestamp("updated_date").toLocalDateTime() : null);
            dto.setBatchkey(rs.getString("batchkey"));
            dto.setUsername(rs.getString("username"));
            return dto;
        });


    }





}