package com.cm55.pdfmonk;

/**
 * ページの幅・高さと四隅の印刷不能領域のマージンを表す。
 * @author ysugimura
 */
public class MkGeometry {
  
  /** ページ全体大きさ */
  public final MkDimension size;
  
  /** 印刷不可領域マージン */
  public final MkInsets margins;
  
  /** 
   * ページ全体のサイズと、印刷不可領域としてのマージンを指定する
   * @param size　ページ全体サイズ
   * @param margins 印刷不可領域としてのマージン
   */
  public MkGeometry(MkDimension size, MkInsets margins) {
    this.size = size;
    this.margins = margins;
  }
  
  /** ページ全体サイズを取得 */
  public MkDimension getPaperSize() {
    return size;
  }

  /** 印刷領域左上位置を取得する */
  public MkDimension topLeftMargin() {
    return margins.topLeft();
  }
  
  /** 印刷領域幅を取得する */
  public MkLen getPrintWidth() {
    return size.x.sub(margins.left).sub(margins.right);
  }

  /** 印刷領域高さを取得する */
  public MkLen getPrintHeight() {
    return size.y.sub(margins.top).sub(margins.bottom);
  }

  /** 印刷領域大きさを取得する */
  public MkDimension getPrintSize() {
    return new MkDimension(MkUnit.MM, getPrintWidth().mmValue(), getPrintHeight().mmValue());
  }
  
  /** 印刷領域左下座標を取得する */
  public MkDimension getBottomLeft() {
    return new MkDimension(MkLen.ZERO, getPrintHeight());
  }

  /** 印刷領域右上座標を取得する */
  public MkDimension getTopRight() {
    return new MkDimension(getPrintWidth(), MkLen.ZERO);
  }
  
  /** デバッグ用出力 */
  @Override
  public String toString() {
    return "size:" + size + ",insets:" + margins;
  }
    
  /** 
   * 引数で指定された位置のPDF上座標を取得する。
   * <p>
   * このシステムでは、ページの(leftMargin, TopMargin)位置を原点としており、右下方向に座標が広がるが、
   * PDFのシステムでは、ページの左下位置、このシステムで言えば、(0, pageWidth)の位置を原点とし、右上方向に座標が広がっている。
   * </p>
   * <p>
   * このメソッドは、本システム上の座標をPDF座標に変換する。
   * </p>
   */
  public MkPdfPosition toPdfPosition(MkDimension position) {
    return toPdfPosition(position.x, position.y);
  }
  
  /** 
   * 引数で指定された位置のPDF上座標を取得する。
   * {@link #toPdfPosition(MkDimension)}を参照のこと
   */
  public MkPdfPosition toPdfPosition(MkLen x, MkLen y) {
    
    // 本システム上の座標
    MkDimension position = new MkDimension(x, y);
    
    // 本システムの印刷領域サイズ
    MkLen contentHeight = getPrintHeight();
    
    // PDF上の座標を返す
    return new MkPdfPosition(
        // x位置は左マージンを加えたもの
        position.x.ptValue() + margins.left.ptValue(),         
        // y位置は中身サイズから指定されたy位置を減算し、ボトムマージンを加えたもの
        contentHeight.ptValue() - position.y.ptValue() + margins.bottom.ptValue()
    );
  }

  /**
   * 引数で指定されたPDF上の座標を本システムの座標に変換する
   * @param position PDF上の座標
   * @return　本システム上の座標
   */
  public MkDimension fromPdfPosition(MkPdfPosition position) { 
    return fromPdfPosition(position.x, position.y);
  }

  /**
   * 引数で指定されたPDF上の座標を本システムの座標に変換する
   * @param x PDF上のx座標（ポイント）
   * @param y PDF上のy座標（ポイント）
   * @return　本システム上の座標
   */
  public MkDimension fromPdfPosition(float x, float y) {
    return new MkDimension(MkUnit.PT,
        // x位置は単純にマージン分を差し引く
        x - margins.left.ptValue(),
        
        // y位置、ページ全体高さからPDF位置を減算し、さらにトップマージンを減算する。
        size.y.ptValue() - y - margins.top.ptValue()
    ); 
  }

  /**
   * A4ポートレイト
   */
  public static MkGeometry A4_PORTRAIT =
    new MkGeometry(
      new MkDimension(MkUnit.MM,
      210, // width
      297  // height
      ),
      new MkInsets(MkUnit.MM, 
      20, 
      25, 
      25, 
      15
      )
    );

  /**
   * A4ランドスケープ
   */
  public static MkGeometry A4_LANDSCAPE =
    new MkGeometry(
        new MkDimension(MkUnit.MM, 
      297,  // width
      210 // height
      ),
      new MkInsets(MkUnit.MM, 
      25, 
      20, 
      20, 
      20)
  );
}
