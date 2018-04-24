package com.cm55.pdfmonk;

/**
 * 例外
 */
public class MkException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MkException(Throwable th) {
    super(th);
  }
  
  public MkException(String message) {
    super(message);
  }
}
