package Simulation.Event;

import FunctionAsAService.Function;
import Simulation.FaaSSimulation;

/**
 * Completion events are triggered when we complete a function request. This instantaneously demotes
 * the function into idle memory.
 */
public class Completion extends FaaSEvent {

  public Completion(double invokeTime, Function function, FaaSSimulation simulation) {
    super(invokeTime, function, simulation);
  }

  @Override
  public void invoke() {
    simulation.getMemory().demote(function);
    this.function.logNewCompletion();
  }
}
