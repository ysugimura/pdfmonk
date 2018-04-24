package com.cm55.pdfmonk;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import com.itextpdf.text.*;

/**
 * イメージ要素
 */
public class MkImage implements MkElement {

  private static final boolean DEBUG = false;

  private Image image;

  /** イメージファイルを指定して作成する */
  public MkImage(File file) throws IOException {
    this(Files.readAllBytes(file.toPath()));
  }

  /** イメージファイルバイトを指定して作成する */
  public MkImage(byte[] bytes) {
    try {
      image = Image.getInstance(bytes);
    } catch (Exception ex) {
      throw new MkException(ex);
    }
  }

  /** イメージ自体のサイズを取得する。単位はピクセル */
  public int getPixelWidth() {
    return Math.round(image.getWidth());
  }

  /** イメージ自体のサイズを取得する。単位はピクセル */
  public int getPixelHeight() {
    return Math.round(image.getHeight());
  }

  public MkImage setSizeAspect(MkUnit unit, float x, float y) {
    return this.setSizeAspect(new MkDimension(unit, x, y));
  }
  
  /** 描画サイズを指定する。ただしアスペクト比を保持するため、どちらか小さい方に合わせられる */
  public MkImage setSizeAspect(MkDimension size) {

    // イメージのピクセルサイズを取得する
    int imageWidth = getPixelWidth();
    int imageHeight = getPixelHeight();

    // 指定された領域サイズいっぱいの描画サイズを計算するが、ただしアスペクト比を保持する
    float width, height;
    if (size.y.ptValue() * imageWidth < imageHeight * size.x.ptValue()) {
      // 高さに合わせる
      height = size.y.ptValue();
      width = height * imageWidth / imageHeight;
    } else {
      // 幅に合わせる
      width = size.x.ptValue();
      height = width * imageHeight / imageWidth;
    }
    if (DEBUG)
      System.out.println("result " + width + "," + height);

    // イメージをスケーリングする
    image.scaleAbsolute(width, height);
    return this;
  }

  /** イメージの描画サイズを取得する。 */
  public MkDimension getDrawSize() {
    return new MkDimension(MkUnit.PT, image.getScaledWidth(), image.getScaledHeight());
  }
  
  /** iText要素を取得する */
  public Stream<Element> getElements() {
    return Arrays.stream(new Element[] { image });
  }

  public void setToContentByte(MkContentByte contentByte, MkUnit unit, float x, float y) {
    this.setToContentByte(contentByte, new MkDimension(unit, x, y));
  }
  
  /**
   * イメージを指定キャンバスの指定位置に設定する。 事前にsetSizeAspectでイメージ描画サイズを指定しておく必要がある。
   * 
   * @param contentByte  対象キャンバス
   * @param position 設定位置
   */
  public void setToContentByte(MkContentByte contentByte, MkDimension position) {
    setAbsolutePosition(contentByte.getGeometry(), position);
    try {
      contentByte.getITextContentByte().addImage(image);
    } catch (Exception ex) {
      throw new MkException(ex);
    } finally {
      this.resetAbsolutePosition();
    }
  }

  public void setToContentByteCentering(MkContentByte contentByte, MkUnit unit, float x, float y, float width, float height) {
    this.setToContentByteCentering(contentByte, new MkRect(unit, x, y, width, height));
  }
  
  /**
   * イメージを指定キャンバスの指定エリアに描画するが、ただし指定エリア内にセンタリングする。
   * 
   * @param contentByte
   *          キャンバス
   * @param area
   *          描画エリア
   */
  public void setToContentByteCentering(MkContentByte contentByte, MkRect area) {
    setAbsolutePosition(contentByte.getGeometry(), topLeftInArea(area));
    try {
      contentByte.getITextContentByte().addImage(image);
    } catch (Exception ex) {
      throw new MkException(ex);
    } finally {
      this.resetAbsolutePosition();
    }
  }

  /** イメージ左上をドキュメントの指定位置に置く */
  public void setToDocument(MkDocument document, MkDimension position) {
    setAbsolutePosition(document.getGeometry(), position);
    flushEditing();
    try {
      document.getITextDocument().add(image);
    } catch (Exception ex) {
      throw new MkException(ex);
    } finally {
      this.resetAbsolutePosition();
    }
  }

  /** イメージをドキュメント中の指定領域の中にセンタリングされるように置く */
  public MkImage setToDocumentCentering(MkDocument document, MkRect area) {
    setAbsolutePosition(document.getGeometry(), topLeftInArea(area));
    flushEditing();
    try {
      document.getITextDocument().add(image);
    } catch (Exception ex) {
      throw new MkException(ex);
    } finally {
      this.resetAbsolutePosition();
    }
    return this;
  }

  /** ある領域の中にイメージをセンタリングした場合の左上位置を取得する */
  private MkDimension topLeftInArea(MkRect area) {
    // 描画サイズを取得する
    MkDimension drawSize = getDrawSize();

    // 指定エリア内の描画位置を計算する
    float x = area.x.mmValue() + area.width.sub(drawSize.x).mmValue() / 2;
    float y = area.y.mmValue() + area.height.sub(drawSize.y).mmValue() / 2;

    // 絶対位置をイメージに設定
    return new MkDimension(MkUnit.MM, x, y);
  }
  

  /**
   * 絶対位置を指定する 座標系変換のために、ドキュメントのジオメトリが必要。
   */
  private void setAbsolutePosition(MkGeometry geometry, MkDimension position) {

    // 描画サイズを取得する
    MkDimension drawSize = getDrawSize();

    // 描画y位置は画像の底辺が指定されなければいけない。
    position = new MkDimension(position.x, position.y.add(drawSize.y));

    // PDF用の位置を取得して、Imageの絶対位置とする。
    MkPdfPosition positionPoint = geometry.toPdfPosition(position);
    image.setAbsolutePosition(positionPoint.x, positionPoint.y);
  }

  /** 
   * 絶対位置指定を解除する。絶対位置指定のままだと。
   * もし、この次にドキュメントに普通に追加された場合、絶対位置に追加されてしまう。
   * @return
   */
  private void resetAbsolutePosition() {
    image.setAbsolutePosition(Float.NaN, Float.NaN);
  }
  
  public void flushEditing() {
  }
}
