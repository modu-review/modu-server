package com.modureview.Service;

import com.modureview.Dto.Board.Request.DeleteBoardRequestDto;
import com.modureview.Dto.Board.Request.SearchBoardDataRequestDto;
import com.modureview.Dto.Board.Request.WriteBoardRequestDto;
import com.modureview.Dto.Board.Response.DeleteBoardResponseDto;
import com.modureview.Dto.Board.Response.ListBoardResponseDto;
import com.modureview.Dto.Board.Response.WriteBoardResponseDto;
import com.modureview.Entity.Board;
import com.modureview.Entity.User;
import com.modureview.Repository.BoardRepository;
import com.modureview.Service.Utill.CustomUserDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
  private final BoardRepository boardRepository;

  private final UserService userService;




  // ---- 게시글 등록 ---- //
  //UserEntity를 CustomUserDetails로 바꿀생각 @AuthenticationPrincipal ==> 어노테이션 사용 (사용하네)
  public WriteBoardResponseDto write_Board(WriteBoardRequestDto dto, CustomUserDetails user) {

    User userEntity = userService.getUserByEmail(user.getUserEmail());
    Board board = WriteBoardRequestDto.toEntity(dto,userEntity);
    //CustomUserDetails로 했을때는 UserService ->findByUUID해서 찾아서 Entity를 바로 집어넣으면 될듯
    //galleryBoard.setMappingUserEntity(user);
    //@AuthenticationPricipal에서 바로 Uuid걷어와서 Service에서 getUserByUUID 로 UserEntity진행
    //UserService에서 null확인으로 Optional 에서 빠져나오고 UserEntity로 확정납니다.

    Board saveBoard = boardRepository.save(board);
    return WriteBoardResponseDto.fromEntity(saveBoard);
  }
  // ---- 게시글 등록 ---- //
  // ---- 게시글 검색,isEmpty() =="" ---- //

  //TODO : 파일세팅시 확인
  public Page<ListBoardResponseDto> search_Board(SearchBoardDataRequestDto dto, Pageable pageable) {
    Page<Board> result = null;
    if(!dto.getTitle().isEmpty()){
      result = boardRepository.findAllTitleContaining(dto.getTitle(),pageable);
    }else if(!dto.getContent().isEmpty()){
      result = boardRepository.findAllContentContaining(dto.getContent(),pageable);
    }else if(!dto.getWriterName().isEmpty()){
      result = boardRepository.findAllWriterNameContaining(dto.getWriterName(),pageable);
    }
    List<ListBoardResponseDto> list = result.getContent().stream()
        .map(ListBoardResponseDto::fromEntity)
        .collect(Collectors.toList());
    return new PageImpl<>(list,pageable, result.getTotalElements());
  }
  /*
  // ---- 게시글 검색,isEmpty() =="" ---- //
  // ---- 게시글 상세보기 ---- //
  public DetailBoardResponseDto detail_Board(Long boardId) {
    Board findBoard = checkExistGalleryBoard(boardId);
    findBoard.upViewCount();
    return DetailBoardResponseDto.fromEntity(findBoard);
  }*/

  // ---- 게시글 상세보기 ---- //
  // ---- 게시글 수정 ---- //


/*

  //TODO : 파일세팅시 확인
  //== 여기도 동일시하게 @AuthenticationPrincipal 그거 인증 받아서 CustomUserDetail받아서 본인 맞는지 확인 이후에 수정 가능하게 하는게 맞는듯
  public DetailBoardResponseDto update_Board(Long boardId, UpdateBoardRequestDto dto,CustomUserDetails user) {
    //일단 존재하는 GalleryBoard 인가 확인
    Board updateGalleryBoard = checkExistGalleryBoard(boardId);
    //그리고 GalleryBoard 작성자가 지금 로그인 한 (인증을 받은 사람과 맞는가?)
    */
/** if(updateGalleryBoard.getUser()!= userService.getUserByUUID(user.getUuid())){
     throw new CustomLogicException(ExceptionCode.USER_NOT_MATCH_WRITER);
     }
     *
     *
     * 친절하고 자상한 CHAT GPT의 왜 고쳤는가
     *
     * JAVA에서 객체를 비교할 때 객체의 메모리 참조를 비교하는 경우 , 두 객체가 실제로 같은 데이터를 가지더라도 ,
     * 서로 다른 메모리 주소에 위치하면 다른 객체로 인식 될 수 있기 때문에 같은 데이터라도 다른 위치에 있으면 false를 반환하게 된다.
     *
     * 위 문제를 해결하려면 객체 자체보다는 고유ID를 통한 비교가 안전하다.
     *
     * 또한, JAVA는 객체의 동일성 비교를 위해 equals() 메서드만을 지원한다.
     * - == : 객체의 메모리 주소를 비교하여 동일 객체인지 확인한다.
     * - equals() : 객체의 내용이 같은지를 비교합니다. JAVA에서 객체의 내용을 비교하고자 할 때는 equals()메서드를 사용하자
     *
     *
     *      if(!updateGalleryBoard.getUser().equals(userService.getUserByUUID(user.getUuid()))){
     *             throw new CustomLogicException(ExceptionCode.USER_NOT_MATCH_WRITER);
     *         } 둘중 하나를 쓰라는데 밑에 ID를 비교하는게 성능상 더 좋대요
     *//*

    if (!updateGalleryBoard.getUser().getEmail().equals(user.getUserEmail())) {
      //throw new CustomLogicException(ExceptionCode.USER_NOT_MATCH_WRITER);
      throw new RuntimeException("GalleryBoard not found : " + boardId);
    }
    Board soon_updateBoard = dto.toEntity(updateGalleryBoard);
    Board updated_Board = boardRepository.save(soon_updateBoard);

    return DetailBoardResponseDto.fromEntity(updated_Board);
  }
  // ---- 게시글 수정 ---- //
  // ---- 게시글 삭제 ---- //
  //== 여기도 동일시하게 @AuthenticationPrincipal 그거 인증 받아서 CustomUserDetail받아서 본인 맞는지 확인 이후에 수정 가능하게 하는게 맞는듯
  //? 어짜피 프론트에서 인증할껀가? 두번인증하면 이득인가 아닌가? 그정도의 보안은 필요가 없나 있나?
*/


  //TODO : 파일도 같이 지워야함.
  public Long delete_Board(Long boardId, CustomUserDetails user) {

    // 게시글이 존재하는지 확인
    Board findBoard = checkExistBoard(boardId);

    if (findBoard == null) {
      //throw new CustomLogicException(ExceptionCode.RESOURCE_NOT_FOUND, "GalleryBoard", "GalleryBoard Id", String.valueOf(GalleryBoardId));
      throw new RuntimeException("GalleryBoard not found : " + boardId);
    }

    // 작성자가 맞는지 확인
    if (!findBoard.getUser().getEmail().equals(user.getUserEmail())) {
      //throw new CustomLogicException(ExceptionCode.USER_NOT_MATCH_WRITER);
      throw new RuntimeException("NOT_MATHCH_WRITER_USER_EMAIL : " + user.getUserEmail()+boardId);

    }

    // 상태가 DELETE로 설정된 엔티티 저장
    //Board savedBoard = boardRepository.save(dto.toEntity(findBoard));
/*
    if (savedBoard == null) {
      //throw new CustomLogicException(ExceptionCode.SAVE_GALLERY_BOARD_FAILED, "GalleryBoard", "Unable to save the deleted state");
      throw new RuntimeException("GalleryBoard not status == DELETE : " + savedBoard.getStatus());
    }*/

    return boardId;
  }



  // ---- 게시글 삭제 ---- //
  // ---- 페이징 리스트 ---- //

 /* public Page<ListBoardResponseDto> getAll_Board(Pageable pageable) {
    Page<Board> galleryBoards = boardRepository.findAllWithUserAndComments(pageable);
    List<ListBoardResponseDto> list = galleryBoards.getContent().stream()
        .map(ListBoardResponseDto::fromEntity)
        .collect(Collectors.toList());
    return new PageImpl<>(list, pageable, galleryBoards.getTotalElements());
  }

*/

  public Page<ListBoardResponseDto> get_ALIVE_Board(Pageable pageable) {
    Page<Board> galleryBoards_ALIVE = boardRepository.findAllWithUserAndCommentsALIVE_Board(pageable);
    List<ListBoardResponseDto> list_ALIVE = galleryBoards_ALIVE.getContent().stream()
        .map(ListBoardResponseDto::fromEntity)
        .collect(Collectors.toList());
    return new PageImpl<>(list_ALIVE,pageable,galleryBoards_ALIVE.getTotalElements());
  }

  public Page<ListBoardResponseDto> get_AllBoard(Pageable pageable) {
    Page<Board> galleryBoards_ALIVE = boardRepository.findAllWithUserAndCommentsALIVE_Board(pageable);
    List<ListBoardResponseDto> list_ALIVE = galleryBoards_ALIVE.getContent().stream()
        .map(ListBoardResponseDto::fromEntity)
        .collect(Collectors.toList());
    return new PageImpl<>(list_ALIVE,pageable,galleryBoards_ALIVE.getTotalElements());
  }

  public Page<ListBoardResponseDto> get_All_Board_Category(Pageable pageable,String category) {
    Page<Board> galleryBoards_ALIVE = boardRepository.findAllWithUserAndCommentsALIVE_Board(pageable);
    List<ListBoardResponseDto> list_ALIVE = galleryBoards_ALIVE.getContent().stream()
        .map(ListBoardResponseDto::fromEntity)
        .collect(Collectors.toList());
    return new PageImpl<>(list_ALIVE,pageable,galleryBoards_ALIVE.getTotalElements());
  }

  // ---- 페이징 리스트 ---- //
  public Board checkExistBoard(Long boardId) {
    return boardRepository.findByIdWithUserAndCommentsAndFiles(boardId)
        .orElseThrow(() -> new RuntimeException("Board not found with id: " + boardId));
  }
}



