package com.cm55.pdfmonk;

import java.util.*;

import com.itextpdf.text.*;

public enum MkFontStyle {
  BOLD(Font.BOLD),
  ITALIC(Font.ITALIC),
  UNDERLINE(Font.UNDERLINE);
  public final int value;
  private MkFontStyle(int value) {
    this.value = value;
  }
  
  public static int getValue(EnumSet<MkFontStyle>set) {
    int value = 0;
    for (MkFontStyle s: set) {
      value |= s.value;
    }
    return value;
  }
  
  public static EnumSet<MkFontStyle>fromValue(int value) {
    EnumSet<MkFontStyle>set = EnumSet.noneOf(MkFontStyle.class);
    for (MkFontStyle s: MkFontStyle.values()) {
      if ((value & s.value) != 0) set.add(s);
    }
    return set;
  }
}
