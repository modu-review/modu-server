package com.modureview.exception;

import com.modureview.enums.BoardErrorCode;

public class BoardCustomException extends RuntimeException {
  private final BoardErrorCode boardErrorCode;

  public BoardCustomException(BoardErrorCode boardErrorCode) {
    super(boardErrorCode.getMessage());
    this.boardErrorCode = boardErrorCode;
  }

  public BoardErrorCode getBoardErrorCode() {
    return this.boardErrorCode;
  }
  public String getErrorMessage(){
    return boardErrorCode.getMessage();
  }
}
