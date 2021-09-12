package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathBotTest {

  @Test
  public void testAddition() {
    MathBot matherator9000 = new MathBot();
    double output = matherator9000.add(10.5, 3);
    assertEquals(13.5, output, 0.01);
  }

  @Test
  public void testLargerNumbers() {
    MathBot matherator9001 = new MathBot();
    double output = matherator9001.add(100000, 200303);
    assertEquals(300303, output, 0.01);
  }

  @Test
  public void testSubtraction() {
    MathBot matherator9002 = new MathBot();
    double output = matherator9002.subtract(18, 17);
    assertEquals(1, output, 0.01);
  }

  @Test
  public void TestLargerSubtraction(){
    MathBot matherator9003 = new MathBot();
    double output = matherator9003.subtract(1012312, 12312);
    assertEquals(1000000, output, 0.01);
  }

  @Test
  public void TestDoubles(){
    MathBot matherator9004 = new MathBot();
    double output = matherator9004.subtract(101.321, 1.21);
    assertEquals(100.111, output, 0.01);
  }

  @Test
  public void TestAddNegatives(){
    MathBot matherator9004 = new MathBot();
    double output = matherator9004.add(-1221, -12);
    assertEquals(-1233, output, 0.01);
  }

  @Test
  public void TestSubtractNegatives(){
    MathBot matherator9004 = new MathBot();
    double output = matherator9004.subtract(-1221, -12);
    assertEquals(-1209, output, 0.01);
  }
}
