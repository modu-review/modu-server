package com.modureview.exception.board;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class BoardIdNotFoundException extends CustomException {
  public BoardIdNotFoundException(Long boardId) {
    super(ErrorCode.BOARD_ID_NOT_FOUND,boardId);
  }

}
