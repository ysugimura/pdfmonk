package com.cm55.pdfmonk;

import com.itextpdf.text.*;

/**
 * テキスト用セル
 */
public class MkTextCell extends MkCell {
  
  /** 
   * コンテキスト、文字列を指定して作成する。コンテキストのフォントが使用される。
   * @param ctx コンテキスト
   * @param text テキスト
   */
  public MkTextCell(MkContext ctx, String text) {
    super(ctx.getCell().getITextCell());
    MkFont font = ctx.getFont();
    modifyImpl().cell.setPhrase(new Phrase(text, font.getITextFont())); 
  }

  /** 複製のみに使用される。他の用途に使用してはいけない */
  protected MkTextCell(Impl impl) {
    super(impl);
  }
  
  /** 
   * テキストを設定する。テーブルにセルを追加した後でテキストを変更しても、既に追加されたセルには影響が無いことに注意。
   * テーブルに追加する際には、セルのコピーが作られるからである。したがって、
   * <pre>
   * MkTextCell cell = new MkTextCell(ctx, "foo");
   * table.addCell(cell);
   * cell.setText("bar");
   * table.addCell(cell);
   * </pre>
   * <p>
   * とすれば、正しく"foo"と"bar"のセルが作成される。
   * </p>
   * @param text
   * @return
   */
  public MkTextCell setText(String text) {
    Phrase ph = modifyImpl().cell.getPhrase();
    ph.clear();
    ph.add(text);
    return this;
  }

  /** 複製する */
  @Override
  public MkTextCell duplicate() {
    return new MkTextCell(impl);
  }
}
