package com.cm55.pdfmonk;

import java.util.*;

import com.itextpdf.text.*;

/**
 * フォント情報
 */
public class MkFont implements MkDuplicatable<MkFont> {

  private static class Impl {
    private int refCount = 1;
    
    private MkBaseFont baseFont;
  
    /** iTextフォント */
    private Font font;
    
    private Impl(MkBaseFont baseFont, Font font) {
      this.baseFont = baseFont;
      this.font = font;
    }
  }
  
  private Impl impl;
  
  public MkFont(MkBaseFont baseFont, float size) {
    impl = new Impl(baseFont, new Font(baseFont.getITextBaseFont(), size));
  }
  
  public MkFont(MkBaseFont baseFont, Font font) {
    impl = new Impl(baseFont, font);
  }

  private MkFont(Impl impl) {
    impl.refCount++;
    this.impl = impl;
  }
  
  private Impl modifyImpl() {
    if (impl.refCount > 1) {
      impl.refCount--;
      impl = new Impl(impl.baseFont, new Font(impl.font));
    }
    return impl;
  }
  
  public MkFont setSize(MkUnit unit, float size) {
    return this.setSize(new MkLen(unit, size));
  }
  
  /** サイズを設定する */
  public MkFont setSize(MkLen size) {
    modifyImpl().font.setSize(size.ptValue());
    return this;
  }

  /** サイズを取得する */
  public MkLen getSize() {
    return new MkLen(MkUnit.PT, impl.font.getSize());
  }
  
  /** スタイルを設定する */
  public MkFont setStyle(EnumSet<MkFontStyle>style) {
    modifyImpl().font.setStyle(MkFontStyle.getValue(style));
    return this;
  }

  /** スタイルを取得する */
  public EnumSet<MkFontStyle> getStyle() {
    int value = impl.font.getStyle();
    return MkFontStyle.fromValue(value);
  }
  
  /** フォントカラーを指定する */
  public MkFont setColor(MkColor color) {
    modifyImpl().font.setColor(color.getBaseColor());
    return this;
  }
  
  /** フォントカラーを取得する　*/
  public MkColor getColor() {
    return new MkColor(impl.font.getColor());
  }

  /** 
   * 指定されたテキストの描画幅を取得する 
   * @param text
   * @return
   */
  public MkLen getStringWidth(String text) {
    return new MkLen(MkUnit.PT, impl.font.getBaseFont().getWidthPoint(text, impl.font.getSize()));
  }

  /** iTextのFontを取得する */
  public Font getITextFont() {
    return impl.font;
  }

  public MkBaseFont getBaseFont() {
    return impl.baseFont;
  }
  
  @Override
  public MkFont duplicate() {
    return new MkFont(impl);
  }
}
