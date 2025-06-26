package com.example.backendproject.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.board.dto.BoardDTO;
import com.example.backendproject.board.entity.Board;
import com.example.backendproject.board.service.BoardService;

import com.example.backendproject.security.core.CustomUserDetails;
import com.example.backendproject.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    /** 글 작성 **/
    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody BoardDTO boardDTO)  {
        Long id = customUserDetails.getId();
        boardDTO.setUser_id(id);
        BoardDTO created = boardService.createBoard(boardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** 게시글 상세 조회 **/
    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardDetail(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardDetail(id));
    }

    /** 게시글 수정 **/
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long id,
            @RequestBody BoardDTO boardDTO) {
        Long userid = customUserDetails.getId();
        if (userid.equals(boardDTO.getUser_id())) {
            //내가 쓴글이면 수정
            return ResponseEntity.ok(boardService.updateBoard(id, boardDTO));
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다");
        }
    }

    /** 게시글 삭제 **/
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long id) {
        Long userid = customUserDetails.getId();
        boardService.deleteBoard(userid,id);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }


    /** 페이징 적용 **/
    //페이징 적용 전체 목록보기
    //기본값은 0페이지 첫페이지입니다 페이지랑 10개 데이터를 불러옴
    @GetMapping
    public Page<BoardDTO> getBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return boardService.getBoards(page, size);
    }

    /** 주석해제하기 front  **/
    //페이징 적용 검색
    @GetMapping("/search")
    public Page<BoardDTO> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return boardService.searchBoardsPage(keyword, page, size);
    }




    /** 게시판 글 쓰기 배치 작업 **/
    @PostMapping("/batchInsert")
    public String batchInsert(@RequestBody List<BoardDTO> boardDTOList) {
        boardService.batchSaveBoard(boardDTOList);
        return "ok";
    }


    @PostMapping("/jpaBatchInsert")
    public String jpaBatchInsert(@RequestBody List<Board> board) {
        boardService.boardSaveAll(board);
        return "ok";
    }

}