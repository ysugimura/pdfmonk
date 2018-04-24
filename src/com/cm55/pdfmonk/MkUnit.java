package com.cm55.pdfmonk;

import static com.cm55.pdfmonk.MkConsts.*;

/**
 * 本システムで使用する何らかの「値」の単位。
 * <p>
 * 本来のPDF、およびitextライブラリでは、値はすべて「ポイント」の単位になっているのだが、これでは使いにくいため、任意の単位が使用できるようにしてある。
 * 日本人であれば、おそらくはミリメートルを採用するのが楽と思われるし、米国人であればインチかもしれない。
 * しかし、そのためには、このライブラリ全体に渡り、明示的に単位を指定しなければならない。その単位の種類をここで定義しており、
 * それらの単位の値の相互変換を提供する。
 * </p>
 * @author ysugimura
 */
public enum MkUnit   {
  /** ミリメートル */
  MM {
    @Override
    public float scalingTo(MkUnit unit) {
      switch (unit) {
      case IN: return INCH_PER_MM;
      case PT: return POINT_PER_MM;
      case MM: return 1;
      default: throw new IllegalArgumentException();
      }
    }
  },
  /** インチ */
  IN {
    @Override
    public float scalingTo(MkUnit unit) {
      switch (unit) {
      case IN: return 1;
      case PT: return POINT_PER_INCH;
      case MM: return MM_PER_INCH;
      default: throw new IllegalArgumentException();
      }
    }
  },
  /** ポイント */
  PT {
    @Override
    public float scalingTo(MkUnit unit) {
      switch (unit) {
      case IN: return INCH_PER_POINT;
      case PT: return 1;
      case MM: return MM_PER_POINT;
      default: throw new IllegalArgumentException();
      }
    }
  };

  /** 
   * この単位の値を指定された単位に変換するためのスケーリング値
   * @param unit 変換先単位
   * @return　スケーリング値
   */
  public abstract float scalingTo(MkUnit unit);
  
  /** 
   * この単位の値を指定された単位に変換する
   * @param value この単位の値
   * @param unit 変換先単位
   * @return 変換先単位の値
   */
  public float valueTo(float value, MkUnit unit) {
    return scalingTo(unit) * value;
  }
}
