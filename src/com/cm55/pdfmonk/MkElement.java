package com.cm55.pdfmonk;

import java.util.stream.*;

import com.itextpdf.text.*;

/**
 * 要素インターフェース
 * @author ysugimura
 */
public interface MkElement {

  /** 
   * iTextの要素オブジェクトを取得する
   * @return iTextの要素オブジェクト
   */
  public Stream<Element> getElements();
}
