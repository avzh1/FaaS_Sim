package Backend.Memory;

import static Backend.Memory.MemoryException.MEMORY_CLASH;
import static Backend.Memory.MemoryException.MEMORY_MISSING;
import static Backend.Memory.MemoryException.MEMORY_OVERFLOW;
import static Backend.Memory.Service.newActiveService;
import static Backend.Memory.Service.newIdleService;
import static Backend.Memory.Service.newLoadingService;
import static Backend.Memory.Service.newUnreservedService;

import Backend.Function;
import java.util.HashMap;
import java.util.Map;

public class Memory {

  Map<Integer, Service> memory = new HashMap<>();
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
    return memory.size();
  }

  /**
   * Adds a new Service for a function with status set to Active
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueActive(Function function) throws MemoryException {
    canAddToMemory(function);
    memory.put(function.getFunctionID(), newActiveService(function));
  }

  /**
   * Adds a new Service for a function with status set to Idle
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueIdle(Function function) throws MemoryException {
    canAddToMemory(function);
    memory.put(function.getFunctionID(), newIdleService(function));
  }

  /**
   * Adds a new Service for a function with status set to Loading
   *
   * @throws MemoryException if the memory already contains the function in memory or the maximum
   *                         capacity is exceeded
   */
  public void enqueueLoading(Function function) throws MemoryException {
    canAddToMemory(function);
    memory.put(function.getFunctionID(), newLoadingService(function));
  }

  public boolean isActive(int functionID) {
    return memory.getOrDefault(functionID, newUnreservedService()).getStatus() == Status.ACTIVE;
  }

  public boolean isIdle(int functionID) {
    return memory.getOrDefault(functionID, newUnreservedService()).getStatus() == Status.IDLE;
  }

  public boolean isLoading(int functionID) {
    return memory.getOrDefault(functionID, newUnreservedService()).getStatus() == Status.LOADING;
  }

  public boolean isUnreserved(int functionID) {
    return !(isActive(functionID) || isLoading(functionID) || isIdle(functionID));
  }

  /**
   * Promotes a function one level up in memory as specified in Service.promote()
   */
  public void promote(Function function) throws MemoryException, ServiceException {
    // does this service exist in memory?
    if (isUnreserved(function.getFunctionID())) {
      throw MEMORY_MISSING;
    }
    memory.get(function.getFunctionID()).promote();
  }

  private void canAddToMemory(Function function) throws MemoryException {
    if (memory.size() >= maximumCapacity) {
      throw MEMORY_OVERFLOW;
    }
    if (memory.containsKey(function.getFunctionID())) {
      throw MEMORY_CLASH;
    }
  }
}
