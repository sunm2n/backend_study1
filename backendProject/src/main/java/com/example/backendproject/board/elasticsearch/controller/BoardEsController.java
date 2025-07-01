package com.example.backendproject.board.elasticsearch.controller;

import lombok.RequiredArgsConstructor;
import com.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import com.example.backendproject.board.elasticsearch.service.BoardEsService;
import com.example.backendproject.board.searchlog.dto.SearchLogMessage;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardEsController {

    private final BoardEsService boardEsService;

    //스프링에서  카프카로 메세지를 전송하기 위한 컴포넌트
    private final KafkaTemplate<String,SearchLogMessage> kafkaTemplate;

    @GetMapping("/elasticsearch")
    //엘라스틱서치 검색 결과를 page 형태로 감싼 다음 HTTP 응답을 json으로 반환
    public ResponseEntity<Page<BoardEsDocument>> elasticSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        //검색어 정보 카프카 전송
        String userId = "1";
        String searchedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        SearchLogMessage message = new SearchLogMessage(keyword,userId,searchedAt);
        kafkaTemplate.send("search-log",message);  //search-log 토픽으로 메세지 전달



        return ResponseEntity.ok(boardEsService.search(keyword, page, size));
    }

    // localhost:8080/boards/top-keywords
    @GetMapping("/top-keywords")
    public ResponseEntity<List<String>> getTopKeyWord() {
        List<String> keywords = boardEsService.getTopSearchKeyword();
        return ResponseEntity.ok(keywords);
    }








}