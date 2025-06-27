package com.example.backendproject.board.repository;

import com.example.backendproject.board.dto.BoardDTO;
import com.example.backendproject.board.entity.Board;
import com.example.backendproject.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {

        // 특정 User의 게시글만 조회
        //@Query(value = "SELECT * FROM board WHERE user_id = :userId", nativeQuery = true)  //이거 사용안해도 되는데 예시로 네이티브쿼라로 작성한거임
        List<Board> findByUserId(Long userId);







        //보드 엔티티를 기준으로 조회하되
        //Board 엔티티 전체를 반환하는게 아니라 원하는 값만 보드dto 생성자에 넣어서 리스트로 반환합니다
        //대소문자 구분없이 검색하는 옵션
        //title에 해당 키워드가 포함되어 있거나 content에 키워드가 포함되어잇는거 출력


        /** 페이징 적용 전 **/
        /** 검색기능 **/
        // 제목 또는 내용에 키워드가 포함된 글 검색 (대소문자 구분 없음)
        @Query("SELECT new com.example.backendproject.board.dto.BoardDTO(" +
                "b.id, b.title, b.content,b.user.userProfile.username, b.user.id, b.created_date, b.updated_date" +
                ") " +
                "FROM Board b " +
                "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<BoardDTO> searchKeyword(@Param("keyword") String keyword);


        /** 페이징 적용 후 **/
        //페이징 전체 목록
        @Query("SELECT new com.example.backendproject.board.dto.BoardDTO(" +
                "b.id, b.title, b.content,b.user.userProfile.username, b.user.id,b.created_date, b.updated_date) " +
                "FROM Board b ")
        //  + "ORDER BY b.title DESC") //쿼리로 정렬
        Page<BoardDTO> findAllPaging(Pageable pageable);
        //페이징 처리 결과를 담는 페이징 객체입니다.
        //전체 페이지수, 현재 페이지 번호,전체 아이템 겟수 등 페이징 관련 모든 정보들을 반환합니다.

        //Pageable은 jpa에서 제공하는 페이징 정보를 담은 객체입니다.
        //page번호, 한페이지당 데이터 갯수 ,정렬 기준 등  파라미터를 받아 원하는 조건으로 페이징 및 정렬 쿼리를 생성할 수 있습니다.

        //페이징 검색 목록
        @Query("SELECT new com.example.backendproject.board.dto.BoardDTO(" +
                "b.id, b.title, b.content,b.user.userProfile.username, b.user.id,  b.created_date, b.updated_date) " +
                "FROM Board b " +
                "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        // + "ORDER BY b.title DESC")// 쿼리로 정렬
        Page<BoardDTO> searchKeywordPaging(@Param("keyword") String keyword, Pageable pageable);















}