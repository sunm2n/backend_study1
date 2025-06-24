package com.example.backendproject.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.backendproject.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;
    private String username;
    private String content;
    private Long userId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long boardId;
    private Long parentId;             // 부모 댓글 id (null이면 최상위 댓글)
    private List<CommentDTO> children; // 대댓글(자식 댓글) 리스트


    public static CommentDTO fromEntity(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setUsername(comment.getUsername());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUserId());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setCreatedDate(comment.getCreated_date());
        dto.setBoardId(comment.getBoard().getId());
        return dto;
    }

}