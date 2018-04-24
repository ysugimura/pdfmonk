package com.cm55.pdfmonk;

import com.itextpdf.text.pdf.*;

/**
 * キャンバス
 * <p>
 * {@link MkCanvas}は{@link MkcontentByte}とほぼ同じで、描画先のバッファとしての性質を持つ。
 * </p>
 * <p>
 * 同じ{@link MkContentByte}から派生した{@link MkTemplate}と異なる点としては、{@link MkCanvas}が{@link Document}
 * のレイヤーの一つを表している点である。単なるバッファとは異なり{@link MkCanvas}は複数ページを持つドキュメントの現在のページを表しているため、
 * キャンバスに描画されているものが溢れた場合には改ページ操作が可能になる。
 * </p>
 * <p>
 * この改ページの判断のために、現在のy位置を保持設定する機能がある。
 * </p>
 * @author ysugimura
 */
public class MkCanvas extends MkContentByte {

  /** 
   * このキャンバスが所属する{@link MkDocument}。
   * 改ページ操作に用いられる。
   */
  private final MkDocument document;

  /** 連続出力の場合の現在y位置 */
  private MkLen vertical = MkLen.ZERO;
  
  /** PDFコンテントバイト、ドキュメントを指定して作成する */
  public MkCanvas(PdfContentByte pcb, MkDocument document) {
    super(document.getContext(), pcb, document.getGeometry());
    this.document = document;
  }
  
  /** 現在のページ番号を取得する */
  public int getPageNumber() {
    return document.getPageNumber();
  }
  
  /** 現在y位置を取得する */
  public MkLen getVertical() {
    return vertical;
  }
  
  /** 現在のy位置を設定する */
  public MkCanvas setVertical(MkUnit unit, float value) {
    return setVertical(new MkLen(unit, value));
  }
  
  /** 現在y位置を設定する  */
  public MkCanvas setVertical(MkLen value) {
    this.vertical = value;
    return this;
  }

  /**
   * 現在のy位置を進める。印字領域をはみ出す場合は改ページして次ページトップに位置づける。
   * @param unit 単位
   * @param value 値
   * @return　新たなy位置
   */
  public MkLen advance(MkUnit unit, float value) {
    return advance(new MkLen(unit, value));
  }

  /**
   * 現在のy位置を進める。印字領域をはみ出す場合は改ページして次ページトップに位置づける。
   * @param add 進める値
   * @return 新たなy位置
   */
  public MkLen advance(MkLen add) {
    vertical = vertical.add(add);
    if (vertical.compareTo(getGeometry().getPrintHeight()) > 0) {
      newPage();
    }
    return vertical;
  }

  /**
   * キャンバスのジオメトリを取得する
   */
  public MkGeometry getGeometry() {
    return document.getGeometry();
  }
  
  /** ページ送りをし、現在のy位置を0にする */
  public void newPage() {
    document.newPage();
    vertical = MkLen.ZERO;
  }
  
  /** 
   * 指定された高さの要素を描画可能かを調べる
   * @param unit 単位
   * @param value 高さの値
   * @return true:描画可能、false:不可能
   */
  public boolean canDraw(MkUnit unit, float value)  {
    return canDraw(new MkLen(unit,  value));
  }
  
  /** 
   * 指定された高さの要素を描画可能かを調べる
   * @param height 高さの値
   * @return true:描画可能、false:不可能
   */
  public boolean canDraw(MkLen height) {
    return document.getGeometry().getPrintHeight().sub(vertical).sub(height).isPositive();
  }
}

