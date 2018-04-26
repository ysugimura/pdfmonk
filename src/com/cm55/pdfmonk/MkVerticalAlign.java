package com.cm55.pdfmonk;

import com.itextpdf.text.*;

/**
 * 垂直アラインメント
 */
public enum MkVerticalAlign {
  TOP(Element.ALIGN_TOP),
  MIDDLE(Element.ALIGN_MIDDLE),
  BOTTOM(Element.ALIGN_BOTTOM),
  BASELINE(Element.ALIGN_BASELINE);
  
  public final int value;
  private MkVerticalAlign(int value) {
    this.value = value;
  }
  
  public static MkVerticalAlign getByValue(int value) {
    for (MkVerticalAlign align: MkVerticalAlign.values()) {
      if (align.value == value) return align;
    }
    return null;
  }
}
