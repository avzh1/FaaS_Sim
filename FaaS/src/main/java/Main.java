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
    // Set up objects for simulation
    File traceCSV = new File("trace-final.csv");

    // Create a new simulation
    sim = createFaaSSimBuilder()
        .withFunctionsFromCSV(traceCSV)
        .withMemoryCapacity(M)
        .withFullIdleMemory() // A6
        .withSimulationTimeDuration(33 * 24 * 60 * 60) // run for 33 days
        .withObservationInterval(12 * 60 * 60) // every half day record the current state
        .createFaaSSimulation();

    // run the simulation
    sim.runSim();

    // Save Statistics to file
    saveSimulationStatistics(traceCSV);
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
    String functionStatistics = sim.collectFunctionStatistics();

    printToFile(version_file, String.valueOf(CSV_version));
    File pathToFunctionResultsCSV = new File(pathToCSV.getAbsoluteFile().getParentFile(),
        "trace-function-results" + CSV_version + ".csv");
    printToFile(pathToFunctionResultsCSV, functionStatistics);

    // Print General Statistics to Console
    String systemStatistics = sim.collectSystemStatistics();
    System.out.println(systemStatistics);
  }
}
