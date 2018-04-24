package com.cm55.pdfmonk;

import java.util.*;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * 複数行の文字列を{@link MkContentByte}の指定位置に描画する
 * @author ysugimura
 */
public class MkTextBlock {

  /** コンテキスト */
  private MkContext ctx;
  
  /** 描画テキスト配列 */
  private String[]texts;

  /** 描画アトリビュート */
  private DrawAttr drawAttr = new DrawAttr();
  
  /**
   * 折り返された結果のテキスト
   */
  private FoldedTexts foldedTexts;
  
  /**
   * 一つの文字列を改行コードで区切り複数行テキストとする
   * @param text
   */
  public MkTextBlock(MkContext ctx, String text) {
    this(ctx, text.split("\n"));
  }
  
  /** 
   * コンテキストとテキスト配列を指定する
   * @param ctx コンテキスト
   * @param texts テキスト配列
   */
  public MkTextBlock(MkContext ctx, String[]texts) {
    this.texts = texts;
    this.ctx = ctx;
  }  

  public MkTextBlock setLimitWidth(MkUnit unit, float limitWidth) {
    return setLimitWidth(new MkLen(unit, limitWidth));
  }
  
  /** 制限幅を指定する */
  public MkTextBlock setLimitWidth(MkLen limitWidth) {
    drawAttr.limitWidth = limitWidth;
    foldedTexts = null;
    return this;
  }
  
  /** スペーシングを設定する */
  public MkTextBlock setSpacing(int spacing) {
    foldedTexts = null;
    drawAttr.lineSpacing = spacing;
    return this;
  }

  /** スペーシングを取得する */
  public int getSpacing() {
    return drawAttr.lineSpacing;
  }

  /** アラインメントを設定する */
  public MkTextBlock setTextAlign(MkAlign align) {
    drawAttr.textAlign = align;
    return this;
  }

  /** ブロックアラインメントを設定する */
  public MkTextBlock setBlockAlign(MkAlign align) {
    drawAttr.blockAlign = align;
    return this;
  }
  
  /** アラインメントを取得する */  
  public MkAlign getTextAlign() {
    return drawAttr.textAlign;
  }
    
  /** 現在のフォント設定、スペーシング設定でのこのテキストブロックのサイズを取得する */
  public MkDimension getSize() {
    ensureFoldedTexts();    
    return foldedTexts.drawSize;
  }

  /**
   * キャンバスの現在のy位置、x=0位置から描画するが、幅制限としてキャンバスジオメトリの
   * PrintWidthを適用する。
   * @param canvas
   * @return
   */
  public MkLen addToCanvasPrintWidthLimited(MkCanvas canvas) {
    this.setLimitWidth(canvas.getGeometry().getPrintWidth());
    return this.addToCanvas(canvas, MkLen.ZERO);
  }

  /**
   * 
   * @param canvas
   * @param unit
   * @param x
   * @return
   */
  public MkLen addToCanvas(MkCanvas canvas, MkUnit unit, float x) {
    return this.addToCanvas(canvas, new MkLen(unit, x));
  }
  
  /**
   * キャンバス上の指定ｘ位置に追加するが、ただし、幅はwidthに制限する。
   * そのwidth内において、指定されたアラインメントを適用する。
   * @param canvas
   */
  public MkLen addToCanvas(MkCanvas canvas, MkLen x) {
    ensureFoldedTexts();
    
    MkDimension drawSize = foldedTexts.drawSize;
    if (!canvas.canDraw(drawSize.y)) {
      canvas.newPage();
    }
    new Renderer(canvas, new MkDimension(x, canvas.getVertical()), drawAttr, foldedTexts, ctx.getFont()).draw();
    MkLen newY = canvas.getVertical().add(drawSize.y);
    canvas.setVertical(newY);
    return newY;
  }
  
  public MkLen setToContentByte(MkContentByte contentByte, MkUnit unit, float x, float y) {
    return this.setToContentByte(contentByte, new MkDimension(unit, x, y));
  }
    
  /**
   * このテキストブロックをコンテントバイトの指定位置に描画する
   * @param contentByte 対象コンテントバイト
   * @param position 描画位置
   * @return
   */
  public MkLen setToContentByte(MkContentByte contentByte, MkDimension position) {
    ensureFoldedTexts();
    return new Renderer(contentByte, position, drawAttr, foldedTexts, ctx.getFont()).draw();
  }
  
  private void ensureFoldedTexts() {
    if (foldedTexts != null) return;
    foldedTexts = new FoldedTexts(ctx.getFont(), texts, drawAttr);
  }
  
  /** 
   * 描画アトリビュート
   * 行間スペーシング、テキストアライン、ブロックアライン、制限幅を保持する
   * @author ysugimura
   */
  static class DrawAttr {
    
    /** 描画テキスト行スペーシング */
    private int lineSpacing = 20;

    /**
     * 各行のアラインメント。これが指定されている場合は、ブロック全体幅の中でテキストの描画アライメントが決められる。
     * ブロック全体幅は、limitWidthの指定がなければ、各行の描画サイズの最大幅であり、limitWidthがあればその値となる。
     */
    private MkAlign textAlign = MkAlign.LEFT;

    /**
     * 最大描画幅の指定。これがある場合は、はみ出すテキストは折り返される。
     */
    private MkLen limitWidth = null;

    /**
     * limitWidthが指定されている場合で、かつ元々のテキスト描画幅がlimitWidthよりも小さいときに、
     * ブロック全体のアラインメントを指定する。
     */
    private MkAlign blockAlign = MkAlign.LEFT;
  }
  
  
  /**
   * 指定フォントで、指定文字列配列を描画した場合に、描画アトリビュートの制限幅からはみ出す場合に、それらの行の折返し状態を格納する。
   * それと共に全体の描画サイズも格納しておく。
   * @author ysugimura
   */
  static class FoldedTexts {

    /** 折返し行配列 */
    String[] foldedTexts;
    
    /** 全体描画サイズ  */
    MkDimension drawSize;

    /** フォント、行配列、描画アトリビュートを指定する */
    FoldedTexts(MkFont font, String[] texts, DrawAttr drawAttr) {
      if (foldedTexts != null)
        return;
      if (drawAttr.limitWidth != null) {
        ensureFoldedTexts1(font, texts, drawAttr.limitWidth);
      } else {
        foldedTexts = texts;
      }

      float maxWidth = 0;
      float totalHeight = 0;
      float lineHeight = font.getSize().mmValue();
      float lineSpacingMM = lineHeight * drawAttr.lineSpacing / 100;
      for (String text : foldedTexts) {
        maxWidth = Math.max(maxWidth, font.getStringWidth(text).mmValue());
        totalHeight += lineHeight + lineSpacingMM;
      }
      if (totalHeight > 0)
        totalHeight -= lineSpacingMM;
      drawSize = new MkDimension(MkUnit.MM, maxWidth, totalHeight);
    }

    void ensureFoldedTexts1(MkFont font, String[] texts, MkLen limitWidth) {

      List<String> list = new ArrayList<String>();
      Arrays.stream(texts).forEach(s -> {
        MkLen len = font.getStringWidth(s);
        if (len.sub(limitWidth).isNegative()) {
          list.add(s);
          return;
        }
        list.addAll(divideByLimit(font, s, limitWidth));
      });
      foldedTexts = list.toArray(new String[0]);
    }

    List<String> divideByLimit(MkFont font, String line, MkLen limitWidth) {
      List<String> result = new ArrayList<>();
      StringBuilder stock = new StringBuilder();
      while (line.length() > 0) {
        MkLen len = font.getStringWidth(line);
        if (len.sub(limitWidth).isNegative()) {
          result.add(line);
          line = stock.reverse().toString();
          stock = new StringBuilder();
          continue;
        }
        stock.append(line.substring(line.length() - 1));
        line = line.substring(0, line.length() - 1);

      }
      return result;
    }
  }

  static class Renderer {
    
    MkContentByte contentByte;
    MkDimension position;
    DrawAttr drawAttr;
    FoldedTexts foldedTexts;
    MkFont font;
    
    Renderer(MkContentByte contentByte, MkDimension position, DrawAttr drawAttr, FoldedTexts foldedTexts, MkFont font) {
      this.contentByte = contentByte;
      this.position = position;
      this.drawAttr = drawAttr;
      this.foldedTexts = foldedTexts;
      this.font = font;
    }

    /**
     * 指定されたコンテントバイトの指定位置を左上としてテキストブロックを描画する。
     * ただし、このブロックはwidth幅の中でblockAlign詰めされる。例えば、幅100のテキストブロックをx位置0から描画することにし、widthが150でblockAlign右詰めとすると、
     * 実際には、このブロックは、x位置50に描画されることになる。
     * 上記はブロック自体の位置であるが、テキストブロック内部のアラインも加味される。textAlignが右詰めの場合には当然、ブロック内でテキストが右詰めされる。
     * 
     * @param contentByte
     * @param position
     * @param width
     * @param blockAlign
     * @return
     */
    public MkLen draw() {

      // テキストブロックを実際に描画する左上位置を取得する
      MkPdfPosition blockPosition = getBlockPosition(contentByte, position, foldedTexts);

      // PDFコンテントバイトを取得
      PdfContentByte pcb = contentByte.getITextContentByte();
      pcb.saveState();
      try {
        pcb.beginText();
        setFont(pcb, font);
        drawTexts(pcb, blockPosition, foldedTexts);
        pcb.endText();
      } finally {
        pcb.restoreState();
      }
      return position.y.add(foldedTexts.drawSize.y);
    }

    /**
     * コンテントバイトの指定位置からテキストブロックを描画する。 各行は{@link #textAlign}の値によってアラインメントされる。
     * 右詰め・中央詰めの場合には、計算されたブロックの幅にしたが行が設定される。
     * 
     * @param pcb
     * @param blockPosition
     */
    private void drawTexts(PdfContentByte pcb, MkPdfPosition blockPosition, FoldedTexts foldedTexts) {

      float fontSize = font.getSize().ptValue();
      float lineHeight = fontSize * (100 + drawAttr.lineSpacing) / 100;

      float y = blockPosition.y - fontSize;
      MkDimension blockSize = foldedTexts.drawSize;
      for (String text : foldedTexts.foldedTexts) {
        switch (drawAttr.textAlign) {
        case LEFT:
          pcb.showTextAligned(Element.ALIGN_LEFT, text, blockPosition.x, y, 0);
          break;
        case CENTER:
          pcb.showTextAligned(Element.ALIGN_CENTER, text, blockPosition.x + blockSize.x.ptValue() / 2, y, 0);
          break;
        case RIGHT:
          pcb.showTextAligned(Element.ALIGN_RIGHT, text, blockPosition.x + blockSize.x.ptValue(), y, 0);
          break;
        default:
          break;
        }
        y -= lineHeight;
      }
    }

    /**
     * コンテントバイトに描画用フォントを設定する。
     * 内部的には、フォントが設定できないので、スタイル付ベースフォントを使用し、フォント色はコンテントバイトに設定する。
     * 
     * @param pcb
     *          コンテントバイト
     * @param font
     *          フォント
     */
    private void setFont(PdfContentByte pcb, MkFont font) {

      /*
       * PCBへの直接描画ではフォントを使うことができず、ベースフォントを使う必要がある。
       * ここでは、元のフォントと同じスタイルのベースフォントを取得する。
       */
      BaseFont baseFont = font.getBaseFont().getStyled(font.getStyle()).getITextBaseFont();

      // コンテントバイトにベースフォントとサイズを設定する
      pcb.setFontAndSize(baseFont, font.getSize().ptValue());

      // ベースフォントには元のフォントの色指定は含まれていない。色はPCBへ設定する
      BaseColor color = font.getITextFont().getColor();
      if (color != null) {
        pcb.setColorFill(color);
        pcb.setColorStroke(color);
      }
    }

    /**
     * テキストブロックを実際に描画する左上位置を取得する。
     * 基本的には指定されたpositionを左上位置とするが、しかしwidthとblockAlignが指定された場合には、そのwidth内でblockAlignアラインメントを行う。
     * つまり、テキストブロック自体の幅が100だとして、描画x位置を0と指定されても、widthが150の右詰めの場合は、描画x位置は50になる。
     * 
     * @param contentByte
     * @param position
     * @param width
     * @param blockAlign
     * @return
     */
    private MkPdfPosition getBlockPosition(MkContentByte contentByte, MkDimension position, FoldedTexts foldedTexts) {
      MkPdfPosition positionPts = contentByte.getGeometry().toPdfPosition(position);
      if (drawAttr.limitWidth == null || drawAttr.blockAlign == null)
        return positionPts;
      float blockX = positionPts.x;
      MkDimension blockSizePts = foldedTexts.drawSize;
      switch (drawAttr.blockAlign) {
      case CENTER:
        blockX += (drawAttr.limitWidth.ptValue() - blockSizePts.x.ptValue()) / 2;
        break;
      case RIGHT:
        blockX += drawAttr.limitWidth.ptValue() - blockSizePts.x.ptValue();
        break;
      default:
        break;
      }
      return new MkPdfPosition(blockX, positionPts.y);
    }
  }
}
