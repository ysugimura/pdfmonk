package com.cm55.pdfmonk;

import com.itextpdf.text.pdf.*;

/**
 * {@link MkTemplate}は{@link MkContentByte}の下位クラスであり、{@link MkContentByte}と同等の機能を持つ。
 * <p>
 * テンプレートは{@link MkContentByte#createTemplate(MkDimension)}等によって作成され、
 * {@link MkContentByte#setTemplate(MkTemplate, MkDimension)}によって{@link MkContentByte}に置かれるのだが、
 * しかし、元となった{@link MkContentByte}にのみ設置が可能なわけではない。任意の{@link MkContentByte}に設置可能のようである。
 * </p>
 * <p>
 * また、{@link MkTemplate}が実際にドキュメントに描画されるのは、ドキュメントがクローズされる直前である。したがって、例えば以下のようなことができる。
 * </p>
 * <ul>
 * <li>ドキュメントの各ページに、あらかじめテンプレートを設置しておき、改ページしていく。
 * <li>ドキュメントのクローズ直前に、各ページに置いたテンプレートに「第*ページ / 全*ページ」のような文字列を描画する。
 * </ul>
 * <p>
 * このようにして、各ページの描画時には未知だった総ページ数を各ページに描画することができる。これをサポートするために、ドキュメントにおいて改ページが行われたら
 * コールバックする仕組みがある。{@link MkDocument#setNewPageCallback(java.util.function.Consumer)}を参照のこと。
 * </p>
 * <p>
 * 上記の仕組み以外で、「各ページに総ページ数を描画する」ような仕組みとしては{@link MkStamper}を使う方法がある。
 * </p>
 * @author ysugimura
 */
public class MkTemplate extends MkContentByte {

  MkTemplate(MkContext ctx, PdfTemplate template, MkDimension size) {
    super(ctx, template, new MkGeometry(size, MkInsets.ZERO));
  }

  /** 
   * itext内部のオブジェクトを返す。
   * {@link PdfContentByte}ではなく、そのサブクラスの{@link PdfTemplate}になる。
   */
  @Override
  public PdfTemplate getITextContentByte() {
    return (PdfTemplate)super.getITextContentByte();
  }

}
