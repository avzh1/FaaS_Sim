package Simulation;

import Simulation.Event.Event;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * My interpretation for a simulation class that can start, record events, and stop when required.
 * It also deals with bookkeeping events by scheduling them into a diary.
 */
public abstract class Sim {

  // Comparator for inserting elements into the queue based on time they were scheduled.
  private static final Comparator<Event> EVENT_COMPARATOR = Comparator.comparingDouble(
      Event::getInvokeTime);
  private final PriorityQueue<Event> diary;

  // Protected attributes visible to child classes
  protected double time = 0.0; // seconds

  public Sim() {
    diary = new PriorityQueue<>(EVENT_COMPARATOR);
  }

  /**
   * @return current time cursor the simulation is at
   */
  public double getTime() {
    return time;
  }

  /**
   * @param event Event to schedule inserted into a time sorted priority queue
   */
  public void schedule(Event event) {
    diary.add(event);
  }

  /**
   * @param event event to deschedule from diary, also deschedules all subsequent triggers this
   *              event created
   */
  public void deschedule(Event event) {
    // remove the event and try to event next event
    if (diary.remove(event) && event.getNextEvent() != null) {
      diary.remove(event.getNextEvent());
    }
  }

  /**
   * Function for executing the simulation while the event queue is not empty and a stopping
   * condition isn't true.
   */
  public void go() {
//    double previousEventTime = 0;
    while (!diary.isEmpty() && !this.stop()) {
      Event topEvent = diary.poll();
      assert topEvent != null;
//      if (previousEventTime > topEvent.getInvokeTime()) {
//        System.err.println("Attempted to schedule something in the past!");
//      }
//      previousEventTime = topEvent.getInvokeTime();
//      System.out.println(topEvent);

      time = topEvent.getInvokeTime();
      if (!this.stop()) {
        topEvent.invoke();
      }
    }
  }

  /**
   * @return current simulation time
   */
  public double getSimulationTime() {
    return time;
  }

  /**
   * Stopping condition for simulation
   */
  public abstract boolean stop();

  public abstract void resetMeasures();
}
