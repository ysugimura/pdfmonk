package com.cm55.pdfmonk;
import com.itextpdf.text.*;

/**
 * ボーダーの指定
 * @author ysugimura
 */
public enum MkBorder {
  LEFT(Rectangle.LEFT),
  RIGHT(Rectangle.RIGHT),
  TOP(Rectangle.TOP),
  BOTTOM(Rectangle.BOTTOM);
  
  public final int value;
  private MkBorder(int value) {
    this.value = value;
  }
}
