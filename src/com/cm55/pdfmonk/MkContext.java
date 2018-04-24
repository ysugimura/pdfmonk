package com.cm55.pdfmonk;

import java.util.*;
import java.util.stream.*;

import com.itextpdf.text.pdf.*;

/**
 * PDFの描画コンテキスト
 * <p>
 * 描画メソッドを呼び出すたびに、いちいちフォントやらデフォルトセルやらを指定するのは面倒なため、それらをまとめたオブジェクトを作成し、
 * 変更する必要の無い場合には、できる限り伝播させることができるようにする。例えば、いったん{@link MkDocument}にコンテキストを設定すると、
 * そこから取得される{@link MkCanvas}には同じコンテキストが、さらにそこから{@link MkTemplate}を作成すると、それに同じ
 * コンテキストが用いられる。
 * </p>
 * <p>
 * しかし、ほとんど同じでも少しだけ変更したい場合もある。例えば、
 * </p>
 * <ul>
 * <li>フォントのサイズを少しだけ大きくしたい。
 * <li>デフォルトセルのボーダーを消したい。
 * </ul>
 * <p>
 * などである。この場合には、既に得られている{@link MkContext}の複製を作成し、必要な部分のみを変更して新たなコンテキストとする。
 * このため、コンテキストに格納するオブジェクトは、複製作業用の{@link MkDuplicatable}インターフェースを実装している。
 * </p>
 * @author ysugimura
 */
public class MkContext {

  public static MkContext DEFAULT_CONTEXT = new MkContext();
  static {
    MkBaseFont baseFont = new MkBaseFont(MkFontFace.HEISEI_MINCHO, MkEncoding.UniJIS_UCS2_H);
    DEFAULT_CONTEXT.setBaseFont(baseFont);
    DEFAULT_CONTEXT.setFont(baseFont.createFont(new MkLen(MkUnit.MM, 3))); 
    DEFAULT_CONTEXT.setCell(new MkCell((PdfPCell)null));
  }

  /**
   * デフォルトのコンテキストを取得するが、静的に定義されたデフォルトコンテキストの複製を得る。
   * @return　デフォルトコンテキスト
   */
  public static MkContext getDefault() {
    return DEFAULT_CONTEXT.duplicate();
  }
  
  /** クラス/値のマップ */
  private Map<Class<? extends MkDuplicatable<?>>, MkDuplicatable<?>>map = new HashMap<>();

  /** 内部使用 */
  private MkContext() {}

  /** 内部使用 */
  private MkContext(Map<Class<? extends MkDuplicatable<?>>, MkDuplicatable<?>>map) {
    this.map = map;
  }

  /** 指定クラスの値を取得する */
  @SuppressWarnings("unchecked")
  public <T extends MkDuplicatable<?>>T get(Class<T>clazz) {
    return (T)map.get(clazz);
  }
  
  /** 指定クラスの値を設定する */
  public <T extends MkDuplicatable<?>>MkContext put(Class<T>clazz, T value) {
    map.put(clazz,  value);
    return this;
  }
  
  /** フォントを取得する */
  public MkFont getFont() {
    return get(MkFont.class);
  }

  /** フォントを設定する */
  public MkContext setFont(MkFont font) {
    put(MkFont.class, font);
    return this;
  }

  /** ベースフォントから指定サイズのフォントを生成し、それを設定する */
  public MkFont setFont(MkUnit unit, float size) {
    return this.setFont(new MkLen(unit, size));
  }

  /** ベースフォントから指定サイズのフォントを生成し、それを設定する */
  public MkFont setFont(MkLen size) {
    MkFont font = getBaseFont().createFont(size);
    setFont(font);
    return font;
  }

  /** ベースフォントを取得する */
  public MkBaseFont getBaseFont() {
    return get(MkBaseFont.class);
  }

  /** ベースフォントを設定する */
  public MkContext setBaseFont(MkBaseFont baseFont) {
    put(MkBaseFont.class, baseFont);
    return this;
  }
  
  /** セルを取得する */
  public MkCell getCell() {
    return get(MkCell.class);
  }

  /** セルを設定する */
  public MkContext setCell(MkCell value) {
    put(MkCell.class, value);
    return this;
  }
  
  /** 複製する */
  public MkContext duplicate() {
    Map<Class<? extends MkDuplicatable<?>>, MkDuplicatable<?>>newMap = new HashMap<>();
    this.map.entrySet().stream().forEach(e-> {      
      MkDuplicatable<?> duplicated = (MkDuplicatable<?>)e.getValue().duplicate();
      if (duplicated == null) throw new NullPointerException();
      newMap.put(e.getKey(), duplicated);
    });
    return new MkContext(newMap);
  }

  /** デバッグ用。文字列化する */
  @Override
  public String toString() {
    return map.entrySet().stream()
        .map(e->e.getKey().getSimpleName() + "=" + e.getValue()).collect(Collectors.joining(","));
  }
}
