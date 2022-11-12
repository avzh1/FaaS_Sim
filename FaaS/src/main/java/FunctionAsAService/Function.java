package FunctionAsAService;

public class Function {

  /* Metadata from CSV */
  private final int FunctionID;
  private final int AvgServiceTimeMilliseconds;
  private final int Invocations30Days;

  /* Arrival of requests for this function */
  private double lambda_f;


  public Function(int functionID, int avgServiceTimeMilliseconds, int invocations30Days) {
    FunctionID = functionID;
    AvgServiceTimeMilliseconds = avgServiceTimeMilliseconds;
    Invocations30Days = invocations30Days;
    this.lambda_f = calculateLambda();
  }

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
}
