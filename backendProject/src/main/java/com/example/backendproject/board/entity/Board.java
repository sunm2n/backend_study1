package com.example.backendproject.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.backendproject.comment.entity.Comment;
import com.example.backendproject.user.entity.BaseTime;
import com.example.backendproject.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String batchkey;


    //아래는 글을 작성한 유저 정보
    // 연관관계 맵핑
    //다대일
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    //일대다
    @OneToMany(mappedBy = "board")
    private List<Comment> comment = new ArrayList<>();


}