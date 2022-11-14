package Simulation;

import FunctionAsAService.Function;
import FunctionAsAService.Memory.Memory;
import Simulation.Event.Request;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * My class for completing this simulation task
 */
public class FaaSSimulation extends Sim {

  // suffers an exponentially distributed overhead with mean 1/coldStart before being loaded in
  // memory
  public static final double coldStart = 0.5; // 1 / second

  private final Memory memory;
  private final List<Function> functions;
  private final double simulationTimeSeconds;
  private final double observationIntervals;
  protected int numEvents = 0;

  protected FaaSSimulation(Memory memory, List<Function> functions, double simulationTimeSeconds,
      double observationIntervals) {
    this.memory = memory;
    this.functions = functions;
    this.simulationTimeSeconds = simulationTimeSeconds;
    this.observationIntervals = observationIntervals;
  }

  public Memory getMemory() {
    return memory;
  }

  public void runSim() {
    // initialise the memory with a random sample of Functions
    Collections.shuffle(functions);

    for (Function f : functions) {
      // for each function, trigger an initial burst of requests in some arbitrary order. This order
      // doesn't matter initially as it is part of the start-up window we don't care about
      schedule(new Request(0, f, this));
    }
    go();
  }

  @Override
  public boolean stop() {
//    return numEvents > 1000000;
    return time > simulationTimeSeconds;
  }

  @Override
  public void resetMeasures() {

  }

  public void countEvent() {
    this.numEvents++;
  }

  /**
   * @return total number of requests the system rejected
   */
  public int getTotalRejections() {
    return functions
        .stream()
        .map(Function::getRejections)
        .reduce(0, Integer::sum);
  }

  /**
   * @return total number of requests the system completed
   */
  public int getTotalCompletions() {
    return functions
        .stream()
        .map(Function::getCompletions)
        .reduce(0, Integer::sum);
  }

  /**
   * @return total number of requests the system rejected
   */
  public int getTotalColdStarts() {
    return functions
        .stream()
        .map(Function::getColdStarts)
        .reduce(0, Integer::sum);
  }

  /**
   * @return total number of requests the system promoted
   */
  public int getTotalPromotions() {
    return functions
        .stream()
        .map(Function::getPromotions)
        .reduce(0, Integer::sum);
  }

  /**
   * @return total number of requests the system received
   */
  public int getTotalRequests() {
    return functions
        .stream()
        .map(Function::getRequests)
        .reduce(0, Integer::sum);
  }

  public String getFunctionStatistics() {
    StringBuilder sb = new StringBuilder();
    sb.append("FunctionID,Requests,ColdStarts,Promotions,Completions,Rejections\n");
    for (Function f : functions) {
      StringJoiner sj = new StringJoiner(",");
      sj.add(Integer.toString(f.getFunctionID()));
      sj.add(Integer.toString(f.getRequests()));
      sj.add(Integer.toString(f.getColdStarts()));
      sj.add(Integer.toString(f.getPromotions()));
      sj.add(Integer.toString(f.getCompletions()));
      sj.add(Integer.toString(f.getRejections()));
      sb.append(sj).append("\n");
    }
    return sb.toString();
  }

  public String getSystemStatistics() {
    StringBuilder sb = new StringBuilder();

    // Print time frame
    sb.append("Simulation Ran for: ").append(getSimulationTime()).append("\n");

    sb.append("Total Requests: ").append(getTotalRequests()).append("\n");
    sb.append("Total ColdStarts: ").append(getTotalColdStarts()).append("\n");
    sb.append("Total Promotions: ").append(getTotalPromotions()).append("\n");
    sb.append("Total Completions: ").append(getTotalCompletions()).append("\n");
    sb.append("Total Rejections: ").append(getTotalRejections()).append("\n");
    sb.append("---------\n");

    // C_ratio: probability that a request incurs a cold start
    double C_ratio = (double) getTotalColdStarts() / (double) getTotalRequests();
    sb.append("C_ratio: ").append(C_ratio).append("\n");
    // L_rate: the rate at which requests are lost
    double L_rate = getTotalRejections() / getSimulationTime();
    sb.append("L_rate: ").append(L_rate).append("\n");

    return sb.toString();
  }

}
