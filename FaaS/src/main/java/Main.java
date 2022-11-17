import static Simulation.FaaSSimulation.confidenceIntervalToString;
import static Simulation.FaaSSimulationBuilder.createFaaSSimBuilder;

import FunctionAsAService.Function;
import Simulation.FaaSSimulation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

/**
 * Starts a server with args[0] = path to csv of the form
 * FunctionID_f,AvgServiceTimeMilliseconds,Invocations30Days
 */
public class Main {

  private static final int M = 40; // 4000 MB (4GB)

  private static final File traceCSV = new File("trace-final.csv");
  private static final File trace2Fs = new File("trace-2-fs.csv");
  private static final File pathToObservationFile = new File(
      traceCSV.getAbsoluteFile().getParentFile(),
      "simulation-observation-results.csv");

  public static void main(String[] args) throws IOException {
    // Q1.a
//    System.out.println("Q1.a");
//    FaaSSimulation sim = runTrackedSimulation(traceCSV, 40, 30 * 24 * 60 * 60, 60 * 60,
//        pathToObservationFile);
//    System.out.println(sim.getOverallSystemStatistics());
//
//    // Save Statistics from Q1 to file
//    saveSimulationStatistics(sim);

    // Q1.b
//    System.out.println("Q1.b");
//    System.out.print("The smallest value of M for a cold start less than 5%: ");
//    System.out.println(confidenceIntervalToString(binarySearchSmallestM(0.05)));

    // Q2.d
    System.out.println("Q2.d");
    FaaSSimulation sim2fs = runStandardSimulation(trace2Fs, 1, 24 * 60 * 60 * 33);
    printFunctionStatsByID(sim2fs, 1);
    printFunctionStatsByID(sim2fs, 2);
    System.out.println(
        "ColdRatio: " + confidenceIntervalToString(sim2fs.getColdRatioConfidenceInterval()));
    System.out.println(
        "LossRate: " + confidenceIntervalToString(sim2fs.getLossConfidenceInterval()));
  }

  /**
   * Prints function statistics relevant to Q2.d, assumes the function is present
   */
  private static void printFunctionStatsByID(FaaSSimulation sim2fs, int id) {
    Function f = sim2fs.getFunctions().stream()
        .filter((func) -> func.getFunctionID() == id)
        .toList().get(0);

    System.out.println("l_1: " + f.getArrivalRate());
    System.out.println("m_1: " + f.getServiceRate());
  }

  /**
   * @param desiredGreatestColdStart a percentage from 0 <= x <= 1 indicating the greatest cold
   *                                 start ratio you wish to observe
   * @return the smallest value of M such that the cold start is maximised but stays below the
   * colsStartRatio input
   */
  private static double[] binarySearchSmallestM(double desiredGreatestColdStart)
      throws IOException {
    int medianCapacity = 0;
    int low = 0;
    int high = 10862;

    List<Integer> memories = new LinkedList<>();

    while (low <= high) {
      medianCapacity = low + ((high - low) / 2);

      FaaSSimulation sim = runStandardSimulation(traceCSV, medianCapacity, 60 * 60 * 24);
      double[] interval = sim.getColdRatioConfidenceInterval();
      double C_ratio = interval[1];

      // check if C_ratio falls in the confidence interval we calculated
      if (interval[0] <= desiredGreatestColdStart && desiredGreatestColdStart <= interval[2]) {
        memories.add(medianCapacity);
      }

//      System.out.println(memories);

//      System.out.println(medianCapacity + ": " + C_ratio);

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
    return new double[]{
        memories.stream().min(Integer::compareTo).get(),
        medianCapacity,
        memories.stream().max(Integer::compareTo).get()};
  }

  /**
   * Runs a standard simulation given a maximum capacity of the memory of a server
   */
  private static FaaSSimulation runStandardSimulation(File functionTrace, int maximumCapacity,
      int simulationTime) throws IOException {
    // Create a new simulation
    FaaSSimulation sim = createFaaSSimBuilder()
        .withFunctionsFromCSV(functionTrace)
        .withMemoryCapacity(maximumCapacity)
        .withFullIdleMemory() // A6
        .withSimulationTimeDuration(simulationTime)
        .createFaaSSimulation();

    // Run the simulation
    sim.runSim();

    return sim;
  }

  private static FaaSSimulation runStandardSimulation(File functionTrace, int maximumCapacity,
      int simulationTime,
      double warmUpTime) throws IOException {
    // Create a new simulation
    FaaSSimulation sim = createFaaSSimBuilder()
        .withFunctionsFromCSV(functionTrace)
        .withMemoryCapacity(maximumCapacity)
        .withFullIdleMemory() // A6
        .withSimulationTimeDuration(simulationTime)
        .withWarmUpPeriod(warmUpTime)
        .createFaaSSimulation();

    // Run the simulation
    sim.runSim();

    return sim;
  }

  /**
   * runs a tracked simulation which, at each `observationInterval` records the current state of the
   * simulation and finally saves it to some output file `observationOutput`.
   *
   * @return
   */
  private static FaaSSimulation runTrackedSimulation(File functionTrace, int maximumCapacity,
      int simulationTime, int observationInterval, File observationOutput) throws IOException {
    // Create a new simulation
    FaaSSimulation sim = createFaaSSimBuilder()
        .withFunctionsFromCSV(functionTrace)
        .withMemoryCapacity(maximumCapacity)
        .withFullIdleMemory()
        .withSimulationTimeDuration(simulationTime)
        .withObservationInterval(observationInterval)
        .withObservationLogFile(observationOutput)
        .createFaaSSimulation();

    // Run the simulation
    sim.runSim();

    return sim;
  }

  private static void printToFile(File file, String content) throws IOException {
    Files.writeString(file.toPath(), content);
  }

  /**
   * Does admin work in saving final statistics for each functions of a simulation to a file in the
   * same directory as the input trace-final.csv
   */
  private static void saveSimulationStatistics(FaaSSimulation sim) throws IOException {
    // Collect CSV version (to not overwrite old CSV)
    File version_file = new File("version.txt");
    int CSV_version =
        Integer.parseInt(new String(Files.readAllBytes(version_file.toPath()))) + 1;

    // Collect Statistics into String
    String functionStatistics = sim.getFunctionStatistics();

    printToFile(version_file, String.valueOf(CSV_version));
    File pathToFunctionResultsCSV = new File(traceCSV.getAbsoluteFile().getParentFile(),
        "trace-function-results" + CSV_version + ".csv");
    printToFile(pathToFunctionResultsCSV, functionStatistics);
  }
}
