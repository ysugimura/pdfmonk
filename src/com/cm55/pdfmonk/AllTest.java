package com.cm55.pdfmonk;


import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.*;

@RunWith(Suite.class) 
@SuiteClasses( { 
  MkGeometryTest.class,
  MkUnitTest.class
})
public class AllTest {
  public static void main(String[] args) {
    JUnitCore.main(AllTest.class.getName());
  }
}