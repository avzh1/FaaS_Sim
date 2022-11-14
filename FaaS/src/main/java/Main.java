import static Simulation.FaaSSimulationBuilder.createFaaSSimBuilder;

import Simulation.FaaSSimulation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Starts a server with args[0] = path to csv of the form
 * FunctionID_f,AvgServiceTimeMilliseconds,Invocations30Days
 */
public class Main {

  private static final int M = 40; // 4000 MB (4GB)

  private static FaaSSimulation sim;

  public static void main(String[] args) throws IOException {
    // input CSV
    File traceCSV = new File("trace-final.csv");

    binarySearchSmallestM(traceCSV, 0.05);

    // Save Statistics to file
    saveSimulationStatistics(traceCSV);
  }

  /**
   * @param desiredGreatestColdStart a percentage from 0 <= x <= 1 indicating the greatest cold
   *                                 start ratio you wish to observe
   * @return the smallest value of M such that the cold start is maximised but stays below the
   * colsStartRatio input
   */
  private static int binarySearchSmallestM(File path, double desiredGreatestColdStart)
      throws IOException {
    int medianCapacity = 0;
    int low = 0;
    int high = 10862;

    while (low <= high) {
      medianCapacity = low + ((high - low) / 2);

      // Create a new simulation
      sim = createFaaSSimBuilder()
          .withFunctionsFromCSV(path)
          .withMemoryCapacity(medianCapacity)
          .withFullIdleMemory() // A6
          .withSimulationTimeDuration(24 * 60 * 60) // run for 33 days
          .createFaaSSimulation();

      // Run the simulation
      sim.runSim();

      double C_ratio = (double) sim.getTotalColdStarts() / (double) sim.getTotalRequests();
      System.out.println(medianCapacity + ": " + C_ratio);

      // Perform binary search to decide which way to expand/decrease memory
      if (C_ratio > desiredGreatestColdStart) {
        low = medianCapacity + 1;
      } else if (C_ratio < desiredGreatestColdStart) {
        high = medianCapacity - 1;
      } else if (C_ratio == desiredGreatestColdStart) {
        break;
      }
    }

    // return least great memory capacity
    return medianCapacity;
  }

  private static void printToFile(File file, String content) throws IOException {
    Files.writeString(file.toPath(), content);
  }

  private static void saveSimulationStatistics(File pathToCSV) throws IOException {
    // Collect CSV version (to not overwrite old CSV)
    File version_file = new File("version.txt");
    int CSV_version =
        Integer.parseInt(new String(Files.readAllBytes(version_file.toPath()))) + 1;

    // Collect Statistics into String
    String functionStatistics = sim.getFunctionStatistics();

    printToFile(version_file, String.valueOf(CSV_version));
    File pathToFunctionResultsCSV = new File(pathToCSV.getAbsoluteFile().getParentFile(),
        "trace-function-results" + CSV_version + ".csv");
    printToFile(pathToFunctionResultsCSV, functionStatistics);
  }
}
