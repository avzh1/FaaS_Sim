package Simulation.Event;

import FunctionAsAService.Function;
import FunctionAsAService.Server.FaaSServer;
import Simulation.FaaSSimulation;

/**
 * Base event. A new request comes at time t. Generally it will inspect the state of the system for
 * a given function and perform one of the following:
 * <li>IDLE: promote the function into active memory and schedule a `Completion` event for the
 * function after time t.</li>
 * <li>ACTIVE: the request is dropped. (A2)</li>
 * <li>LOADING: check if the incoming request is the same as the request that triggered the
 * cold-start. Otherwise, drop the event (A3) </li>
 * <li>UNRESERVED: check if it is possible to evict a function. If it isn't the request is dropped.
 * Otherwise we evict a function instantaneously and transfer our function into the loading stage.
 * We schedule a `Promotion` to active memory for function f which its-self schedules a `Completion`
 * event. </li>
 */
public class Request extends FaaSEvent {

  public Request(double invokeTime, Function function, FaaSSimulation simulation) {
    super(invokeTime, function, simulation);
  }

  @Override
  public void invoke() {
    simulation.countEvent();
    this.function.logNewRequest();

    FaaSServer memory = simulation.getServer();
    
    // go through cases as in JavaDoc above
    if (memory.isIdle(function.getFunctionID())) {
      memory.promote(function);
      simulation.schedule(completion());
    } else if (memory.isUnreserved(function.getFunctionID()) && memory.canEvict()) {
      memory.evict();
      memory.enqueueLoading(function);
      simulation.schedule(coldStart());
      this.function.logNewColdStart();
    } else {
      this.function.logNewRejection();
    }

    // The inter-arrival rate for an arrival of the function has come and gone. Therefore, schedule
    // another function call.
    simulation.schedule(request());
  }

  @Override
  public String toString() {
    return "REQUEST: " + super.toString();
  }
}
