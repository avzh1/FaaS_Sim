package Simulation.Event;

/**
 * An abstract class for an event, allowing you to overwrite the invoke method.
 * <p>
 * This class can further track any subsequent event that this event may trigger. For now, it is
 * assumed that each event may only trigger one other event. This may be useful in the future for
 * tracking events that need to be rescheduled.
 */

public abstract class Event {

  // global static variable handing out unique IDs.
  private static int uniqueID = Integer.MIN_VALUE;

  // Event fields
  private final int id;
  private final double invokeTime;

  private Event nextEvent;

  public Event(double invokeTime) {
    this.invokeTime = invokeTime;
    this.id = ++uniqueID;
  }

  public abstract void invoke();

  public double getInvokeTime() {
    return invokeTime;
  }

  public int getId() {
    return id;
  }

  public Event getNextEvent() {
    return nextEvent;
  }

  public void setNextEvent(Event event) {
    this.nextEvent = event;
  }
}
