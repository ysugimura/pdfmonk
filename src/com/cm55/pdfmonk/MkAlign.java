package com.cm55.pdfmonk;

import com.itextpdf.text.*;

/**
 * アラインメント
 */
public enum MkAlign {
  LEFT(Element.ALIGN_LEFT),
  CENTER(Element.ALIGN_CENTER),
  RIGHT(Element.ALIGN_RIGHT),
  JUSTIFIED(Element.ALIGN_JUSTIFIED);
  
  public final int value;
  private MkAlign(int value) {
    this.value = value;
  }
  
  public static MkAlign getByValue(int value) {
    for (MkAlign align: MkAlign.values()) {
      if (align.value == value) return align;
    }
    return null;
  }
}
