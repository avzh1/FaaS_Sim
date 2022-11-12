package FunctionAsAService;

public class Function {
  private static final int Mf = 1; // 100 MB

  /* Metadata from CSV */
  private final int FunctionID;
  private final int AvgServiceTimeMilliseconds;
  private final int Invocations30Days;

  /* Arrival of requests for this function */
  private final double lambda_f;


  public Function(int functionID, int avgServiceTimeMilliseconds, int invocations30Days) {
    FunctionID = functionID;
    AvgServiceTimeMilliseconds = avgServiceTimeMilliseconds;
    Invocations30Days = invocations30Days;
    lambda_f = calculateLambda();
  }

  /**
   * Assumes that inter-arrival times for each function are exponentially distributed
   *
   * @return arrival rate of requests to function f (requests/second).
   */
  private double calculateLambda() {
    return 0;
  }

  public int getFunctionID() {
    return FunctionID;
  }

  public int getAvgServiceTimeMilliseconds() {
    return AvgServiceTimeMilliseconds;
  }

  public int getInvocations30Days() {
    return Invocations30Days;
  }

  public double getLambda_f() {
    return lambda_f;
  }

  public int getMf() {
    return Mf;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ID: ").append(getFunctionID());
    return sb.toString();
  }
}
