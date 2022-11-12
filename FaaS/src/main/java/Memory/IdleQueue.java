package Memory;

import static FunctionAsAService.Service.newIdleService;
import static Memory.MemoryException.MEMORY_CLASH;
import static Memory.MemoryException.MEMORY_UNDERFLOW;

import FunctionAsAService.Function;
import FunctionAsAService.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

class IdleQueue {

  private final Map<Integer, Service> idleServices = new HashMap<>();
  private final TreeSet<Service> ordering = new TreeSet<>();


  public void add(Function function) throws MemoryException {
    if (idleServices.containsKey(function.getFunctionID())) {
      throw MEMORY_CLASH;
    }
    Service idleService = newIdleService(function);
    idleServices.put(function.getFunctionID(), idleService);
    ordering.add(idleService);
  }

  public int size() {
    return idleServices.size();
  }

  public boolean contains(int functionId) {
    return idleServices.containsKey(functionId);
  }

  public Function peek() {
    return ordering.first().getFunction();
  }

  @SuppressWarnings("all") // getFunction() may produce Nullpointer fixed with checking size() == 0
  public Function pop() throws MemoryException {
    if (size() == 0) {
      throw MEMORY_UNDERFLOW;
    }
    Function top = ordering.pollFirst().getFunction();
    idleServices.remove(top.getFunctionID());
    return top;
  }

  public Function remove(int functionID) {
    Service service = idleServices.remove(functionID);
    ordering.remove(service);
    return service.getFunction();
  }
}
