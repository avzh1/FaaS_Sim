package Memory;

import static FunctionAsAService.Service.newActiveService;
import static FunctionAsAService.Service.newLoadingService;
import static FunctionAsAService.Service.newUnreservedService;
import static Memory.MemoryException.MEMORY_CLASH;
import static Memory.MemoryException.MEMORY_MISSING;
import static Memory.MemoryException.MEMORY_OVERFLOW;
import static Memory.Status.IDLE;

import FunctionAsAService.Function;
import FunctionAsAService.Service;
import FunctionAsAService.ServiceException;
import java.util.HashMap;
import java.util.Map;

public class Memory {

  Map<Integer, Service> memory = new HashMap<>();
  IdleQueue idleFunctions = new IdleQueue();
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
    return memory.size() + idleFunctions.size();
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
    idleFunctions.add(function);
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
    return idleFunctions.contains(functionID);
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

    Service service;
    if (isIdle(function.getFunctionID())) {
      // move function out from the queue of idle services
      service = new Service(idleFunctions.remove(function.getFunctionID()), IDLE, 0);
    } else {
      // it is loading
      service = memory.get(function.getFunctionID());
      assert (function == service.getFunction());
    }

    service.promote();
    if (service.getStatus() != IDLE) {
      // replace the function id with a new Service reference
      memory.put(service.getFunctionID(), service);
    }
  }

  private void canAddToMemory(Function function) throws MemoryException {
    if (size() >= maximumCapacity) {
      throw MEMORY_OVERFLOW;
    }
    if (memory.containsKey(function.getFunctionID())) {
      throw MEMORY_CLASH;
    }
  }


}
