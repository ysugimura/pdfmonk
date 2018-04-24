package com.cm55.pdfmonk;

import java.io.*;

import com.itextpdf.text.*;
import com.itextpdf.text.io.*;
import com.itextpdf.text.pdf.*;

/**
 * 複数のPDFを一つにまとめて出力する
 * <p>
 * 単純に出力先を指定して、入力とするPDFを次々に指定していき、最後にクローズする。
 * 入力としては、バイト配列、入力ストリーム、ファイルのいずれでもよい。
 * </p>
 * @author ysugimura
 */
public class MkPdfBinder {

  private OutputStream output;
  private Document document = null;
  private PdfCopy copy = null;

  /**
   * 出力先ファイルを指定する。オープンされるが、このオブジェクトのクローズ時に自動的にクローズされる。
   * @param file
   * @throws IOException
   */
  public MkPdfBinder(File file) throws IOException {
    try {
      output = new FileOutputStream(file);
    } catch (IOException ex) {
      throw new MkException(ex);
    }
  }
  
  /**
   * 出力ストリームを指定する。このオブジェクトのクローズ時に自動的にクローズされる。
   * @param out 出力先ストリーム
   */
  public MkPdfBinder(OutputStream out) {
    this.output = out;
  }

  /** バイト配列の形のPDFを追加する */
  public void add(byte[] bytes) {
    add(new RandomAccessFileOrArray(
        new RandomAccessSourceFactory().createSource(bytes)));
  }

  /** 入力ストリームの形のPDFを追加する */
  public void add(InputStream in) {
    try {
    add(new RandomAccessFileOrArray(
        new RandomAccessSourceFactory().createSource(in)));
    } catch (IOException ex) {
      throw new MkException(ex);
    }
  }

  /** ファイルの形のPDFを追加する */
  public void add(File file) {
    try {
    add(new RandomAccessFileOrArray(
        new RandomAccessSourceFactory().createSource(new FileInputStream(file))));
    } catch (IOException ex) {
      throw new MkException(ex);
    }
  }

  /**
   * 入力の内容を出力に追加する。
   * @param input 追加する入力
   */
  private void add(RandomAccessFileOrArray input) {
    try {
      PdfReader reader = new PdfReader(input, null); // ここがポイント
      reader.consolidateNamedDestinations();
      if (document == null) {
        document = new Document(reader.getPageSizeWithRotation(1));
        copy = new PdfCopy(document, output);
        document.open();
      }
      PdfImportedPage page;
      int numberOfPages = reader.getNumberOfPages();
      for (int i = 1; i <= numberOfPages; i++) {
        page = copy.getImportedPage(reader, i);
        copy.addPage(page);
      }
      reader.close();
    } catch (Exception ex) {
      throw new MkException(ex);
    }
  }

  /**　
   * クローズする。
   * {@link #output}も自動的にクローズされる。
   */
  public void close() {
    copy.close();
    document.close();
  }
}
