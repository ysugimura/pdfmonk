package com.cm55.pdfmonk;

/**
 * 複製可能オブジェクトが実装すべきインターフェース
 * <p>
 * Java標準のCloneableはあえて使用しない。
 * </p>
 * @author ysugimura
 *
 * @param <T>
 */
public interface MkDuplicatable<T> {

  /** 複製して同じオブジェクトを得る */
  public T duplicate();
}
