package Simulation;

import FunctionAsAService.Function;
import FunctionAsAService.Server.FaaSServer;
import Simulation.Event.Request;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;

/**
 * My class for completing this simulation task
 */
public class FaaSSimulation extends Sim {

  /* Loading functions suffer an exponentially distributed overhead */
  public static final double coldStartRate = 0.5; // 1 / second

  /* Simulation objects */
  private final FaaSServer server;
  private final List<Function> functions;

  /* Simulation trackers */
  private final double simulationTimeSeconds;
  private final double warmUpPeriod;
  private boolean warmUp = true;
  protected int numEvents = 0;

  /* Fields responsible for holding the tracked state of the server */
  private StringBuilder observations = new StringBuilder();
  private double timeSinceLastObservation = 0;
  private final double observationIntervals;
  private final File observationOutput; // file to output observation csv to

  protected FaaSSimulation(FaaSServer server, List<Function> functions,
      double simulationTimeSeconds, double warmUpPeriod, double observationIntervals,
      File observationOutput) {
    this.server = server;
    this.functions = functions;
    this.simulationTimeSeconds = simulationTimeSeconds;
    this.warmUpPeriod = warmUpPeriod;
    this.observationIntervals = observationIntervals;
    this.observationOutput = observationOutput;

    // Define the format of the observations
    observations.append(
        "ServerTime,TotalRequests,TotalColdStarts,TotalPromotions,TotalCompletions,TotalRejections\n");
  }

  /* Returns the server object */
  public FaaSServer getServer() {
    return server;
  }

  /**
   * Runs the simulation for `simulationTimeSeconds` seconds. Initially, schedules as many functions
   * into idle memory as possible. Then runs the simulation and optionally saves the output into a
   * csv
   */
  public void runSim() {
    // for each function, trigger an initial burst of requests in some arbitrary order. This order
    // doesn't matter initially as it is part of the start-up window we don't care about
    for (Function f : functions) {
      schedule(new Request(0, f, this));
    }

    // run simulation
    go();

    // save any observations to the output file (if specified)
    if (observationOutput != null) {
      try {
        Files.writeString(observationOutput.toPath(), observations.toString());
      } catch (IOException e) {
        System.err.println("WARNING: " + e);
      }
    }
  }

  @Override
  public boolean stop() {
    return time > simulationTimeSeconds;
  }

  @Override
  public void tryResetMeasure() {
    if (warmUp && time > warmUpPeriod) {
      warmUp = false;
      // reset all the measures
      functions.forEach(Function::resetMeasures);
      observations = new StringBuilder();
      timeSinceLastObservation = 0;
    }
  }

  @Override
  public void tryRecordMeasure() {
    // check if we should record the current server state
    if (time - timeSinceLastObservation > observationIntervals) {
      // update timeSinceLastObservation
      timeSinceLastObservation = time;

      // save the current server state
      observations.append(getCurrentSimulationState());
    }
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

  /**
   * @return returns an array with ret[0] = unbiased cold start, ret[1,2] = 90% confidence bounds
   */
  public double[] getColdRatioConfidenceInterval() {
    List<Double> coldStartRatiosPerFunction = new ArrayList<>(functions.size());
    for (Function f : functions) {
      if (f.getRequests() != 0) { // if the function was never called
        coldStartRatiosPerFunction.add((double) f.getColdStarts() / (double) f.getRequests());
      }
    }

    return calculateSampleStatistics(getBiasedColdStartRatio(), coldStartRatiosPerFunction);
  }

  /**
   * @param interval confidence range with lower bound at 0, mean at 1, and upper bound at 2
   */
  public static String confidenceIntervalToString(double[] interval) {
    return "( " + interval[0] + " <= " + interval[1] + " <= " + interval[2] + " )";
  }

  /**
   * @return returns a sample mean of the cold starts. For bounds consider
   * calculateUnbiasedColdStart
   */
  public double getBiasedColdStartRatio() {
    return (double) getTotalColdStarts() / (double) getTotalRequests();
  }

  /**
   * @return returns an array with ret[0] = unbiased loss rate, ret[1,2] = 90% confidence bounds
   */
  public double[] getLossConfidenceInterval() {
    List<Double> lossRatePerFunction = new ArrayList<>(functions.size());
    for (Function f : functions) {
      if (f.getRequests() != 0) {
        lossRatePerFunction.add((double) f.getRejections() / getSimulationTime());
      }
    }

    return calculateSampleStatistics(getBiasedLossRate(), lossRatePerFunction);
  }

  private double @NotNull [] calculateSampleStatistics(double sampleMean,
      List<Double> coldStartRatiosPerFunction) {
    double mseTotal = 0;
    for (double d : coldStartRatiosPerFunction) {
      mseTotal += Math.pow(sampleMean - d, 2);
    }

    double sampleVariance = mseTotal / (functions.size() - 1);

    double sampleSTD = Math.pow(sampleVariance, 0.5);

    // int degreesOfFreedom = functions.size() - 1;
    // for the sake of the coursework we'll assume that functions size will always be 10861 so deg
    // of freedom is 10860 ~ 10,000. For a constant confidence interval of 90%, we need (1 - 0.90) / 2
    // so student's table for ~10000 degrees of freedom and 0.05.

    double t_student = 1.960;

    return new double[]{
        sampleMean - t_student * sampleSTD / Math.sqrt(functions.size()),
        sampleMean,
        sampleMean + t_student * sampleSTD / Math.sqrt(functions.size())};
  }

  public double getBiasedLossRate() {
    return getTotalRejections() / getSimulationTime();
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

  public String getOverallSystemStatistics() {
    return "Simulation Ran for: " + getSimulationTime() + "\n"
        + "Total Requests: " + getTotalRequests() + "\n"
        + "Total ColdStarts: " + getTotalColdStarts() + "\n"
        + "Total Promotions: " + getTotalPromotions() + "\n"
        + "Total Completions: " + getTotalCompletions() + "\n"
        + "Total Rejections: " + getTotalRejections() + "\n"
        + "---------\n"

        // C_ratio: probability that a request incurs a cold start
        + "C_ratio: " + confidenceIntervalToString(getColdRatioConfidenceInterval()) + "\n"
        // L_rate: the rate at which requests are lost
        + "L_rate: " + confidenceIntervalToString(getLossConfidenceInterval()) + "\n";
  }

  public List<Function> getFunctions() {
    return functions;
  }

  public String getCurrentSimulationState() {
    return time + ","
        + getTotalRequests() + ","
        + getTotalColdStarts() + ","
        + getTotalPromotions() + ","
        + getTotalCompletions() + ","
        + getTotalRejections() + "\n";
  }
}
