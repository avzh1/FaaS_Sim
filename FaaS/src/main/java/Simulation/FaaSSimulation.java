package Simulation;

import FunctionAsAService.Function;
import FunctionAsAService.Memory.Memory;
import Simulation.Event.Event;
import Simulation.Event.Request;
import java.util.Set;

/**
 * My class for completing this simulation task
 */
public class FaaSSimulation extends Sim {

  // suffers an exponentially distributed overhead with mean 1/coldStart before being loaded in
  // memory
  private static final double coldStart = 0.5; // 1 / seconds

  // first event
  private Event arrivalEvent;

  private final Memory memory;
  private final Set<Function> functions;

  public FaaSSimulation(Memory memory, Set<Function> functions) {
    this.memory = memory;
    this.functions = functions;
  }

  public Memory getMemory() {
    return memory;
  }

  public void runSim() {
    arrivalEvent = new Request(5, functions.stream().findFirst().get(), this);
    schedule(arrivalEvent);
    go();
  }

  @Override
  public boolean stop() {
    return false;
  }

  @Override
  public void resetMeasures() {

  }
}
