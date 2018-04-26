package com.cm55.pdfmonk;

import java.util.*;
import java.util.stream.*;

import com.cm55.pdfmonk.*;

/**
 * テンプレートを使用して、後から以前のページに描画する。これは典型的には、ページ番号を振るために用いられる。つまり、
 * <ul>
 * <li>ドキュメント中に複数のページを作成するが、各のページの隅に、「3 ページ / 全 10 ページ」などと描画したい。
 * <li>しかし、例えば3ページ目の描画中には、全部で何ページになるのかわからない。
 * <li>このため、あらかじめ各ページにテンプレートを描画しておき、最終ページ描画終了時に、すべてのテンプレートの描画を行う。
 * </ul>
 * <p>
 * これが可能なのは、テンプレートを置いた時点では、PDFに描画されておらず、それがドキュメントのクローズ直前に行われるからである。
 * 詳細は{@link MkTemplate}を参照のこと。
 * </p>
 * <p>
 * また、この目的のために、{@link MkDocument}には改ページごとにコールバックが行われる仕組みが用意されている。
 * {@link MkDocument#setNewPageCallback(java.util.function.Consumer)}を参照のこと。
 * </p>
 * <p>
 * 典型的な使用例は以下の通り、
 * </p>
 * <pre>
 * MkDocument doc = new MkDocument(....);
 * MkCanvas canvas = doc.getCanvasOver();
 * MkPageNumbering pageNumbering = new MkPageNumbering(canvas, テンプレート位置, テンプレートサイズ);
 * doc.setNewPageCallback(pageNumbering::create);
 * ...
 * ...
 * // クローズ前にすべてのページについて描画
 * pageNumbering.pageSlots().forEach(ps-> {
 * });
 * doc.close();
 * </pre>
 * @author ysugimura
 */
public class MkPageNumbering {

  MkContentByte contentByte;
  MkDimension templatePosition;
  MkDimension templateSize;
  List<PageSlot>pageSlots = new ArrayList<PageSlot>();

  /**
   * テンプレートを置く対象キャンバス、置く位置、テンプレートのサイズを指定する。
   * @param canvas 対象キャンバス
   * @param unit 値の単位
   * @param x 置く位置x
   * @param y 置く位置y
   * @param width　テンプレートの幅
   * @param height テンプレートの高さ
   */
  public MkPageNumbering(MkContentByte contentByte, MkUnit unit, float x, float y, float width, float height) {
    this(contentByte, new MkDimension(unit, x, y), new MkDimension(unit, width, height));
  }

  /**
   * テンプレートを置く対象キャンバス、置く位置、テンプレートのサイズを指定する。
   * @param canvas 対象キャンバス
   * @param templatePosition 
   * @param templateSize
   */
  public MkPageNumbering(MkContentByte contentByte, MkDimension templatePosition, MkDimension templateSize) {
    this.contentByte = contentByte;
    this.templatePosition = templatePosition;
    this.templateSize = templateSize;
  }

  /**
   * ページ番号を指定して作成する
   * @param pageNumber
   */
  public void create(int pageNumber) {
    PageSlot pageSlot = new PageSlot(pageNumber, contentByte.createTemplate(templateSize));
    pageSlots.add(pageSlot);
    contentByte.setTemplate(pageSlot.template, templatePosition);
  }
  
  /** 置かれた{@link PageSlot}をすべて処理するためのストリームを得る */
  public Stream<PageSlot>pageSlots() {
    return pageSlots.stream();
  }
  
  /** 各ページに置くテンプレートと、そのページ番号 */
  public static class PageSlot {
    public final int pageNumber;
    public final MkTemplate template;
    PageSlot(int pageNumber, MkTemplate template) {
      this.pageNumber = pageNumber;
      this.template = template;
    }
  }
}
