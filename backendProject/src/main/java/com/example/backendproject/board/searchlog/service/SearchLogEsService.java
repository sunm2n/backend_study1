package com.example.backendproject.board.searchlog.service;

import com.example.backendproject.board.searchlog.domain.SearchLogDocument;
import com.example.backendproject.board.searchlog.repository.SearchLogEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchLogEsService {

    // 엘라스틱 서치 저장. 통계 집계 비즈니스 로식 서비스

    private final SearchLogEsRepository searchLogEsRepository;

    // 카프카에서 전달 받은 검색데이터 저장 메서드

    public void save(SearchLogDocument searchLogDocument) {

        searchLogEsRepository.save(searchLogDocument);
    }
}
