package Backend.Server2;

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
}
