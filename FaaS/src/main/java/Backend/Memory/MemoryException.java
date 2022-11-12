package Backend.Memory;

public class MemoryException extends Exception {

  public final static MemoryException MEMORY_CLASH = new MemoryException(
      "Attempted to reserve memory for a function that is already in memory");
  public final static MemoryException MEMORY_OVERFLOW = new MemoryException(
      "Attempted to reserve memory for a function when memory is at full capacity");
  public final static MemoryException MEMORY_MISSING = new MemoryException(
      "Attempted to perform an action on a function that is missing from memory");

  public MemoryException(String message) {
    super(message);
  }
}
