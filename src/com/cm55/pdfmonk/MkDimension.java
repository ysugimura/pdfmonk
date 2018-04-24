package com.cm55.pdfmonk;

/**
 * 二次元座標、もしくは幅・高さのペアを表す。
 * @author ysugimura
 */
public class MkDimension {

  public static final MkDimension ZERO = new MkDimension(MkUnit.PT, 0, 0);
  
  /** x座標あるいは幅 */
  public final MkLen x;
  
  /** y座標あるいは高さ */
  public final MkLen y;

  /** 単位とその値を指定する */
  public MkDimension(MkUnit unit, float x, float y) {
    this.x = new MkLen(unit, x);
    this.y = new MkLen(unit, y);
  }

  /** 二つの値を指定する */
  public MkDimension(MkLen x, MkLen y) {
    this.x = x;
    this.y = y;
  }
  
  /** 加算する */
  public MkDimension add(MkDimension that) {
    return new MkDimension(this.x.add(that.x), this.y.add(that.y));
  }
  
  /**
   * 文字列化する。デバッグ用
   */
  @Override
  public String toString() {
    return "(" + x.value + ", " + y.value + ")";
  }
}
