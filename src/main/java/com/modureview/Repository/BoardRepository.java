package com.modureview.Repository;

import com.modureview.Entity.Board;
import com.modureview.Entity.Status.Category;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board,Long>{

  //게시글 상세 조회
  @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.Id = :Board ")
  Optional<Board> findByIdWithUserAndCommentsAndFiles(@Param("BoardId") Long BoardId);


  //첫 페이징 화면 ("/")->쓸지 잘 모르겠음,  아마 무한대로 나타날꺼 같은데 그냥 게시물의 몇개? 의 로딩 구성으로 나  타낼 가능성이 매우 높음
  @Query(value = "SELECT b FROM Board b JOIN FETCH b.user")
  Page<Board> findAllWithUserAndComments(Pageable pageable);

  //첫 페이징 화면 (STATUS가 DELETE가 아닌 페이지를 가져옵니다.)
  @Query("SELECT b FROM Board b JOIN b.user u ")
  Page<Board> findAllWithUserAndCommentsALIVE_Board(Pageable pageable);


  //제목 검색
  @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.title LIKE %:title% ")
  Page<Board> findAllTitleContaining(@Param("title")String title, Pageable pageable);

  //내용 검색
  @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.content LIKE %:content%")
  Page<Board> findAllContentContaining(@Param("content")String content, Pageable pageable);

  //작성자 검색
  @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.user.email LIKE %:username%")
  Page<Board> findAllWriterNameContaining(@Param("Eamil")String email, Pageable pageable);


  @Query("SELECT b FROM Board b WHERE b.category = :category")
  Page<Board> findAllByCategory(Pageable pageable,@Param("category") Category category);

}
