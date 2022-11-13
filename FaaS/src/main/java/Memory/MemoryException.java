package Memory;

public class MemoryException extends RuntimeException {

  public final static MemoryException MEMORY_CLASH = new MemoryException(
      "Attempted to reserve memory for a function that is already in memory");
  public final static MemoryException MEMORY_OVERFLOW = new MemoryException(
      "Attempted to reserve memory for a function when memory is at full capacity");
  public final static MemoryException MEMORY_UNDERFLOW = new MemoryException(
      "Attempted to perform an action on memory that is empty");
  public final static MemoryException MEMORY_MISSING = new MemoryException(
      "Attempted to perform an action on a function that is missing from memory");
  public final static MemoryException MEMORY_BUSY = new MemoryException(
      "Attempted to evict from memory when all functions are busy");

  public MemoryException(String message) {
    super(message);
  }
}
