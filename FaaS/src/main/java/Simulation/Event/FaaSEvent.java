package Simulation.Event;

import FunctionAsAService.Function;
import Samplers.Sampler;
import Simulation.FaaSSimulation;

/**
 * Class more specified to our simulation. Holds the current FaaSSimulation class which deals with
 * memory and evictions, and also the function the event is handling.
 */
public abstract class FaaSEvent extends Event {

  // Server state variables
  protected final Function function;
  protected final FaaSSimulation simulation;

  public FaaSEvent(double invokeTime, Function function, FaaSSimulation simulation) {
    super(invokeTime);
    this.function = function;
    this.simulation = simulation;
  }

  /**
   * @return new Promotion event displaced by a sample of coldStart distribution
   */
  public final Promotion coldStart() {
    double coldStart = Sampler.Exponential(FaaSSimulation.coldStart);
    return new Promotion(getInvokeTime() + coldStart, function, simulation);
  }

  /**
   * @return new Request event displaced by a sample of inter arrival rate for a particular function
   */
  public final Request request() {
    double interArrivalTime = Sampler.Exponential(function.getArrivalRate());
    return new Request(getInvokeTime() + interArrivalTime, function, simulation);
  }

  /**
   * @return new Promotion event displaced by a sample of average processing time for a function
   */
  public final Completion completion() {
    double completionTime = Sampler.Exponential(1 / function.getAvgServiceTimeSeconds());
    return new Completion(getInvokeTime() + completionTime, function, simulation);
  }

  @Override
  public String toString() {
    return "[" + getInvokeTime() + "](" + function.getFunctionID() + ")";
  }
}
