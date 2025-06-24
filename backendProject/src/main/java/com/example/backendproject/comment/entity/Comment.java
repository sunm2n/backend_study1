package com.example.backendproject.comment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.backendproject.board.entity.Board;
import com.example.backendproject.user.entity.BaseTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTime {

    /**
     댓글 과 대댓글 = 자기 자신을 참조하는 다대일 일대다 계층형구조
     **/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String content; //댓글 본문

    @Column(nullable = false)
    private Long userId;

    //다대일
    //하나의 게시글에 여러개의 댓글 구조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    @OnDelete(action = OnDeleteAction.CASCADE) //글이 삭제되면 관련 댓글 다 삭제
    private Board board;



    //여러개의 댓글은 하나의 부모 댓글을 가짐
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; //대댓글



    //하나의 댓글에 여러 대댓글을 가질 수 있음
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();



}