package com.cm55.pdfmonk;

public class MkInsets {
  
  public static final MkInsets ZERO = new MkInsets(MkLen.ZERO, MkLen.ZERO, MkLen.ZERO, MkLen.ZERO);
  
  public final MkLen left, right, top, bottom;
  
  public MkInsets(MkUnit unit, float top, float bottom, float left, float right) {
    this.top = new MkLen(unit, top);
    this.bottom = new MkLen(unit, bottom);
    this.left = new MkLen(unit, left);
    this.right =new MkLen(unit, right);
  }
  
  protected MkInsets(MkLen top, MkLen bottom, MkLen left, MkLen right) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }
  
  public MkDimension topLeft() {
    return new MkDimension(left, top);
  }
  
  public MkDimension bottomRight() {
    return new MkDimension(right, bottom);
  }
  
  @Override
  public String toString() {
    return  top + "," + bottom + "," + left + "," + right;
  }
}
