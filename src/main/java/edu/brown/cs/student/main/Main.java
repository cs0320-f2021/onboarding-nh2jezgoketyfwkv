package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    List<List<String>> data = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" ");
          MathBot mathBot = new MathBot();

          // Parses the command line input and determines how to process the information
          switch (arguments[0]) {
            case "add":  // Adds two numbers
              System.out.println(
                  mathBot.add(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])));
              break;
            case "subtract":  // subtracts two numbers
              System.out.println(
                  mathBot.subtract(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])));
              break;
            case "stars":
              // Utilizes the parsing method defined below to get a list of data
              data = parse(arguments[1]);
              break;
            case "naive_neighbors":
              if (arguments.length == 5 && arguments[2].charAt(0) != '"') {
                nearestNeighbors(Integer.parseInt(arguments[1]), Float.parseFloat(arguments[2]),
                    Float.parseFloat(arguments[3]), Float.parseFloat(arguments[4]), data);
              } else {
                StringBuilder name = new StringBuilder();
                // Concatenates name
                for (int i = 2; i < arguments.length; i++) {
                  name.append(" ").append(arguments[i]);
                }
                nearestNeighbors(Integer.parseInt(arguments[1]), name.substring(2,
                    name.toString().length() - 1).strip(), data);
              }
              break;
            default:
              System.out.println(arguments[0]);
              break;
          }

        } catch (Exception e) {
          // e.printStackTrace();
          System.out.println(e.getMessage());
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }

  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl)r
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
  /**
   * @param url A string with the location of the data
   * @return A 2 dimensional list of data
   */
  public static List<List<String>> parse(String url) {
    List<List<String>> data = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(url))) {
      String line;
      // Read through each line of the file
      while ((line = br.readLine()) != null) {
        // Converts the split line into a list of data
        data.add(Arrays.asList(line.split(",")));
      }
    } catch (Exception e) {
      System.out.println("Invalid file format");
    }
    return data;
  }

  /**
   * @param k    The number of neighbors being searched for
   * @param x    The x coordinate of the star being searched
   * @param y    The y coordinate of the star being searched
   * @param z    The z coordinate of the star being searched
   * @param data A 2d list of data
   */
  public static void nearestNeighbors(int k, double x, double y, double z,
                                          List<List<String>> data) {
    // Map distance and star name
    Map<Double, String> distanceMap = new HashMap<>();
    double[] distances = new double[data.size()];
    distances[0] = Float.MAX_VALUE;


    // Find distances between a given star and every other stars
    for (int i = 1; i < data.size(); i++) {
      double x2 = Double.parseDouble(data.get(i).get(2));
      double y2 = Double.parseDouble(data.get(i).get(3));
      double z2 = Double.parseDouble(data.get(i).get(4));
      double distance = Math.sqrt(Math.pow((x - x2), 2) + Math.pow((y - y2), 2) + Math.pow((z - z2),
          2));

      // We don't want to count the same planet
      if (distance == 0.0) {
        distances[i] = Float.MAX_VALUE;
        continue;
      }
      distanceMap.put(distance, data.get(i).get(0));
      distances[i] = distance;
    }
    // Sort distances
    Arrays.sort(distances);
    // Find the neighbors based on closest distances
    for (int i = 0; i < k; i++) {
      System.out.println(distanceMap.get(distances[i]));
    }
  }

  /**
   * @param k    The number of nearest neighbors to return
   * @param name The name of the star for which we are finding nearest neighbors
   * @param data A 2d list of data
   */
  public static void nearestNeighbors(int k, String name, List<List<String>> data)
      throws Exception {
    if (data.size() == 0) {
      throw new Exception("ERROR: No stars file has been parsed");
    }
    double x = 0, y = 0, z = 0;
    boolean starFound = false;
    // Search for the coordinates of the star
    for (int i = 1; i < data.size(); i++) {
      if (data.get(i).get(1).equals(name)) {
        // Parse the integers
        x = Double.parseDouble(data.get(i).get(2));
        y = Double.parseDouble(data.get(i).get(3));
        z = Double.parseDouble(data.get(i).get(4));
        starFound = true;
        break;
      }
    }
    if (!starFound) {
      throw new Exception("ERROR: No star has this name");
    }
    nearestNeighbors(k, x, y, z, data);
  }
}
