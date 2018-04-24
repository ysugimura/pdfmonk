package com.cm55.pdfmonk;

import java.util.*;
import java.util.stream.*;

import com.itextpdf.text.*;

public class MkPhrase implements MkElement {

  /** iTextのフレーズオブジェクト */
  private Phrase phrase;

  /**
   * テキストとフォントを指定して作成する
   * 
   * @param text テキスト
   * @param font フォント
   */
  public MkPhrase(MkContext ctx, String text) {
    phrase = new Phrase(text, ctx.getFont().getITextFont());
  }

  /** iTextの要素を取得する */
  @Override
  public Stream<Element> getElements() {
    return Arrays.stream(new Element[] { phrase });
  }

}
