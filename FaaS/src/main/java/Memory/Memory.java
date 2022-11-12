package Memory;

import static Memory.MemoryException.MEMORY_BUSY;
import static Memory.MemoryException.MEMORY_CLASH;
import static Memory.MemoryException.MEMORY_MISSING;
import static Memory.MemoryException.MEMORY_OVERFLOW;

import FunctionAsAService.Function;
import java.util.HashMap;
import java.util.Map;

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
}