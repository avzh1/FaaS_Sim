package Backend.Server;

import java.util.HashSet;
import java.util.Set;

public class Server {

  // simulation variables
  private final double alpha;    // seconds^(-1) cold-start overhead parameter for exp(1/alpha)
  private final int m_f;      // 100s of MB memory occupation of a loaded function f (identical)
  private final int m;        // 100s of MB initial total memory available in the server
  private final double lambda_f; //  (request / second) for function f; arrival rate of requests

  // server book-keeping data-structures
  private final Set<Integer> activeFunctions; // Functions that are in memory and are active
  private final IdleFunctions idleFunctions; // Functions that are in memory and are inactive

  protected Server(int memoryCapacity, int loadedFunctionMemory, double lambda_f, double alpha) {
    this.alpha = alpha;
    this.m_f = loadedFunctionMemory;
    this.m = memoryCapacity;
    this.lambda_f = lambda_f;

    // from A0, no function is in memory
    activeFunctions = new HashSet<>(m);
    idleFunctions = new IdleFunctions(m);
  }

  // from A6, advised to start the FaaS with the first M functions in memory in the idle state
  // and the remained in unloaded (not in memory) state
  public void setUpServer() {

  }

  // from A1, if a request is sent for a function which isn't in memory, then attempt to evict
  // the oldest idle function. If no function is idle, lose the incoming request to f.
  public void evictOldestIdle() throws IdleFunctionException {
    if ( !idleFunctions.isEmpty() ) {
      // remove the oldest idle function
      idleFunctions.pollOldest();
    } // lose incoming request
  }

  // given a function identifier, checks whether the function is in memory
  public boolean isLoaded(int function_identifier) {
    return isIdle(function_identifier) || isActive(function_identifier);
  }

  // given a function identifier, checks whether the function is idle in memory
  public boolean isIdle(int function_identifier) {
    return idleFunctions.contains(function_identifier);
  }

  // given a function identifier, checks whether the function is active in memory
  public boolean isActive(int function_identifier) {
    return activeFunctions.contains(function_identifier);
  }

  public double getLambda_f() {
    return lambda_f;
  }

  public int getM_f() {
    return m_f;
  }

  public double getAlpha() {
    return alpha;
  }

  public int getM() {
    return m;
  }
}
