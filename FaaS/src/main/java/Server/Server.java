package Server;

import FunctionAsAService.Function;
import Memory.Memory;
import Memory.MemoryException;
import java.util.Set;

/**
 * Initial skeleton for a server, subject to change.
 * <p>
 * MemoryCapacity records the number of whole functions we can store in memory assuming that all
 * functions require the same amount in memory
 */
public class Server {

  private final Memory memory;
  private final Set<Function> functions;

  public Server(int memoryCapacity, Set<Function> functions) {
    memory = new Memory(memoryCapacity);
    this.functions = functions;
    prepareServer();
    System.out.println(memory);
  }

  /**
   * A6: To simplify the simulation you are advised to start the FaaS with the first M function sin
   * memory in the idle state and the remainder in the unloaded (not in memory) state - this means
   * that there will be precisely M functions in memory at all times, so you will not have to model
   * unused memory that has yet to be loaded/initialised
   */
  private void prepareServer() {
    for (Function f : functions) {
      if (memory.isFull()) {
        break;
      }
      try {
        memory.enqueueIdle(f);
      } catch (MemoryException e) {
        throw new RuntimeException("Shouldn't Happen");
      }
    }
  }


}
