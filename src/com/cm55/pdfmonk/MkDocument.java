package com.cm55.pdfmonk;

import java.io.*;
import java.util.function.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * PDFドキュメント
 * <p>
 * ファクトリによる作成時には、ページのサイズを表す{@link MkGeometry}と、出力先の{@link OutputStream}を必要とする。
 * 作成すると、最初のページへの書き込み状態になっている。ページを送るには{@link MkDocument#newPage()}を用いる。
 * {@link MkDocument#close()}を呼び出すとドキュメントへの書き込みは終了し、{@link OutputStream}も閉じられる。
 * </p>
 * <h2>PDFの４つのレイヤー</h2>
 * <p>
 * PDFには４つのレイヤーがあり、それぞれ独立して扱うようになっている。これには、以下がある（手前から後ろ側への順）
 * </p>
 * <ul>
 * <li>4. Direct Content(Over)
 * <li>3. High Level Object(Paragraph)
 * <li>2. High Level Object(Image)
 * <li>1. Direct Content(Under)
 * </ul>
 * <p>
 * これらのレイヤーは最終的なPDFの表示としては重ね合わされる。
 * </p>
 */
public class MkDocument  {

  /** コンテキスト */
  private MkContext ctx;

  /** このドキュメントのジオメトリ */
  private MkGeometry geometry;
  
  /** itextの{@link Document} */
  private Document document;

  /** itextの{@link PdfWriter} */
  private PdfWriter pdfWriter;

  /** 最上位コンテンツバイト */
  private MkCanvas canvasOver;

  /** 最下位コンテンツバイト */
  private MkCanvas canvasUnder;
  
  /** 現在のページ番号。1以上 */
  private int pageNumber;
  
  /** 改ページが行われた場合のコールバック */
  private Consumer<Integer>newPageCallback;

  /**
   * ドキュメントをオープンする。その際、ドキュメントのページサイズと出力先ファイルを指定する。
   * このシステムでは、一つのドキュメント内のすべてのページは同じジオメトリを持つものとする。
   * @param ctx コンテキスト
   * @param geo ジオメトリ
   * @param out 出力ファイル
   * @throws IOException
   */
  public MkDocument(MkContext ctx, MkGeometry geo, File out) throws IOException {
    this(ctx, geo, new FileOutputStream(out));
  }
  
  /**
   * ドキュメントをオープンする。その際、ドキュメントのページサイズと出力先ストリームを指定する。
   * このシステムでは、一つのドキュメント内のすべてのページは同じジオメトリを持つものとする。
   * 指定されたOutputStreamは、close時に自動的にcloseされる。
   */
  public MkDocument(MkContext ctx, MkGeometry geo, OutputStream out) {
    this.ctx = ctx;
    geometry = geo;
    pageNumber = 1;
    try {
      document = new Document(new Rectangle(geo.size.x.ptValue(), geo.size.y.ptValue()), geo.margins.left.ptValue(),
          geo.margins.right.ptValue(), geo.margins.top.ptValue(), geo.margins.bottom.ptValue());
      
      // PdfWriterを作成する
      pdfWriter = PdfWriter.getInstance(document, out);
      pdfWriter.setPdfVersion(PdfWriter.VERSION_1_7);

      // Documentをオープンする
      document.open();

      // キャンバスを得る
      canvasOver = new MkCanvas(pdfWriter.getDirectContent(), this);
      canvasUnder = new MkCanvas(pdfWriter.getDirectContentUnder(), this);

    } catch (Exception ex) {
      throw new MkException(ex);
    }
  }

  /**
   * 改ページが行われた場合のコールバックを設定する。
   * <p>
   * このコールバックを設定すると同時に、現在のページ番号でコールバックが行われる。
   * その後、{@link #newPage()}が呼び出され現在のページ番号が更新された後でコールバックされる。
   * </p>
   * @param newPageCallback 改ページが行われた場合のコールバック
   * @return このオブジェクト
   */
  public MkDocument setNewPageCallback(Consumer<Integer>newPageCallback) {
    this.newPageCallback = newPageCallback;
    newPageCallback.accept(pageNumber);
    return this;
  }

  /** itextの｛@link Document}オブジェクトを得る */
  public Document getITextDocument() {
    return document;
  }

  /** コンテキストを得る */
  public MkContext getContext() {
    return ctx;
  }

  /** ジオメトリを得る */
  public MkGeometry getGeometry() {
    return geometry;
  }

  /** 現在のページ番号を得る。１から開始する */
  public int getPageNumber() {    
    // document.getPageNumber();は使用できない。改ページで自動的に増加するものでは無い模様
    return pageNumber;
  }
  
  /** ドキュメントをクローズする */
  public void close() {
    document.close();
    document = null;
  }

  /** 最上位層キャンバスを得る */
  public MkCanvas getCanvasOver() {
    return canvasOver;
  }

  /** 最下位層キャンバスを得る */
  public MkCanvas getCanvasUnder() {
    return canvasUnder;
  }

  /** 
   * 改ページし、現在のページ番号を更新する。
   * {@link #setNewPageCallback(Consumer)}が設定されていれば、そのコールバックを呼び出す。
   */
  public void newPage() {
    document.newPage();
    pageNumber++;
    if (newPageCallback != null) newPageCallback.accept(pageNumber);
  }

  /** 要素を追加する */
  public void add(MkElement element) {
    element.getElements().forEach(e -> {
      try {
        document.add(e);
      } catch (Exception ex) {
        throw new MkException(ex);
      }
    });
  }
}
