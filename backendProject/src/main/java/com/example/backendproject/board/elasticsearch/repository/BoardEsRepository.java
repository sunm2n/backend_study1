package com.example.backendproject.board.elasticsearch.repository;

import com.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardEsRepository extends ElasticsearchRepository<BoardEsDocument, String> {

    // 문서 id로 데이터 삭제하는 쿼리메서드
    void deleteById(String id);

}
