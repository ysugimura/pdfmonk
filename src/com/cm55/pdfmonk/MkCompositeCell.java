package com.cm55.pdfmonk;

import com.itextpdf.text.pdf.*;

/**
 * コンポジットセル
 */
public class MkCompositeCell extends MkCell {

  public MkCompositeCell(MkContext ctx) {
    super(ctx.getCell().getITextCell());
  }
    
  protected MkCompositeCell(Impl impl) {
    super(impl);
  }
  
  public MkCompositeCell addElement(MkElement element) {
    PdfPCell cell = modifyImpl().cell;
    element.getElements().forEach(e->cell.addElement(e));
    return this;
  }  
  
  /** 複製する */
  @Override
  public MkCompositeCell duplicate() {
    return new MkCompositeCell(impl);
  }
}
