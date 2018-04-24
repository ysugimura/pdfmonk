package com.cm55.pdfmonk;

public class MkPdfPosition {
  public final float x;
  public final float y;
  public MkPdfPosition(float x, float y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return x + "," + y;
  }
}
