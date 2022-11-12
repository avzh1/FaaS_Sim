package Backend.Server2;

import java.util.HashMap;
import java.util.Map;

public class ReservedMemory implements ThreeTieredMemory {

  private final int maxCapacity;
  private final Map<Integer, Function> loading;
  private final Map<Integer, Function> active;
  private final Map<Integer, Function> idle;

  public ReservedMemory(int maxCapacity) {
    this(maxCapacity, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  public ReservedMemory(int maxCapacity, Map<Integer, Function> loading,
      Map<Integer, Function> active, Map<Integer, Function> idle) {
    this.maxCapacity = maxCapacity;
    this.loading = loading;
    this.active = active;
    this.idle = idle;
  }

  @Override
  public boolean contains(Function function) {
    return isIdle(function) || isActive(function) || isLoading(function);
  }


  /**
   * Puts the function in the 'loading phase' of the memory.
   *
   * @param function Function object to load
   * @return false: function already in memory, memory is full.
   * <p>
   * true : function successfully 'loading'.
   */
  @Override
  public boolean load(Function function) {
    // check if the function is already being loaded (nothing more to do)
    if (isLoading(function)) {
      return true;
    }

    // check if the function is already in memory, cannot 'load' a function that's already loaded
    if (isActive(function) || isIdle(function)) {
      return false;
    }

    // check if we have space to load the function into memory
    if (size() <= maxCapacity) {
      loading.put(function.getFunctionID(), function);
      return true;
    }

    return false;
  }

  @Override
  public void allocate(Function function) {

  }

  @Override
  public void suspend(Function function) {

  }

  @Override
  public void deallocate(Function function) {

  }

  //
  @Override
  public int size() {
    return loading.size() + idle.size() + active.size();
  }

  // returns true if the function is being loaded into memory
  // it cannot be used, nor can it be deallocated
  @Override
  public boolean isLoading(Function function) {
    return loading.containsKey(function.getFunctionID());
  }

  // returns true if the function is busy serving a request
  @Override
  public boolean isActive(Function function) {
    return active.containsKey(function.getFunctionID());
  }

  // returns true if the Function is idle but in memory
  @Override
  public boolean isIdle(Function function) {
    return idle.containsKey(function.getFunctionID());
  }

  @Override
  public int getMaxCapacity() {
    return maxCapacity;
  }
}
