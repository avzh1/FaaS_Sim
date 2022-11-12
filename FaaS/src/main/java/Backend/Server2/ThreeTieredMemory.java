package Backend.Server2;

public interface ThreeTieredMemory {

  boolean contains(Function function);

  boolean load(Function function);

  void allocate(Function function);

  void suspend(Function function);

  void deallocate(Function function);

  int size();

  boolean isLoading(Function function);

  boolean isActive(Function function);

  boolean isIdle(Function function);

  int getMaxCapacity();
}
