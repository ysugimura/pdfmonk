package com.cm55.pdfmonk;

import java.util.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * テーブルセル
 * <p>
 * itextの{@link PdfPCell}をラップしたもの
 * </p>
 */
public class MkCell implements MkDuplicatable<MkCell> {

  /**
   * セルは何度も複製されるが、セルに対する変更操作の無い場合には
   * 同じものが使い回される。参照カウントを使って、いくつの{@link MkCell}から参照されているかを保持する。
   * @author ysugimura
   */
  protected static class Impl {
    private int refCount = 1;
    protected PdfPCell cell;
    private Impl(PdfPCell cell) {
      this.cell = cell;
    }
  }

  /** セルの実装 */
  protected Impl impl;

  /**
   * デフォルトセルを指定してセルを作成する。あるいは新規にセルを作成する
   * <p>
   * デフォルトセル指定された場合は、そのセルの状態をコピーした新たなセルを作成する。
   * デフォルトセル指定の無い場合は、新たなセルを作成する。
   * </p>
   * @param defaultCell デフォルトセル
   */
  public MkCell(PdfPCell defaultCell) {
    if (defaultCell != null) {
      // デフォルトセルのある場合、その内容をコピーした新たなセルを作成して、実装クラスにラップする
      impl = new Impl(new PdfPCell(defaultCell));
    } else { 
      // デフォルトセルの無い場合、新たなセルを作成して実装クラスにラップする。
      impl = new Impl(new PdfPCell((Phrase)null));
    }
  }

  /**
   * セルの複製のみに使用される
   * @param impl
   */
  protected MkCell(Impl impl) {
    (this.impl = impl).refCount++;
  }

  /**
   * セルに対する変更を準備する。参照カウントが２以上であれば、
   * 実体をコピーして自分用に確保する。
   * @return
   */
  protected Impl modifyImpl() {
    if (impl.refCount > 1) {
      impl.refCount--;
      impl = new Impl(new PdfPCell(impl.cell));
    }
    return impl;
  }
  
  /** セルの背景色を指定する */
  public MkCell setBgColor(MkColor color) {
    modifyImpl().cell.setBackgroundColor(color.getBaseColor());
    return this;
  }

  /** セルの境界表示を設定する */
  public MkCell setBorderVisible(MkBorder... borders) {
    EnumSet<MkBorder> set = EnumSet.noneOf(MkBorder.class);
    Arrays.stream(borders).forEach(b -> set.add(b));
    return setBorderVisible(set);
  }

  /** セルの境界表示を設定する */
  public MkCell setBorderVisible(EnumSet<MkBorder> borders) {
    int value = 0;
    for (MkBorder border : MkBorder.values()) {
      if (borders.contains(border))
        value |= border.value;
    }
    modifyImpl().cell.setBorder(value);
    return this;
  }
  
  public EnumSet<MkBorder>getBorderVisible() {
    int value = impl.cell.getBorder();
    EnumSet<MkBorder>set = EnumSet.noneOf(MkBorder.class);
    for (MkBorder border: MkBorder.values()) {
      if ((value & border.value) != 0)  set.add(border);
    }
    return set;
  }

  /** ボーダーの描画状態を指定する */
  public MkCell setBorderVisible(boolean value) {
    if (value) {
      setBorderVisible(EnumSet.allOf(MkBorder.class));
    } else {
      setBorderVisible(EnumSet.noneOf(MkBorder.class));
    }
    return this;
  }

  /**
   * ボーダー幅を指定する
   */
  public MkCell setBorderWidths(MkUnit unit, float top, float bottom, float left, float right) {
    return setBorderWidths(new MkInsets(unit, top, bottom, left, right));
  }
  
  /**
   * ボーダー幅を指定する
   */
  public MkCell setBorderWidths(MkInsets insets) {
    PdfPCell cell = modifyImpl().cell;
    cell.setBorderWidthLeft(insets.left.ptValue());
    cell.setBorderWidthRight(insets.right.ptValue());
    cell.setBorderWidthTop(insets.top.ptValue());
    cell.setBorderWidthBottom(insets.bottom.ptValue());
    return this;
  }

  /**
   * ボーダー幅を取得する
   * @return ボーダー幅
   */
  public MkInsets getBorderWidth() {
    PdfPCell cell = impl.cell;
    return new MkInsets(
      MkUnit.PT, 
      cell.getBorderWidthTop(),
      cell.getBorderWidthBottom(),
      cell.getBorderWidthLeft(),
      cell.getBorderWidthRight()
    );
  }
  
  /** セルの列スパン数を指定する */
  public MkCell setColSpan(int colSpan) {
    modifyImpl().cell.setColspan(colSpan);
    return this;
  }

  /** 行のスパン数を指定する */
  public MkCell setRowSpan(int rowSpan) {
    modifyImpl().cell.setRowspan(rowSpan);
    return this;
  }

  /** セルのアラインメントを取得する */
  public MkAlign getAlign() {
    return MkAlign.getByValue(impl.cell.getHorizontalAlignment());
  }
  
  /** セルのアラインメントを指定する */
  public MkCell setAlign(MkAlign align) {
    modifyImpl().cell.setHorizontalAlignment(align.value);    
    return this;
  }
  
  /** 
   * セルパディングを指定する
   * セルの上下左右のボーダーまでの距離
   * @param insets 上下左右のパディング長さ
   * @return このオブジェクト
   */
  public MkCell setPaddingWidths(MkInsets insets) {
    PdfPCell cell = modifyImpl().cell;
    cell.setPaddingLeft(insets.left.ptValue());
    cell.setPaddingRight(insets.right.ptValue());
    cell.setPaddingTop(insets.top.ptValue());
    cell.setPaddingBottom(insets.bottom.ptValue());    
    return this;
  }
  
  /**
   * セルパディングを取得する
   * @return セルパディング値
   */
  public MkInsets getPaddingWidths() {
    PdfPCell cell = impl.cell;
    return new MkInsets(
      MkUnit.PT,
      cell.getPaddingTop(),
      cell.getPaddingBottom(),
      cell.getPaddingLeft(),
      cell.getPaddingRight()
    );
  }

  /** 
   * セルを複製する。本オブジェクトと同じ実体を得る。
   * @return
   */
  public MkCell duplicate() {
    return new MkCell(impl);
  }

  /** 
   * {@link PdfPCell}を取得する。
   * これに対して変更を行ってはならない
   */
  public PdfPCell getITextCell() {
    return impl.cell;
  }  
}

