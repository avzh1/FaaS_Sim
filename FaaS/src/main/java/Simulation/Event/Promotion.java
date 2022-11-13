package Simulation.Event;

import FunctionAsAService.Function;
import Simulation.FaaSSimulation;

/**
 * A `Promotion` event is triggered by an event transferring a function from the Loading partition
 * into the Active partition. This in turn triggers a `Completion` event.
 */
public class Promotion extends FaaSEvent {

  public Promotion(double invokeTime, Function function, FaaSSimulation simulation) {
    super(invokeTime, function, simulation);
  }

  @Override
  public void invoke() {
    System.out.println(this + " Promotion");
    simulation.getMemory().promote(function);
    simulation.schedule(completion());
  }
}
