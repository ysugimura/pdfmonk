package com.cm55.pdfmonk.sample;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;

import com.cm55.pdfmonk.*;

/**
 * PDF出力は、カレントディレクトリのoutput.pdfに書き込まれることに注意。
 * @author ysugimura
 */
public class Sample1 {

  public static void main(String[] args) throws IOException {
    new Sample1().execute();
  }
  
  public void execute() throws IOException {

    // PDF出力先ファイル
    File outFile = new File("output.pdf");
    
    // ドキュメントを作成し、キャンバスを取得する
    MkGeometry geo = MkGeometry.A4_PORTRAIT;
    MkContext ctx = MkContext.getDefault();
    MkDocument doc = new MkDocument(ctx, geo, outFile);
    MkCanvas canvas = doc.getCanvasOver();

    // ページナンバリングオブジェクトをセットアップ
    MkPageNumbering pageNumbering = new MkPageNumbering(canvas, 
      geo.getBottomLeft(),
      new MkDimension(geo.getPrintWidth(), ctx.getFont().getSize())
    );
    doc.setNewPageCallback(pageNumbering::create);
        
    // キャンバスの絶対座標を指定して描画する
    drawCustomer(canvas);
    drawTitle(canvas);
    drawLogo(canvas);
    
    // これ以降は、キャンバスの「現在のy位置」に描画する
    canvas.setVertical(MkUnit.MM, 55);
    drawTable(canvas);

    // 各ページ番号を描画する
    drawPageNumber(ctx, pageNumbering, doc.getPageNumber());
    
    // ドキュメントをクローズして表示する
    doc.close();    
    Desktop desktop = Desktop.getDesktop();
    desktop.browse(outFile.toURI());   
  }
  
  /** お客様名称住所を描画する */
  void drawCustomer(MkCanvas canvas) {
    MkContext ctx = canvas.getContext();
    
    // 顧客住所名称等を描画
    MkTextBlock tb = new MkTextBlock(ctx, 
      "〒 123-4567\n" +
      "\n" +
      "＊＊県＊＊市＊＊町＊＊12-23-34\n" +
      "\n" +
      "鈴木一郎　様"
    );
    tb.setToContentByte(canvas, MkUnit.MM, 0, 0);

    // 枠で囲む。Graphics2Dの座標系をミリメートルとして、ミリメートル座標系で描画する。
    // ここでは単純に矩形しか描画していないが、おそらくGraphics2Dのすべての機能が使用できると思われる。
    // ただし、座標系変換のためにアフィン変換を設定しているので、アフィン変換を再設定すると、当然ながらミリメートル座標系ではなくなる。
    Graphics2D g2d = canvas.createGraphics(MkUnit.MM);
    g2d.setStroke(new BasicStroke(0.1F, 0, 0));
    g2d.draw(new Rectangle2D.Float(0, 0, 50, 30));
    
    // 使い終わったら必ずdispose()すること。これをしないとドキュメントクローズ時にエラーが発生する。
    g2d.dispose();
  }

  /** タイトルを描画する */
  void drawTitle(MkCanvas canvas) {
    MkContext ctx = canvas.getContext().duplicate();
    MkGeometry geo = canvas.getGeometry();
    ctx.setFont(MkUnit.MM, 6);
    new MkTextBlock(ctx, "納品書")
      .setLimitWidth(geo.getPrintWidth())
      .setBlockAlign(MkAlign.CENTER)
      .setToContentByte(canvas,  MkUnit.MM, 0, 0);
  }
  
  /** ロゴを描画する */
  void drawLogo(MkCanvas canvas) throws IOException {
    float printWidth = canvas.getGeometry().getPrintWidth().mmValue();
    MkImage image = new MkImage(Sample1.class.getResource("dog.jpg"));
    image.setSizeAspect(MkUnit.MM, 50, 50);
    image.setToContentByteCentering(canvas, MkUnit.MM, printWidth - 50, 0, 50, 50);
  }
  
  /** キャンバスの現在位置からテーブルを描画する */
  void drawTable(MkCanvas canvas) {
    MkContext ctx = canvas.getContext();
    MkCell cell = ctx.getCell();    
        
    MkTable table = new MkTable(ctx, 6);
    table.setColumnRatios(canvas.getGeometry().getPrintWidth(), new float[] { 15, 15, 100, 15, 15, 15 });
    for (String name: new String[] {
      "", "商品コード", "商品名", "単価", "数量", "合計"
    }) {
      table.addTextCell(name, MkAlign.CENTER);
    }
    int grandTotal = 0;
    for (int sales = 0; sales < 10; sales++) {
      int subTotal = 0;
      table.addCell(new MkTextCell(ctx, "＊＊月＊＊日お買い上げ分").setColSpan(6));
      table.addCell(new MkTextCell(ctx, "詳細").setRowSpan(5).setAlign(MkAlign.CENTER).setVerticalAlign(MkVerticalAlign.MIDDLE));
      for (int detail = 1; detail <= 5; detail++) {
        table.addTextCell("01234", MkAlign.RIGHT);
        table.addTextCell("商品名称" + detail);
        table.addTextCell("" + (detail * 100), MkAlign.RIGHT);
        table.addTextCell("" + detail, MkAlign.RIGHT);
        table.addTextCell("" + (detail * detail * 100), MkAlign.RIGHT);
        subTotal += detail * detail * 100;
      }
      cell.setBorderVisible(false);
      table.addCell(new MkTextCell(ctx, "").setColSpan(3));
      cell.setBorderVisible(true);
      table.addCell(new MkTextCell(ctx, "小計").setColSpan(2));
      table.addTextCell("" + subTotal, MkAlign.RIGHT);
      grandTotal += subTotal;
    }
    cell.setBorderVisible(false);
    table.addCell(new MkTextCell(ctx, "").setColSpan(3));
    cell.setBorderVisible(true);
    table.addCell(new MkTextCell(ctx, "総合計").setColSpan(2));
    table.addTextCell("" + grandTotal, MkAlign.RIGHT);
    table.addToCanvas(canvas);
  }

  /** 各ページ番号を描画する */
  void drawPageNumber(MkContext ctx, MkPageNumbering pageNumbering, int totalPages) {
    pageNumbering.pageSlots().forEach(slot-> {
      new MkTextBlock(ctx, "" + slot.pageNumber + " ページ / 全 " + totalPages + " ページ")
      .setToContentByte(slot.template,  MkDimension.ZERO);
    });
  }
}
