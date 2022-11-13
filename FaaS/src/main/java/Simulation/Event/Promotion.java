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
    simulation.getMemory().promote(function);
    this.function.logNewPromotion();
    simulation.schedule(completion());
  }
}
