package com.cm55.pdfmonk;

import java.awt.*;
import java.awt.geom.*;

import com.itextpdf.awt.*;
import com.itextpdf.text.pdf.*;

/**
 * コンテントバイト
 * <p>
 * このオブジェクトは、PDF内部の描画バッファを表すものであり、様々な方法で取得あるいは生成される。また、その方法によって本クラスの下位クラスが操作を担当することがある。
 * </p>
 * <ul>
 * <li>{@link MkContentByte}：これが取得されるのは、既存のPDFを{@link MkStamper}で操作するときのみである。
 * 基本的に、そのPDF文書のジオメトリであり、１ページずつ別々のオブジェクトとなる。
 * <li>{@link MkCanvas}：これは、PDF新規作成時の最前面と再背面のレイヤーとして表現される。新規作成中のPDF文書のジオメトリであるが、
 *　複数ページにわたり同じオブジェクトが用いられ、描画中の改ページを行うこともできる。
 * <li>{@link Template}：ある{@link MkContentByte}から、任意のサイズのバッファとして作成される。つまり、指定されたジオメトリを持つ。
 * {@link MkTemplate}に描画した後には、それと元の{@link MkContentByte}に書き込むことができる。
 * </ul>
 */
public class MkContentByte {

  private MkContext ctx;
  
  /** itextのPDFコンテンツバイト */
  private final PdfContentByte pcb;

  /** このコンテンツバイトのジオメトリ */
  private final MkGeometry geometry;

  MkContentByte(MkContext ctx, PdfContentByte pcb,  MkGeometry geometry) {
    this.ctx = ctx;
    this.pcb = pcb;
    this.geometry = geometry;
  }

  /** ジオメトリを取得する */
  public MkGeometry getGeometry() {
    return geometry;
  }

  /** コンテキストを取得する */
  public MkContext getContext() {
    return ctx;
  }
  
  /**
   * 指定サイズのテンプレートを作成する。{@link #createTemplate(MkDimension)}を参照のこと。
   * @param unit サイズの単位
   * @param width 幅サイズ
   * @param height 高さサイズ
   * @return
   */
  public MkTemplate createTemplate(MkUnit unit, float width, float height) {
    return createTemplate(new MkDimension(unit, width, height));
  }
  
  /** 
   * 指定サイズの{@link MkTemplate}を作成する。
   * {@link MkTemplate}は{@link MkContentByte}のサブクラスであることに注意。
   * したがって、テンプレートにもまた{@link MkContentByte}と同じ描画を行うことができる。
   * 描画後は、{@link #setTemplate(MkTemplate, MkDimension)}を使用し、本コンテントバイトの任意の位置にテンプレートの内容を描画することができる。
   * @param size 作成するテンプレートのサイズ
   * @return テンプレート
   */
  public MkTemplate createTemplate(MkDimension size) {
    return new MkTemplate(ctx, pcb.createTemplate(size.x.ptValue(), size.y.ptValue()), size);
  }

  /** 
   * 指定されたテンプレートを指定位置に描画する。テンプレートは特にこのコンテントバイトから作成したものでなくてもよい。
   * {@link MkTemplate}を参照のこと。
   * @param template テンプレート
   * @param unit 描画位置の単位
   * @param x 描画x位置
   * @param y 描画ｙ位置
   */
  public void setTemplate(MkTemplate template, MkUnit unit, float x, float y) {
    setTemplate(template, new MkDimension(unit, x, y));
  }
  
  /** 
   * 指定されたテンプレートを指定位置に描画する。テンプレートは特にこのコンテントバイトから作成したものでなくてもよい。
   * {@link MkTemplate}を参照のこと。
   * @param template テンプレート
   * @param position 描画位置
   */
  public void setTemplate(MkTemplate template, MkDimension position) {
    MkLen x = position.x;
    MkLen y = position.y.add(template.getGeometry().size.y);
    MkPdfPosition pdfPos = getGeometry().toPdfPosition(x, y);
    pcb.addTemplate(template.getITextContentByte(), pdfPos.x, pdfPos.y);
  }

  /** 
   * この{@link MkContentByte}描画用の{@link Graphics2D}を作成する。使用後はdispose()すること。
   * 本システムの他の部分では、ミリメートル座標とポイント座標を混在して使用することができるが、Javaの{@link Graphics2D}の操作についてはそうではない。
   * {@link Graphics2D}に対してどのような座標系で描画するかを指定しなければならない。それがscaling引数である。
   * {@link MkUnit#MM}の場合はミリメートル座標系であり、{@link MkUnit#PT}の場合はポイント座標系になる。
   * 
   * なお、ここで作成する{@link Graphics2D}は、コンテントバイト全体に対するものであり、その一部だけを指定することはできない。
   * これを行うには、このコンテントバイトを元にして{@link MkContentByte#createTemplate(MkDimension)}を作成して、
   * その専用の{@link Graphics2D}を取得し、そこに描画を行い、さらにその後にこのコンテントバイトに書き戻す必要がある。
   * @param scaling
   * @return
   */
  public Graphics2D createGraphics(MkUnit unit) {
    
    MkDimension paperSize = this.getGeometry().getPaperSize();
    MkDimension topLeft = this.getGeometry().topLeftMargin();

    /*
     * PdfGraphics2Dを作成する。ここでは、用紙全体を描画領域としているが、
     * その任意の一部のみを描画領域とする方法は不明。例えば、サイズをより小さなものにすると、用紙の左下領域への描画になってしまう。
     * 用紙中の任意の場所の任意のサイズという指定がPdfGraphics2Dのコンストラクタに存在しない。
     * これを行うには、作成したGraphics2Dに対して、クリッピングとアフィン変換を行う必要があると思われる。
     */
    Graphics2D g2d = new PdfGraphics2D(pcb, paperSize.x.ptValue(), paperSize.y.ptValue());

    // アフィン変換を設定する
    // アフィン変換を作成
    // unitで指定された単位の値で描画できるようにスケーリングを行い、さらに印刷領域左上を原点とする。
    // アフィン変換オブジェクトにおいて、scaleしてからtranslateするには、逆の順でscale/translateを行う必要があることに注意
    AffineTransform trans = new AffineTransform();
    trans.translate(topLeft.x.ptValue(), topLeft.y.ptValue());
    float scale = unit.scalingTo(MkUnit.PT);
    trans.scale(scale, scale);
    g2d.setTransform(trans);

    return g2d;
  }
  
  /** itextのコンテンツバイトオブジェクトを取得する */
  public PdfContentByte getITextContentByte() {
    return pcb;
  }
}
