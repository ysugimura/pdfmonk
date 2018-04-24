package com.cm55.pdfmonk;

import java.util.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * ベースフォント
 */
public class MkBaseFont implements MkDuplicatable<MkBaseFont> {

  private static class Impl {
    private int refCount = 1;
    private MkFontFace baseName;
    private MkEncoding encoding;
    private boolean embedded;

    private BaseFont baseFont;
    private EnumSet<MkFontStyle> style;
  }
  
  private Impl impl;
  
  private MkBaseFont(Impl impl) {
    (this.impl = impl).refCount++;    
  }
  
  public MkBaseFont(MkFontFace baseName, MkEncoding encoding) {
    this(baseName, encoding, BaseFont.NOT_EMBEDDED);
  }
  
  public MkBaseFont(MkFontFace baseName, MkEncoding encoding, boolean embedded) {
    this(baseName, encoding, embedded, EnumSet.noneOf(MkFontStyle.class));
  }

  public MkBaseFont(MkFontFace baseName, MkEncoding encoding, boolean embedded, EnumSet<MkFontStyle> style) {
    impl = new Impl();
    
    impl.baseName = baseName;
    impl.encoding = encoding;
    impl.embedded = embedded;
    impl.style = style;

    String name = baseName.desc;
    switch (MkFontStyle.getValue(style)) {
    case Font.BOLD:
      name += ",Bold";
      break;
    case Font.ITALIC:
      name += ",Italic";
      break;
    case Font.BOLDITALIC:
      name += ",BoldItalic";
      break;
    }

    try {
      // String name, String encoding, boolean embedded
      impl.baseFont = BaseFont.createFont(name, encoding.desc, embedded);
    } catch (Exception ex) {
      throw new MkException(ex);
    }
  }

  public MkFont createFont(MkUnit unit, float size) {
    return this.createFont(new MkLen(unit, size));
  }
  
  /** 
   * このベースフォントを元に、サイズを指定してフォントを作成する
   * @param size
   * @param color
   * @return
   */
  public MkFont createFont(MkLen size) {
    return new MkFont(this.duplicate(), size.ptValue());
  }
  
  /**
   * スタイル付のベースフォントを取得する。これは内部で使用される。
   * <p>
   * 本来、{@link MkFont}つまり{@link Font}はスタイル付で作成されるのだが、{@link PdfContentByte}
   * に描画する場合には、{@link Font}は使用されず、{@link BaseFont}が使用されてしまう。
   * つまり、{@link BaseFont}自体がスタイル付でないとBOLD/ITALICが正しく描画されない。
   * </p>
   * <p>
   * これでは面倒なので、スタイル付の{@link MkFont}から{@link BaseFont}を取得する際に、
   * フォントがスタイル付であれば、スタイル付きのベースフォントにすり替える。
   * </p>
   * 
   * @param style
   *          スタイル
   * @return
   */
  public MkBaseFont getStyled(EnumSet<MkFontStyle> style) {
    if (impl.style.equals(style))
      return this;
    return new MkBaseFont(impl.baseName, impl.encoding, impl.embedded, style);
  }

  /** {@inheritDoc} */

  public BaseFont getITextBaseFont() {
    return impl.baseFont;
  }

  @Override
  public MkBaseFont duplicate() {
    return new MkBaseFont(impl);
    
  }
}
