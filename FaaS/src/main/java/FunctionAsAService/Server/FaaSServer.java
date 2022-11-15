package FunctionAsAService.Server;

import FunctionAsAService.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FaaSServer class has three partitions of used memory: active, loading and idle.
 * <p>
 * As an Assumption, all functions take the same amount of space in memory, we assume that memory
 * will be an integer value denoting the number of Functions we can store. E.g. if each function
 * requires 20MB and Max memory is 200MB then maximumCapacity will be 10 Functions.
 * <p>
 * A1: If a request arrives for a function f and there is not enough spare memory to load it, the
 * function g that is already loaded in memory and has been idle the longest is instantaneously
 * deallocated and its memory space immediately allocated to f.
 */
public class FaaSServer {

  private final Map<Integer, Function> active = new HashMap<Integer, Function>();
  private final Map<Integer, Function> loading = new HashMap<Integer, Function>();
  private final QueueMap<Integer, Function> idle = new QueueMap<Integer, Function>();

  private final int maximumCapacity;

  /**
   * @param maximumCapacity Maximum capacity of the memory
   */
  public FaaSServer(int maximumCapacity) {
    this.maximumCapacity = maximumCapacity;
  }

  /**
   * A6: To simplify the simulation you are advised to start the FaaS with the first M functions in
   * memory in the idle state and the remainder in the unloaded (not in memory) state - this means
   * that there will be precisely M functions in memory at all times, so you will not have to model
   * unused memory that has yet to be loaded/initialised.
   * <p>
   * The order of these functions doesn't matter as after the initialisation bias is over the state
   * won't be dependent of the state it started in.
   */
  public void fillMemory(List<Function> functions) {
    for (Function f : functions) {
      if (isFull()) {
        break;
      }
      enqueueIdle(f);
    }
  }

  /**
   * @return current of the memory
   */
  public int size() {
    return active.size() + idle.size() + loading.size();
  }

  /**
   * @return True if memory can still accept more Functions, False otherwise
   */
  public boolean isFull() {
    return maximumCapacity <= size();
  }

  /**
   * Adds a new Function to the unordered collection of Active Services
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueActive(Function function) {
    canAddToMemory(function);
    active.put(function.getFunctionID(), function);
  }

  /**
   * Adds a new Function to the ordered collection of Idle Services
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueIdle(Function function) {
    canAddToMemory(function);
    idle.put(function.getFunctionID(), function);
  }

  /**
   * Adds a new Function to the unordered collection of Loading Services
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueLoading(Function function) {
    canAddToMemory(function);
    loading.put(function.getFunctionID(), function);
  }

  /**
   * @return returns if a given functionID is currently busy/active
   */
  public boolean isActive(int functionID) {
    return active.containsKey(functionID);
  }

  /**
   * @return returns if a given functionID is currently idle/inactive
   */
  public boolean isIdle(int functionID) {
    return idle.containsKey(functionID);
  }

  /**
   * @return returns if a given functionID is currently loading into memory
   */
  public boolean isLoading(int functionID) {
    return loading.containsKey(functionID);
  }

  /**
   * @return returns if a given functionID is not in memory
   */
  public boolean isUnreserved(int functionID) {
    return !(isActive(functionID) || isLoading(functionID) || isIdle(functionID));
  }

  /**
   * Promotes the function to the next level of Status.
   * <p>
   * Loading -> Active
   * <p>
   * Idle -> Active
   */
  public void promote(Function function) {
    // does this service exist in memory?
    if (isUnreserved(function.getFunctionID())) {
      throw MemoryException.MEMORY_MISSING;
    }

    if (isLoading(function.getFunctionID())) {
      // move to active list
      active.put(function.getFunctionID(), loading.remove(function.getFunctionID()));
    }

    if (isIdle(function.getFunctionID())) {
      // move to active list
      active.put(function.getFunctionID(), idle.remove(function.getFunctionID()));
    }
  }

  /**
   * Given a function demotes the function one level down the memory partition hierarchy.
   * <p>
   * Active -> Idle
   * <p>
   * Idle -> Unreserved
   * <p>
   * Loading -> throws exception
   */
  public void demote(Function function) {
    // does this service exist in memory?
    if (isUnreserved(function.getFunctionID())) {
      throw MemoryException.MEMORY_MISSING;
    }

    // is this function loading?
    if (isLoading(function.getFunctionID())) {
      throw MemoryException.MEMORY_BUSY;
    }

    if (isActive(function.getFunctionID())) {
      // move function to idle memory
      idle.put(function.getFunctionID(), active.remove(function.getFunctionID()));
    } else if (isIdle(function.getFunctionID())) {
      // remove from memory
      idle.remove(function.getFunctionID());
    }

  }

  /**
   * @param function returns true iff the function isn't in memory and there is enough space to add
   *                 it
   */
  private void canAddToMemory(Function function) {
    if (size() >= maximumCapacity) {
      throw MemoryException.MEMORY_OVERFLOW;
    }
    if (!isUnreserved(function.getFunctionID())) {
      throw MemoryException.MEMORY_CLASH;
    }
  }

  /**
   * In the event that memory is full
   *
   * @return true if there is a service we can replace, false otherwise
   */
  public boolean canEvict() {
    return idle.size() > 0;
  }

  /**
   * @return the function that has been idle for the longest in memory if there is one. Otherwise
   * throws a memory exception
   */
  public Function evict() {
    if (!canEvict()) {
      throw MemoryException.MEMORY_BUSY;
    }
    // evict the oldest idle service
    return idle.pop();
  }

  /**
   * @return string representation of the memory status
   */
  @Override
  public String toString() {
    return "[" + size() + " - " +
        "(A:" + active.size() +
        ",I:" + idle.size() +
        ",L:" + loading.size() + ")/"
        + maximumCapacity + "]\n";
  }
}
