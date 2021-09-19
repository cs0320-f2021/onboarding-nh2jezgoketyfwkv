package edu.brown.cs.student.main;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CSVParserTest {
  @Test
  public void tenStarTest() {
    List<List<String>> data = Main.parse("data/stars/ten-star.csv");
    assertEquals(data.get(1).get(1), "Sol");
    assertEquals(data.get(2).get(0), "1");
    assertEquals(data.get(4).get(2), "277.11358");
    assertEquals(data.get(6).get(3), "-0.36132");
    assertEquals(data.get(8).get(4), "-1.17665");
  }

  @Test
  public void starDataTest() {
    List<List<String>> data = Main.parse("data/stars/stardata.csv");
    for(int i = 0; i < 100; i++){
      int ran = (int) (Math.random() * data.size());
      int starId = Integer.parseInt(data.get(ran).get(0));
      assertEquals(ran - 1, starId, 0);
    }
  }
}
