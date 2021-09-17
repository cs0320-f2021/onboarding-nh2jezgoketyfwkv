package edu.brown.cs.student.main;

import org.junit.Test;

import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NearestNeighborsTest {


  @Test
  public void fiveNeighborsTest() throws Exception {
    List<List<String>> data = Main.parse("data/stars/ten-star.csv");
    Main.nearestNeighbors(5, "Sol", data);

  }
  @Test
  public void largerFileTest(){

  }
}
