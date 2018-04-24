package com.cm55.pdfmonk;

import com.itextpdf.text.pdf.*;

/**
 * ある要素を描画するときに、それが与えられた領域、例えばページの未描画部分に入り切るかどうかわからない場合がある。
 * この入り切る・入り切らないを検出するのが{@link MkColumnText}の役割である。つまり、以下のようなことができる。
 * <ul>
 * <li>描画する要素と描画領域を{@link MkColumnText}に与えておき、実際に描画させてみる。入り切らなかった場合にはその旨の結果が通知される。
 * <li>入り切らなかった場合、例えば改ページ等を行に、新たに描画領域を設定し、描画させてみる。このようにして、入り切るまで次々に改ページしていく。
 * <li>最終的に入りきった場合には、その終了位置を取得できる。これは、新たなページの中ほどまでかもしれない。そのy位置を取得する。
 * </ul>
 * <p>
 * その他、入りきってしまった場合には、更に描画要素を追加する、入り切るかどうかのシュミレーションのみを行うこともできる。 
 * </p>
 * <p>
 * ※なお、{@link MkColumnText}は{@link MkContentByte}を対象として描画できるが、前述での「改ページ」は、その下位の
 * {@link MkCanvas}でしかできないことに注意。
 * </p>
 * <p>
 * 典型的な使用方法は以下の通り（ただし、キャンバスを対象とする）。
 * </p>
 * <pre>
 * MkCanvas canvas = ...
 * MkColumnText ct = new MkcolumnText(canvas);
 * ct.add
 * </pre>
 * @author ysugimura
 */
public class MkColumnText {

  private static final boolean DEBUG = false;

  private final MkGeometry geometry;
  private final ColumnText columnText;

  public MkColumnText(MkContentByte contentByte) {
    this.geometry = contentByte.getGeometry();
    columnText = new ColumnText(contentByte.getITextContentByte());
  }

  /** 描画要素を追加する */
  public void addElement(MkElement element) {

    element.getElements().forEach(e -> columnText.addElement(e));
  }

  /**
   * 描画領域を提供して描画させる。すべての要素が描画仕切れた場合にはtrueを返す。
   * @param rect 描画させる猟奇
   * @return true:描画しきれた、false:描画しきれなかった
   */
  public boolean provideArea(MkRect rect) {

    // 左下のポイント座標を取得
    MkPdfPosition bl = geometry.toPdfPosition(rect.bottomLeft());

    // 右上のポイント座標を取得
    MkPdfPosition tr = geometry.toPdfPosition(rect.topRight());

    // 左下・右上のポイント座標を設定
    columnText.setSimpleColumn(bl.x, bl.y, tr.x, tr.y);
    
    if (DEBUG) {
      System.out.println("getYLine " + columnText.getYLine());
    }

    try {
      int r = columnText.go();
      if (DEBUG)
        System.out.println("go " + r);
      return r != ColumnText.NO_MORE_COLUMN;
    } catch (Exception ex) {
      throw new MkException(ex);
    }    
  }
  
  /** YLineを取得する */
  public MkLen getYLine() {
    return geometry.fromPdfPosition(0, columnText.getYLine()).y;
  }
}
