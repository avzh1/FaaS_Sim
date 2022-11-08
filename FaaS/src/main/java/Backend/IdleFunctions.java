package Backend;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

// Wrapper for two classes, one retaining the constant time lookup, and the other retaining
// the constant time lookup for the longest idle function in memory.
public class IdleFunctions {

  // int[0] represents the function id
  // int[1] represents the idle order
  private static final Comparator<int[]> idleOrderSorter = ((arr1, arr2) -> {
    int funcDiff = arr1[1] - arr2[1];
    return funcDiff == 0 ? arr1[0] - arr2[0] : funcDiff;
  });

  // can be thought of a pseudo 'time stamp', the smaller the number, the earlier the function
  // was marked as idle.
  private static int order = 0;

  // Datastructures used to sort and store functions
  private final TreeSet<int[]> idleFunctionOrder;
  private final Set<Integer> idleFunctions;

  public IdleFunctions(int initSize) {
    this.idleFunctionOrder = new TreeSet<>(idleOrderSorter);
    this.idleFunctions = new HashSet<>(initSize);
  }

  public IdleFunctions() {
    this(2); // choose some arbitrary size to start with
  }

  public int size() {
    return idleFunctions.size();
  }

  public boolean isEmpty() {
    return idleFunctions.isEmpty();
  }

  public boolean contains(int functionIdentifier) {
    return idleFunctions.contains(functionIdentifier);
  }

  public void add(int functionIdentifier) throws IdleFunctionException {
    // add function to hash set - O(1)
    if (!idleFunctions.add(functionIdentifier)) {
      throw new IdleFunctionException("Cannot add back a function if it is already idle");
    }
    // add function to tree set (for ordering on idle length). - O(log n)
    idleFunctionOrder.add(new int[]{functionIdentifier, order++});
  }

  public boolean remove(int functionIdentifier) throws IdleFunctionException {
    // remove function from hash set - O(1)
    if (!idleFunctions.remove(functionIdentifier)) {
      throw new IdleFunctionException(
          "Cannot remove an idle function as it isn't currently idle");
    }
    // remove function from tree set - O(n)
    idleFunctionOrder.removeIf(arr -> arr[0] == functionIdentifier);
    return true;
  }

  public int peekOldest() throws IdleFunctionException {
    if (idleFunctionOrder.isEmpty()) {
      // this case should never be hit due to assumption 6, the simulation is started with
      // idle functions in some order
      throw new IdleFunctionException("Cannot peak idle function - no functions idle");
    }
    return idleFunctionOrder.first()[0]; // O(1)
  }

  public int pollOldest() throws IdleFunctionException {
    if (idleFunctionOrder.isEmpty()) {
      // this case should never be hit due to assumption 6, the simulation is started with
      // idle functions in some order
      throw new IdleFunctionException("Cannot peak idle function - no functions idle");
    }
    return Objects.requireNonNull(idleFunctionOrder.pollFirst())[0];
  }
}

