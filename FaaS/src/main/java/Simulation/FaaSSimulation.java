package Simulation;

import FunctionAsAService.Function;
import FunctionAsAService.Memory.Memory;
import Simulation.Event.Request;
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

  public FaaSSimulation(Memory memory, List<Function> functions) {
    this.memory = memory;
    this.functions = functions;
  }

  public Memory getMemory() {
    return memory;
  }

  public void runSim() {

    for (Function f : functions) {
      // for each function, trigger an initial burst of requests in some arbitrary order. This order
      // doesn't matter initially as it is part of the start-up window we don't care about
      schedule(new Request(0, f, this));
    }
    go();
  }

  @Override
  public boolean stop() {
    return numEvents > 1000000;
  }

  @Override
  public void resetMeasures() {

  }
}
