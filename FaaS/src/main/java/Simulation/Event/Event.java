package Simulation.Event;

/**
 * An abstract class for an event, allowing you to overwrite the invoke method.
 */

public abstract class Event {
  /* Event fields */
  private final double invokeTime;

  public Event(double invokeTime) {
    this.invokeTime = invokeTime;
  }

  public abstract void invoke();

  public double getInvokeTime() {
    return invokeTime;
  }
}
