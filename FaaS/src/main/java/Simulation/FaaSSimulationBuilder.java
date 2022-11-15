package Simulation;

import FunctionAsAService.Function;
import FunctionAsAService.Server.FaaSServer;
import FunctionAsAService.Server.MemoryException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for running the simulation.
 * <p>
 * Important to note, there are several routes to take with creating a FaaSSimulation.
 * <ol>
 * <li> present your own list of functions, or choose to populate it from an input CSV file.</li>
 * <li> present your own memory object, or choose to populate it from scratch with its constructor
 * parameters (memory) </li>
 * <li> you may choose to opt into populating the memory with idle functions before running the
 * simulation, although you need to make sure that functions and memory are not null beforehand</li>
 * <li>Optionally, you may choose to set the observation window. If left uncalled, the simulation
 * will start and observations will happen every Integer.MAX_INT (i.e. never)</li>
 * </ol>
 */
public class FaaSSimulationBuilder {

  private FaaSServer faaSServer = null;
  private List<Function> functions = null;
  private double simulationTimeSeconds = 0.0;
  private File observationOutput = null;
  private double observationIntervals = Integer.MAX_VALUE;

  public static FaaSSimulationBuilder createFaaSSimBuilder() {
    return new FaaSSimulationBuilder();
  }

  public FaaSSimulationBuilder withMemory(FaaSServer faaSServer) {
    this.faaSServer = faaSServer;
    return this;
  }

  public FaaSSimulationBuilder withMemoryCapacity(int capacity) {
    this.faaSServer = new FaaSServer(capacity);
    return this;
  }

  public FaaSSimulationBuilder withFunctions(List<Function> functions) {
    this.functions = functions;
    return this;
  }

  public FaaSSimulationBuilder withFunctionsFromCSV(File pathToCSV) throws IOException {
    this.functions = parseCSV(pathToCSV);
    return this;
  }

  public FaaSSimulationBuilder withFunctionsFromCSV(String pathToCSV) throws IOException {
    return withFunctionsFromCSV(new File(pathToCSV));
  }

  private static @NotNull List<Function> parseCSV(File fp) throws IOException {
    FileReader fr = new FileReader(fp);
    BufferedReader br = new BufferedReader(fr);
    List<Function> functions = new LinkedList<>();

    String line = br.readLine(); // ignore the heading (assume correct format)
    while ((line = br.readLine()) != null) {
      String[] split = line.split(","); // split the csv into columns
      Function f = new Function(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
          Integer.parseInt(split[2]));
      functions.add(f);
    }

    fr.close();
    return functions;
  }

  public FaaSSimulationBuilder withSimulationTimeDuration(double simulationTimeSeconds) {
    this.simulationTimeSeconds = simulationTimeSeconds;
    return this;
  }

  public FaaSSimulationBuilder withObservationInterval(double observationIntervals) {
    this.observationIntervals = observationIntervals;
    return this;
  }

  public FaaSSimulation createFaaSSimulation() {
    return new FaaSSimulation(faaSServer, functions, simulationTimeSeconds, observationIntervals,
        observationOutput);
  }

  public FaaSSimulationBuilder withFullIdleMemory() {
    if (faaSServer == null) {
      throw new MemoryException("Cannot fill memory with idle functions as memory is null");
    }
    if (functions == null) {
      throw new MemoryException("Cannot fill memory with idle functions as functions is null");
    }
    faaSServer.fillMemory(functions);
    return this;
  }

  public FaaSSimulationBuilder withObservationLogFile(File observationOutput) {
    this.observationOutput = observationOutput;
    return this;
  }
}