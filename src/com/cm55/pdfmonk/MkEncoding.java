package com.cm55.pdfmonk;

/**
 * エンコーディング
 * 
 * iTextで使用可能なエンコーディングの一覧
 */
public class MkEncoding {
  
  /** Adobe日本語文字のUniCode用エンコーディング。横書き */
  public static final MkEncoding UniJIS_UCS2_H = new MkEncoding("UniJIS-UCS2-H");
  
  /** UniJIS-UCS2-Hの縦書きエンコーディング。これは縦書きになる */
  public static final MkEncoding UniJIS_UCS2_V = new MkEncoding("UniJIS-UCS2-V");
  
  /** UniJIS-UCS2-Hのうち、プロポーショナル文字のみ半角文字に変更したエンコーディング。横書きだが、文字幅計算がうまく行かない模様 */
  public static final MkEncoding UniJIS_UCS2_HW_H = new MkEncoding("UniJIS-UCS2-HW-H");
  
  /** UniJIS-UCS2-HW-Hの縦書きエンコーディング。これは縦書きになる。 */
  public static final MkEncoding UniJIS_UCS2_HW_V = new MkEncoding("UniJIS-UCS2-HW-V");

  /** エンコーディング文字列 */
  public final String desc;
  
  private MkEncoding(String desc) {
    this.desc = desc;
  }
}
