package com.cm55.pdfmonk;

import java.text.*;
import java.util.*;

/**
 * 価格フォーマッタ
 * 
 * @author ysugimura
 */
public class MkPriceFormatter {

  /** フォーマッタ */
  private static NumberFormat priceFormatter = NumberFormat.getCurrencyInstance(Locale.JAPAN);

  /**
   * 整数値を価格形式にフォーマットする
   * 
   * @param price
   *          価格を表す整数
   * @return 価格フォーマットされた文字列
   */
  public static String format(int price) {
    return priceFormatter.format(price);
  }

}
