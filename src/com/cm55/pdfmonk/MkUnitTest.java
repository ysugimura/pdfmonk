package com.cm55.pdfmonk;

import org.junit.*;
import static org.junit.Assert.*;

public class MkUnitTest {

  @Test
  public void test() {
    assertEquals(72, MkUnit.IN.valueTo(1, MkUnit.PT), 0.01);
    assertEquals(25.4, MkUnit.IN.valueTo(1, MkUnit.MM), 0.01);
    assertEquals(1, MkUnit.IN.valueTo(1, MkUnit.IN), 0.01);
    
    assertEquals(2.834, MkUnit.MM.valueTo(1, MkUnit.PT), 0.01);
    assertEquals(1, MkUnit.MM.valueTo(1, MkUnit.MM), 0.01);
    assertEquals(0.030393, MkUnit.MM.valueTo(1, MkUnit.IN), 0.01);
        
    assertEquals(1, MkUnit.PT.valueTo(1, MkUnit.PT), 0.01);
    assertEquals(0.3527, MkUnit.PT.valueTo(1, MkUnit.MM), 0.01);
    assertEquals(0.0138888, MkUnit.PT.valueTo(1, MkUnit.IN), 0.01);

  }

}
