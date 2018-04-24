package com.cm55.pdfmonk;

/**
 * 距離を表す。単位と値が指定される。
 * <p>
 * 本システムで距離を表す値だが、値の単位は固定されていない。
 * {@link MkUnit}による単位とその数値のペアで構成される。
 * </p>
 * @author ysugimura
 */
public class MkLen implements Comparable<MkLen> {
  
  public static final MkLen ZERO = new MkLen(MkUnit.PT, 0);

  /** 単位 */
  public final MkUnit unit;

  /** 値 */
  public final float value;
  
  /** 距離の値を指定する */
  public MkLen(MkUnit unit, float value) {
    this.unit = unit;
    this.value = value;  
  }
  
  /** 別のオブジェクトとの和を表す新たなオブジェクトを返す */
  public  MkLen add(MkLen that) {
    return new MkLen(unit, this.value + adjustValue(that));
  }

  /** 正値を判定 */
  public boolean isPositive() {
    return value > 0;
  }
  
  /** 負値を判定 */
  public boolean isNegative() {
    return value < 0;
  }
  
  /** 別のオブジェクトとの差を表す新たなオブジェクトを返す */
  public MkLen sub(MkLen that) {
    return new MkLen(unit, this.value - adjustValue(that));
  }
  
  /** 別のオブジェクトとの積を表す新たなオブジェクトを返す */
  public  MkLen mul(MkLen that) {
    return new MkLen(unit, this.value * adjustValue(that));
  }
  
  /** 別のオブジェクトとの除を表す新たなオブジェクトを返す */
  public MkLen div(MkLen that) {
    return new MkLen(unit, this.value / adjustValue(that));
  }
  
  /** 任意の長さの値を、本オブジェクトの単位の値に変換する */
  private float adjustValue(MkLen that) {
    return that.unit.valueTo(that.value,  unit);
  }
    
  @Override
  public int compareTo(MkLen that) {
    return (int)Math.signum(this.value - adjustValue(that));
  }

  /** ミリメートル値を得る */
  public float mmValue() {
    return unit.valueTo(value, MkUnit.MM);
  }

  /** ポイント値を得る */
  public float ptValue() {
    return unit.valueTo(value, MkUnit.PT);
  }
  
  /** インチ値を得る */
  public float inValue() {
    return unit.valueTo(value, MkUnit.IN);
  }

  @Override
  public String toString() {
    return unit + " " + value;
  }
}
