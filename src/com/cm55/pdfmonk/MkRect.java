package com.cm55.pdfmonk;

/**
 * 二次元座標上の矩形領域を表すオブジェクト。左上点と幅・高さが指定される。
 * @author ysugimura
 */
public class MkRect {

  /** x位置 */
  public final MkLen x;
  
  /** y位置 */
  public final MkLen y;
  
  /** 幅 */
  public final MkLen width;
  
  /** 高さ */
  public final MkLen height;

  /**
   * 単位、位置、サイズを指定する
   * @param unit
   * @param x
   * @param y
   * @param width
   * @param height
   */
  public MkRect(MkUnit unit, float x, float y, float width, float height) {
    this.x = new MkLen(unit, x);
    this.y = new MkLen(unit, y);
    this.width = new MkLen(unit, width);
    this.height = new MkLen(unit, height);
  }
  
  /**
   * 位置とサイズを指定する
   * @param x
   * @param y
   * @param width
   * @param height
   */
  public MkRect(MkLen x, MkLen y, MkLen width, MkLen height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * 位置オブジェクトとサイズオブジェクトを指定する
   * @param origin
   * @param size
   */
  public MkRect(MkDimension origin, MkDimension size) {
    this.x = origin.x;
    this.y = origin.y;
    this.width = size.x;
    this.height = size.y;
  }
  
  /** 左上点を取得する */
  public MkDimension topLeft() { return new MkDimension(x, y); }
  
  /** 左下点を取得する */
  public MkDimension bottomLeft() { return new MkDimension(x, y.add(height)); }
  
  /** 右上点を取得する */
  public MkDimension topRight() { return new MkDimension(x.add(width), y); }
  
  /** サイズオブジェクトを取得する */
  public MkDimension size() { return new MkDimension(width, height); }

  /** 左側座標を取得する */
  public MkLen left() { return x; }
  
  /** 右側座標を取得する */
  public MkLen right() { return x.add(width); }
  
  /** 上側座標を取得する */
  public MkLen top() { return y; }
  
  /** 下側座標を取得する */
  public MkLen bottom() { return y.add(height); }
  

  /**
   * 文字列化。デバッグ用
   */
  @Override
  public String toString() {
    return "x:" + x + ",y:" + y + ",w:" + width + ",h:" + height;
  }
}
