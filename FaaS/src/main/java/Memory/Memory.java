package Memory;

import static Memory.MemoryException.MEMORY_BUSY;
import static Memory.MemoryException.MEMORY_CLASH;
import static Memory.MemoryException.MEMORY_MISSING;
import static Memory.MemoryException.MEMORY_OVERFLOW;

import FunctionAsAService.Function;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory class has three partitions of used memory: active, loading and idle.
 * <p>
 * As an Assumption, all functions take the same amount of space in memory, we assume that memory
 * will be an integer value denoting the number of Functions we can store. E.g. if each function
 * requires 20MB and Max memory is 200MB then maximumCapacity will be 10 Functions.
 * <p>
 * A1: If a request arrives for a function f and there is not enough spare memory to load it, the
 * function g that is already loaded in memory and has been idle the longest is instantaneously
 * deallocated and its memory space immediately allocated to f.
 */
public class Memory {

  Map<Integer, Function> active = new HashMap<Integer, Function>();
  Map<Integer, Function> loading = new HashMap<Integer, Function>();
  QueueMap<Integer, Function> idle = new QueueMap<Integer, Function>();

  private final int maximumCapacity;

  /**
   * @param maximumCapacity Maximum capacity of the memory
   */
  public Memory(int maximumCapacity) {
    this.maximumCapacity = maximumCapacity;
  }

  /**
   * @return size of the memory
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
  public void enqueueActive(Function function) throws MemoryException {
    canAddToMemory(function);
    active.put(function.getFunctionID(), function);
  }

  /**
   * Adds a new Function to the ordered collection of Idle Services
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueIdle(Function function) throws MemoryException {
    canAddToMemory(function);
    idle.put(function.getFunctionID(), function);
  }

  /**
   * Adds a new Function to the unordered collection of Loading Services
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueLoading(Function function) throws MemoryException {
    canAddToMemory(function);
    loading.put(function.getFunctionID(), function);
  }

  public boolean isActive(int functionID) {
    return active.containsKey(functionID);
  }

  public boolean isIdle(int functionID) {
    return idle.containsKey(functionID);
  }

  public boolean isLoading(int functionID) {
    return loading.containsKey(functionID);
  }

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
  public void promote(Function function) throws MemoryException {
    // does this service exist in memory?
    if (isUnreserved(function.getFunctionID())) {
      throw MEMORY_MISSING;
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

  private void canAddToMemory(Function function) throws MemoryException {
    if (size() >= maximumCapacity) {
      throw MEMORY_OVERFLOW;
    }
    if (!isUnreserved(function.getFunctionID())) {
      throw MEMORY_CLASH;
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

  public Function evict() throws MemoryException {
    if (!canEvict()) {
      throw MEMORY_BUSY;
    }
    // evict the oldest idle service
    return idle.pop();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("SIZE: [").append(size()).append("/").append(maximumCapacity).append("]")
        .append("\n");
    sb.append("ACTIVE ==============================").append("\n");
    for (Function f : active.values()) {
      sb.append(f).append("\n");
    }
    // active.forEach((id, f) -> sb.append(f.getFunctionID()).append("\n"));
    sb.append("IDLE ================================").append("\n");
    for (Function f : idle.values()) {
      sb.append(f).append("\n");
    }
    // idle.forEach((id, f) -> sb.append(f.getFunctionID()).append("\n"));
    sb.append("LOADING =============================").append("\n");
    for (Function f : loading.values()) {
      sb.append(f).append("\n");
    }
//    loading.forEach((id, f) -> sb.append(f.getFunctionID()).append("\n"));
    return sb.toString();
  }
}
