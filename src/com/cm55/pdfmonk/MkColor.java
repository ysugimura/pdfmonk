package com.cm55.pdfmonk;

import com.itextpdf.text.*;

public class MkColor {

  public static final MkColor RED = new MkColor("ff0000");
  public static final MkColor GREEN = new MkColor("00ff00");
  public static final MkColor BLUE = new MkColor("0000ff");
  public static final MkColor LT_GRAY = new MkColor("d3d3d3");
  public static final MkColor WHITE = new MkColor("ffffff");
  private final BaseColor baseColor;  
  
  public MkColor(int red, int green, int blue, int alpha) {
    baseColor = new BaseColor(red, green, blue, alpha);
  }
  
  public MkColor(BaseColor baseColor) {
    this.baseColor = baseColor;
  }
  
  public MkColor(String hex) {    
      baseColor = new BaseColor(
          Integer.parseInt(hex.substring(0, 2), 16),
          Integer.parseInt(hex.substring(2, 4), 16),
          Integer.parseInt(hex.substring(4, 6), 16),
          255
      );
  }
  
  public BaseColor getBaseColor() {
    return baseColor;
  }
}
