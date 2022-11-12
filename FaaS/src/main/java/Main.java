import static Backend.Server.ServerBuilder.makeServerBuilder;

import Backend.Server.Server;
import Backend.Server.ServerBuilderException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

  /**
   * Q1.a) Implement a simulator of a FaaS system hosted on single machine and compute unbiased
   *       point and interval estimates for:
   *       C_ratio - the cold start ratio, i.e. the probability that a request incurs a cold start.
   *       L_rate  - the loss rate, i.e., the rate at which request are lost.
   * */
  public static void main(String[] args) throws ServerBuilderException, IOException {

    if (args.length != 1) {
      printUsage();
    }

    // create a default server implementation
    Server server = makeServerBuilder().setLambda_f(1.0).createServer();

    List<String> lines = Files.readAllLines(Paths.get(args[0]), StandardCharsets.UTF_8);
    System.out.println(lines);


    server.setUpServer();
  }

  public static void printUsage() {
    System.out.println("Simulation and modelling coursework 1:\n"
        + "How to use:"
        + "[csv] : csv for file containing FunctionID_f, AvgServiceTimeMilliseconds, Invocations30Days\n"
        + "        (by default this is trace-final.csv, did you mean this?)");
  }
}
