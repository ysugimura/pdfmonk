package com.cm55.pdfmonk;

import java.util.*;
import java.util.stream.*;

import com.itextpdf.text.*;

/**
 * パラグラフ
 * 
 * @author ysugimura
 *
 */
public class MkParagraph implements MkElement, MkSpacing<MkParagraph> {

  private Paragraph p;
  private MkContext ctx;
  
  
  public MkParagraph(MkContext ctx, String text) {
    p = new Paragraph(text, ctx.getFont().getITextFont());
  }

  /** アラインメントを指定する */
  public MkParagraph setAlign(MkAlign align) {
    p.setAlignment(align.value);
    return this;
  }

  /** 前スペーシングを指定する */
  @Override
  public MkParagraph setSpacingBefore(MkLen mm) {
    p.setSpacingBefore(mm.ptValue());
    return this;
  }

  /** 後スペーシングを指定する */
  @Override
  public MkParagraph setSpacingAfter(MkLen mm) {
    p.setSpacingAfter(mm.ptValue());
    return this;
  }

  @Override
  public Stream<Element> getElements() {
    return Arrays.stream(new Element[] { p });
  }


  public MkParagraph alignCenter() {
    setAlign(MkAlign.CENTER);
    return this;
  }

  public MkParagraph alignRight() {
    setAlign(MkAlign.RIGHT);
    return this;
  }
}
