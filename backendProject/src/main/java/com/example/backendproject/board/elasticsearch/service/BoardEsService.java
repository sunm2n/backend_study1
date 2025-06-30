package com.example.backendproject.board.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.PrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import com.example.backendproject.board.elasticsearch.repository.BoardEsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardEsService {

    // 엘라스틱서치에 명령을 전달하는 자바 API
    private final ElasticsearchClient client;
    private final BoardEsRepository repository;

    // 데이터 저장 메서드
    public void save(BoardEsDocument document) {
        repository.save(document);
    }

    // 데이터 삭제 메서드
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    // 검색 키워드와 페이지 번호와 페이지 크기를 받아서 엘라스틱서치에서 검색하는 메서드
    // 검색된 정보와 페이징 정보도 함께 반환하도록 하기 위해 page 객체를 사용하여 반환
    public Page<BoardEsDocument> search(String keyword, int page, int size) {

        try {

            // 엘라스틱서치에서 페이징을 위한 시작 위치를 계산하는 변수
            int from = page * size;

            // 엘라스틱서치에서 사용할 검색조건을 담는 객체
            Query query;

            // 검색어가 없으면 모든 문서를 검색하는 matchAll 쿼리
            if (keyword == null || keyword.isBlank()) {
                query = MatchAllQuery.of(m -> m)._toQuery(); // 전체 문서를 가져오는 쿼리를 생성하는 람다 함수
                // MatchAllQuery는 엘라스틱서치에서 조건 없이 모든 문서를 검색할 때 사용하는 쿼리
            }
            //검색어가 있을 때
            else {
                // boolquery는 복수 조건을 조합할 때 사용하는 쿼리
                // 이 쿼리 안에서 여러개의 조건을 나열
                // 예를 들어서 "백엔드"라는 키워드가 들어왔을 때
                query = BoolQuery.of(b -> {

                    //PrefixQuery는 해당 필드가 특정 단어로 시작되는지 검사하는 쿼리
                    //MatchQuery는 해당 단어가 포함되어 있는지 검색하는 쿼리

                    /**
                     must: 모두 일치해야 함 (AND)
                     should: 하나라도 일치하면 됨 (OR)
                     must_not: 해당 조건을 만족하면 제외
                     filter : must와 같지만 점수 계산 안함 (속도가 빠름)
                     **/

                    b.should(PrefixQuery.of(p -> p.field("title").value(keyword))._toQuery());
                    b.should(PrefixQuery.of(p -> p.field("content").value(keyword))._toQuery());

                    return b;

                })._toQuery();
            }

            // SearchRequest는 엘라스틱 서치에서 검색을 하기 위한 검색요청 객체
            // 인덱스명, 페이징 정보, 쿼리를 포함한 검색 요청
            SearchRequest request = SearchRequest.of(s -> s
                    .index("board-index")
                    .from(from)
                    .size(size)
                    .query(query)
            );
            // SearchResponse는 엘라스틱서치의 검색 결과를 담고 있는 응답 객체
            SearchResponse<BoardEsDocument> response =
                    // 엘라스틱서치에 명령을 전달하는 자바 API 검색요청을 담아서 응답객체로 반환
                    client.search(request, BoardEsDocument.class);
            // 위 응답객체에서 받은 검색 결과 중 문서만 추출해서 리스트로 만듬
            // Hit는 엘라스틱서치에서 검색된 문서 1개를 감싸고 있는 객체
            List<BoardEsDocument> contnet = response.hits() // 엘라스틱 서치 응답에서 hits(문서 검색결과) 전체를 꺼냄
                    .hits()// 검색 결과 안에 개별 리스트를 가져옴
                    .stream()// 자바 stream api를 사용
                    .map(Hit::source) // 각 Hit 객체에서 실제 문서를 꺼내는 작업
                    .collect(Collectors.toList()); // 위에서 꺼낸 객체를 자바 LIST에 넣음

            // 전체 검색 결과 수 (총 문서의 갯수)
            long total = response.hits().total().value();

            // pageImpl 객체를 사용해서 Spring에서 사용할 수 있는 page 객체로 변환
            return new PageImpl<>(contnet, PageRequest.of(page, size), total);

        } catch (IOException e) {
            log.error("검색 오류 ", e);
            throw new RuntimeException("검색 중 오류 발생", e);
        }
    }
}
