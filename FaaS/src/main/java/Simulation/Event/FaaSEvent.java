package Simulation.Event;

import FunctionAsAService.Function;
import Simulation.FaaSSimulation;

public abstract class FaaSEvent extends Event {

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
    double coldStart = 0.5; // something to do with simulation.memory (constant for all)
    Promotion event = new Promotion(getInvokeTime() + coldStart, function, simulation);
    this.setNextEvent(event);
    return event;
  }

  /**
   * @return new Request event displaced by a sample of inter arrival rate for a particular function
   */
  public final Request request() {
    double interArrivalTime = 10.0;
    Request event = new Request(getInvokeTime() + interArrivalTime, function, simulation);
    this.setNextEvent(event);
    return event;
  }

  /**
   * @return new Promotion event displaced by a sample of average processing time for a function
   */
  public final Completion completion() {
    double completionTime = 10.0;
    Completion event = new Completion(getInvokeTime() + completionTime, function, simulation);
    this.setNextEvent(event);
    return event;
  }

  @Override
  public String toString() {
    return "EVENT [" + getInvokeTime() + "](" + function.getFunctionID() + ")";
  }
}
