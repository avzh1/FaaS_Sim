package Simulation;

import FunctionAsAService.Function;
import FunctionAsAService.Memory.Memory;
import Simulation.Event.Request;
import java.util.Collections;
import java.util.List;

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
  protected int numEvents = 0;

  public FaaSSimulation(Memory memory, List<Function> functions, double simulationTimeSeconds) {
    this.memory = memory;
    this.functions = functions;
    this.simulationTimeSeconds = simulationTimeSeconds;
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
}