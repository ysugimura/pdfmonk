package com.cm55.pdfmonk;

/**
 * iTextで使用可能な日本語フォント一覧
 */
public class MkFontFace {
  
  /** 小塚明朝 */
  public static MkFontFace KOZUKA_MINCHO = new MkFontFace("KozMinPro-Regular");

  /**　平成明朝 */
  public static MkFontFace HEISEI_MINCHO = new MkFontFace("HeiseiMin-W3");

  /** 平成角ゴシック */
  public static MkFontFace HEISEI_KAKU_GOTHIC = new MkFontFace("HeiseiKakuGo-W5");

  /** フォント種類 */
  public final String desc;
  
  private MkFontFace(String desc) {
    this.desc = desc;
  }
}
