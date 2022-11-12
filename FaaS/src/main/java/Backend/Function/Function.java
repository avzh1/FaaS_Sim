package Backend.Function;

import Backend.Server.Server;

public class Function implements Event {

  /* Metadata from CSV */
  private final int FunctionID;
  private final int AvgServiceTimeMilliseconds;
  private final int Invocations30Days;

  /* Arrival of requests for this function */
  private double lambda_f;

  /* Simulation data */
  private final Server server;
  private int evocations;
  private int scheduledRequests;

  public Function(int functionID, int avgServiceTimeMilliseconds, int invocations30Days,
      Server server) {
    FunctionID = functionID;
    AvgServiceTimeMilliseconds = avgServiceTimeMilliseconds;
    Invocations30Days = invocations30Days;
    this.server = server;
  }

  public boolean isLoaded() {
    return isIdle() || isActive();
  }

  public boolean isIdle() {
    return server.isIdle(this.FunctionID);
  }

  public boolean isActive() {
    return server.isActive(this.FunctionID);
  }

  @Override
  public void request() {
    evocations++;
    // A2: If a request for function f is received while that function is serving another request,
    // then the incoming request is lost
    if (isActive()) {
      return;
    }
    // A3: During the cold start period for function f, incoming requests to f other than the one
    // that triggered the cold start are lost. The request that triggered the cost start can begin
    // service only after the cold start period ends

    // A4: A function f cannot be deallocated from memory during its cold start. The earliest time
    // at which it can be deallocated is right after it serves the first jon, if f is idle then.

    // A5: The CPU capacity is over provisioned and contention is negligible, os that any request to
    // function f either (i) receives its intended service time, (ii) receives the service time plus
    // the cold start time, or (iii it is lost, based on the rules above
  }


}
