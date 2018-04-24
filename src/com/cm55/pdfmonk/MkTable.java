package com.cm55.pdfmonk;

import java.util.*;
import java.util.List;
import java.util.stream.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * テーブル
 * <h2>テーブルの定義</h2>
 * <p>
 * テーブルは最初にコンテキストと列数を指定する必要がある。
 * コンテキストは簡易テキストセル追加の際等に使用される。列数はテーブルへのセル追加に必須となる。
 * </p>
 * <h2>テーブルへのセルの追加</h2>
 * <p>
 * {@link #addCell(String)}等でセルを追加していくのだが、
 * 追加していく方向は当然のことながら、左上から右方向になり、一行分が終了すると自動的に次の行の先頭ということになる。
 * つまり、確実に一行分のセルを追加していかないと、列位置がずれることになる。
 * ただし、colspanやrowspanによって詰める数が変わることに注意。
 * </p>
 * <h2>テキストセルの追加</h2>
 * <p>
 * テーブルにテキストセルを置く場合にも、{@link MkTextCell}を生成し、{@link #addCell(MkCell)}の引数としても
 * 良いのだが、これでは面倒なため、特にテキストについては、簡単に置けるメソッドが用意してある。それが、
 * {@link #addTextCell(String)}等である。
 * </p>
 * <h2>各列の幅の設定</h2>
 * <p>
 * 各列の幅設定は、すべてのセルを格納し、テーブルを描画する直前で構わない。これをサポートしている理由としては、
 * セルをすべて置いた後で、各列の最大幅を調査してから、各列の幅を決定したい場合に対応するものである。
 * そのような必要がなければ、テーブル生成後にすぐ各列のサイズを設定してしまってよい。
 * </p>
 */
public class MkTable implements MkElement, MkSpacing<MkTable> {

  /** コンテキスト */
  private MkContext ctx;
  
  /** iTextのテーブル */
  private PdfPTable table;
 
  /** 
   * 列数のみを指定して作成する。
   * この時点では各列は等しい適当な幅に設定される。
   */
  public MkTable(MkContext ctx, int columns) {
    this.ctx = ctx;
    table = new PdfPTable(columns);
  }

  /**
   * テーブル全体幅と各列の幅比率を指定する。
   * @param unit テーブル全体幅単位
   * @param tableWidth テーブル全体幅
   * @param ratios 各列幅の比率。列数は正確にコンストラクタで指定した数に一致していること
   * @return このオブジェクト
   */
  public MkTable setColumnRatios(MkUnit unit, float tableWidth, float[] ratios) {
    return this.setColumnRatios(new MkLen(unit, tableWidth), ratios);
  }
  
  /**
   * テーブル全体幅と各列の幅比率を指定する。
   * @param tableWidth テーブル全体幅
   * @param ratios 各列幅の比率。列数は正確にコンストラクタで指定した数に一致していること
   * @return このオブジェクト
   */
  public MkTable setColumnRatios(MkLen tableWidth, float[]ratios) {
    float total = (float)IntStream.range(0, ratios.length).mapToDouble(i->ratios[i]).sum();
    List<MkLen>widths = IntStream.range(0, ratios.length).mapToObj(
      i->new MkLen(MkUnit.PT, tableWidth.ptValue() * ratios[i] / total)).collect(Collectors.toList());
    return setColumnWidths(widths);
  }

  /**
   * 各列の幅を指定する。テーブルサイズはその合計となる。
   * @param unit
   * @param columnWidths
   * @return
   */
  public MkTable setColumnWidths(MkUnit unit, List<Float>columnWidths) {
    return this.setColumnWidths(columnWidths.stream().map(f->new MkLen(unit,  f)).collect(Collectors.toList()));
  }
  
  /** 各列の幅を指定する。テーブルサイズは合計サイズになる */
  public MkTable setColumnWidths(List<MkLen>columnWidths) {
    return setColumnWidths(columnWidths.toArray(new MkLen[0]));
  }
  
  public MkTable setColumnWidths(MkUnit unit, float[]columnWidths) {
    return this.setColumnWidths(unit,  
        IntStream.range(0,  columnWidths.length).mapToObj(i->(Float)columnWidths[i]).collect(Collectors.toList())
    );
  }
  
  /** 
   * 各列の幅を指定する。テーブルサイズは合計サイズにする。
   * @param columnWidths
   * @return
   */
  public MkTable setColumnWidths(MkLen[] columnWidths) {
    float[]widthsPt = new float[columnWidths.length];
    for (int i = 0; i < widthsPt.length; i++) widthsPt[i] = columnWidths[i].ptValue();
    return setColumnWidthsPt(widthsPt);
  }
  
  /** 
   * ポイント数で指定した絶対的な列幅を指定し、その幅にロックする.
   * {@link PdfTable}にはもともと様々な列幅指定方法があるが、ここでは絶対的な列幅を指定してその幅にロックし、勝手に幅が変わらないようにしている。
   * 以下を参考のこと。
   * https://developers.itextpdf.com/question/how-define-width-cell
   */
  MkTable setColumnWidthsPt(float[]widthsPt) {    
    // 絶対的な列幅を指定する。これらの幅はロックされ、勝手に変更されることはない。
    try {
      table.setTotalWidth(widthsPt);
    } catch (DocumentException ex) {
      throw new MkException(ex);
    }
    table.setLockedWidth(true);
    return this;
  }
  
  /**
   * セルをテーブルに追加する。
   * ※注意：この操作では内部的な{@link PdfPCell}の複製がテーブルに追加されるため、
   * これ以降元のセルを操作してもテーブル上のセルには影響は及ばない。
   * @param cell 追加するセル
   */
  public MkTable addCell(MkCell cell) {
    table.addCell(cell.getITextCell());
    return this;
  }
  
  /** 
   * このテーブルのコンテキストでテキストセルを作成して追加する。コンテキストはテーブルのものが使用される。
   * ※テーブルにセルを追加した後でセルの状態を変更しても反映されないため、このメソッドは追加したセルを返り値とはしない。
   * @param text セルの中身とするテキスト。文字'\n'によってセル内で改行される。
   */
  public MkTable addTextCell(String text) {
    addTextCell(ctx, text);
    return this;
  }
  
  /** 
   * このテーブルのコンテキストでテキストセルを作成して追加する。ただし、指定アラインを適用する。コンテキストはテーブルのものが使用される。
   * ※テーブルにセルを追加した後でセルの状態を変更しても反映されないため、このメソッドは追加したセルを返り値とはしない。
   * @param text セルの中身とするテキスト。文字'\n'によってセル内で改行される。
   * @param align セル内テキストのアラインメント
   */
  public MkTable addTextCell(String text, MkAlign align) {
    addTextCell(ctx, text, align);
    return this;
  }

  /** 
   * コンテキストを指定してテキストセルを作成して追加する 
   * ※テーブルにセルを追加した後でセルの状態を変更しても反映されないため、このメソッドは追加したセルを返り値とはしない。
   * @param ctx コンテキスト
   * @param text セルの中身とするテキスト。文字'\n'によってセル内で改行される。
   */
  public MkTable addTextCell(MkContext ctx, String text) {
    return addCell(new MkTextCell(ctx, text));
  }
  
  /** 
   * コンテキストを指定してテキストセルを作成して追加する 。ただし、指定アラインを適用する。
   * ※テーブルにセルを追加した後でセルの状態を変更しても反映されないため、このメソッドは追加したセルを返り値とはしない。
   * @param ctx コンテキスト
   * @param text セルの中身とするテキスト。文字'\n'によってセル内で改行される。
   * @param align セル内テキストのアラインメント
   */
  public MkTable addTextCell(MkContext ctx, String text, MkAlign align) {
    MkCell cell = ctx.getCell();
    MkAlign save = cell.getAlign();
    cell.setAlign(align);
    try {
      addCell(new MkTextCell(ctx, text));
    } finally {
      cell.setAlign(save);
    }
    return this;
  }

  /** テーブルの描画前スペーシングを指定する */
  public MkTable setSpacingBefore(MkUnit unit, float size) {
    return setSpacingBefore(new MkLen(unit, size));
  }
  
  /** テーブルの描画前スペーシングを指定する */
  public MkTable setSpacingBefore(MkLen size) {
    table.setSpacingBefore(size.ptValue());
    return this;
  }

  /** テーブルの描画後スペーシングを指定する */
  public MkTable setSpacingAfter(MkLen size) {
    table.setSpacingAfter(size.ptValue());
    return this;
  }
  
  /** テーブルの描画後スペーシングを指定する */
  public MkTable setSpacingAfter(MkUnit unit, float size) {
    return setSpacingAfter(new MkLen(unit, size));
  }

  /** {@link Element}としてのテーブル */
  @Override
  public Stream<Element> getElements() {
    return Arrays.stream(new Element[] { table });
  }

  /** 
   * 各列それぞれの最大サイズを取得する。
   * <p>
   * {@link MkTable}は、すべてのセルを格納した後で{@link #setColumnWidths(List)}等で各セルの描画幅を指定することができるが、
   * その際に、実際の各列に格納されたセルデータの幅を加味して、各列の描画幅を決定したい場合がある。
   * このメソッドによって、各列の最大描画幅を得ることができる。
   * </p>
   * <p>
   * ただし、colspanが1以外のものは無視される。
   * </p>
   * @return
   */
  public MkLen[]getColumnMaxWidths() {    
    int colCount = table.getNumberOfColumns();
    int rowIndex = 0;    
    float[]widthsPt = new float[colCount];
    for (PdfPRow row: table.getRows()) {
      PdfPCell[]pcells = row.getCells();
      for (int col = 0; col < pcells.length; col++) {      
        if (pcells.length <= col) continue;
        PdfPCell pcell = pcells[col];
        if (pcell == null) continue;
        if (pcell.getColspan() != 1) continue;
        widthsPt[col] = Math.max(widthsPt[col],  getPtWidthOf(pcell));
      }
      rowIndex++;
    }
    return IntStream.range(0, colCount)
        .mapToObj(i->new MkLen(MkUnit.PT, widthsPt[i]))
        .collect(Collectors.toList()).toArray(new MkLen[0]);
  }
  
  /** 
   * 指定されたセルのポイント単位の描画幅を取得する
   * ただし、現在はテキストセルにしか対応していない。
   * @param pcell 対象とするセル
   * @return セルの描画幅（ポイント）
   */
  float getPtWidthOf(PdfPCell pcell) {
    float contentWidth = 0;    
    Phrase phrase = pcell.getPhrase();
    if (phrase != null) {
      Font font = phrase.getFont();
      String text = phrase.getContent();
      contentWidth = phrase.getFont().getBaseFont().getWidthPoint(text, font.getSize());
    }
    
    // 左右のボーダーとパディングの幅を取得する
    float borderPadding = 
        pcell.getBorderWidthLeft() + pcell.getBorderWidthRight() +
        pcell.getPaddingLeft() + pcell.getPaddingRight();

    // 幅を返す
    return contentWidth + borderPadding;
  }
  
  /** 
   * このテーブルのサイズを取得する。
   * @return　テーブルのサイズ
   */
  public MkDimension getSize() {
    return new MkDimension(MkUnit.PT, table.getTotalWidth(), table.getTotalHeight());
  }

  /**
   * このテーブルをコンテントバイトの指定位置に設定する
   * <p>
   * ※iText上での指定位置はテーブルの左上であることに注意。したがって、テーブルの高さによって位置を 補正する必要はない。
   * </p>
   * @param canvas キャンバス
   * @param unit 位置の単位
   * @param x ｘ位置
   * @param y ｙ位置
   */
  public void setToContentByte(MkContentByte canvas, MkUnit unit, float x, float y) {
    setToContentByte(canvas, new MkDimension(unit, x, y));
  }
  
  /**
   * このテーブルをコンテントバイトの指定位置に設定する
   * <p>
   * ※iText上での指定位置はテーブルの左上であることに注意。したがって、テーブルの高さによって位置を 補正する必要はない。
   * </p>
   * @param canvas キャンバス
   * @param position 描画指定位置
   */  
  public void setToContentByte(MkContentByte canvas, MkDimension position) {
    MkPdfPosition pdfPosition = canvas.getGeometry().toPdfPosition(position);
    table.writeSelectedRows(0, -1, pdfPosition.x, pdfPosition.y, canvas.getITextContentByte());    
  }

  /**
   * <p>
   * このテーブルを、指定されたキャンバスの現在のy位置から流し込む。
   * 行がはみ出すようであれば、改ページして残りの行の流し込みを続行する。
   * この機能は、{@link MkCanvas}でしか機能しない。すなわち、ドキュメントのレイヤーとしてのコンテンツバイトのみに利用可能である。
   * </p>
   * @param canvas 描画先のキャンバス
   */  
  public void addToCanvas(MkCanvas canvas) {
    addToCanvas(canvas, MkAlign.LEFT);
  }
  
  /**
   * <p>
   * このテーブルを、指定されたキャンバスの現在のy位置から流し込む。
   * 行がはみ出すようであれば、改ページして残りの行の流し込みを続行する。
   * この機能は、{@link MkCanvas}でしか機能しない。すなわち、ドキュメントのレイヤーとしてのコンテンツバイトのみに利用可能である。
   * 幅としてはドキュメントの印刷可能領域一杯となるが、alignで指定されたアライメントが適用される。
   * </p>
   * @param canvas 描画先のキャンバス
   */
  public void addToCanvas(MkCanvas canvas, MkAlign align) {
    table.setHorizontalAlignment(align.value);
    MkGeometry page = canvas.getGeometry();
    
    MkLen y = canvas.getVertical();
    MkRect writeArea = new MkRect(       
      MkLen.ZERO, 
      y, 
      page.getPrintWidth(),
      page.getPrintHeight().sub(y)
    );

    MkColumnText column = new MkColumnText(canvas);
    column.addElement(this);
    while (true) {
      // 描画領域を設定
      if (column.provideArea(writeArea))break;
      
      // 改ページを行い、1ページの残りの書き込める領域を１ページ分に変更する。
      canvas.newPage();
      writeArea = new MkRect(MkLen.ZERO, MkLen.ZERO, page.getPrintWidth(), page.getPrintHeight());
    }
    
    // 最終書き込み位置をキャンバスに設定しておく
    canvas.setVertical(column.getYLine());
  }

}
