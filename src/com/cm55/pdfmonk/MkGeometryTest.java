package com.cm55.pdfmonk;

import org.junit.*;
import static org.junit.Assert.*;

public class MkGeometryTest {

  public static MkGeometry A4_PORTRAIT =
    new MkGeometry(
      new MkDimension(MkUnit.MM, 
      210, // width
      297  // height
      ),
      new MkInsets(MkUnit.MM, 
      20, 
      25, 
      25, 
      15
      )
   );
  
  @Test
  public void pdfConversion() {
    MkDimension position = new MkDimension(MkUnit.MM, 50, 60);
    MkPdfPosition pdf = A4_PORTRAIT.toPdfPosition(position);
    assertEquals(212.598F, pdf.x, 0.1F);
    assertEquals(615.118, pdf.y, 0.1F);
    position = A4_PORTRAIT.fromPdfPosition(pdf);
    assertEquals(50, position.x.mmValue(), 0.01F);
    assertEquals(60, position.y.mmValue(), 0.01F);    
  }
  
  @Test
  public void toStringTest() {
    System.out.println("" + A4_PORTRAIT);
  }

}
