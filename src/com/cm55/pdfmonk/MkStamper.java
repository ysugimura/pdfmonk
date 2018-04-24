package com.cm55.pdfmonk;

import java.io.*;

import com.itextpdf.text.pdf.*;

/**
 * 既存のPDFファイルを入力し、各ページの{@link MkContent}に対して操作を行い、編集後のPDFを出力する。
 * <p>
 * 既存のPDFのページ数が取得できるため、主には分数ページ番号をふるのに利用される。
 * つまり、元々のPDFの生成中には、全体ページ数がわからないため、3/15のようなページ番号をふることはできない。
 * このオブジェクトは、いったん元のPDFを作成を終了してしまい、それを読み込みながらページ番号をふるといったことに利用される。
 * </p>
 * <p>
 * このオブジェクトを生成した後は{@link #pageCount}でページ数が取得できるので、１からそのページ数を指定して
 * {@link #getContentByteOver(int)}もしくは{@link #getContentByteUnder(int)}で、そのページの{@link MkContentByte}を取得する。
 * </p>
 * <p>
 * {@link MkContentByte}に描画できるものとしては、{@link MkImage#setToContentByte(MkContentByte, MkDimension)}、
 * {@link MkTextBlock#setToContentByte(MkContentByte, MkDimension)}がある。
 * {@link MkCanvas}とは異なり、{@link MkContentByte}には改ページ機能が無いので、複数ページにまたがる要素を描画することはできない。
 * </p>
 * <p>
 * 作業が終了したら{@link #close()}を呼び出す。
 * </p>
 */
public class MkStamper  {

  /** コンテキスト */
  private MkContext ctx;
  
  /** ページのジオメトリ */
  private MkGeometry geometry;
  
  /** PDFリーダ */
  private PdfReader reader;

  /** PDFスタンパ */
  private PdfStamper stamper;

  /** ページ数 */
  private int pageCount;
  
  public MkStamper(MkContext ctx, MkGeometry geometry, File in, File out) throws IOException {
    this.ctx = ctx;
    this.geometry = geometry;
    try {
      setup(ctx, new FileInputStream(in), new FileOutputStream(out));
    } catch (IOException ex) {
      throw new MkException(ex);
    }
  }
  
  /**
   * セットアップする。 ドキュメントのジオメトリ、PDF入力ストリーム、PDF出力ストリームを取得する。
   */
  public MkStamper(MkContext ctx, MkGeometry geometry, InputStream in, OutputStream out) {
    this.geometry = geometry;
    setup(ctx, in, out);
  }
  
  private void setup(MkContext ctx, InputStream in, OutputStream out) {
    this.ctx = ctx;
    try {
      reader = new PdfReader(in);
      pageCount = reader.getNumberOfPages();
      stamper = new PdfStamper(reader, out);
    } catch (Exception ex) {
      throw new MkException(ex);
    }
  }

  /** 総ページ数を取得する */
  public int pageCount() {
    return pageCount;
  }

  /** 指定ページの最前面コンテンツバイトを取得する。ページ番号は１からpageCount()まで */
  public MkContentByte getContentByteOver(int pageNumber) {
    PdfContentByte pcb = stamper.getOverContent(pageNumber);
    return new MkContentByte(ctx, pcb, geometry);
  }

  /** 指定ページの再背面コンテンツバイトを取得する。ページ番号は１からpageCount()まで */
  public MkContentByte getContentByteUnder(int pageNumber) {
    PdfContentByte pcb = stamper.getUnderContent(pageNumber);
    return new MkContentByte(ctx, pcb, geometry);
  }

  /** クローズする */
  public void close() {
    close(false);
  }

  /** クローズする */
  public void close(boolean flat) {
    try {
      if (flat)
        stamper.setFormFlattening(true);
      stamper.close();
      reader.close();
    } catch (Exception ex) {
      throw new MkException(ex);
    }
  }
}
