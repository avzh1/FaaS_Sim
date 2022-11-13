package FunctionAsAService;

public class Function {

  private static final int Mf = 1; // 100 MB

  /* Metadata from CSV */
  private final int functionID; // ID
  // Assume that the service times for function f are exponentially distributed with rate parameter
  // 1/Sf (i.e. mean Sf ).
  private final double avgServiceTimeSeconds; // seconds / request
  private final int invocations30Days; // requests in 30 days

  /* Arrival of requests for this function */
  private final double lambda_f; // requests / second

  // Simulation variables
  private int requests = 0;
  private int invocations = 0;
  private int coldStarts = 0;
  private int promotions = 0;
  private int completions = 0;
  private int rejections = 0;

  public Function(int functionID, double avgServiceTimeMilliseconds, int invocations30Days) {
    this.functionID = functionID;
    this.avgServiceTimeSeconds = convertToSeconds(avgServiceTimeMilliseconds);
    this.invocations30Days = invocations30Days;
    lambda_f = calculateLambda();
  }

  private double convertToSeconds(double avgServiceTimeMilliseconds) {
    // milliseconds / job -> seconds / job means diving by 10^3
    return avgServiceTimeMilliseconds * Math.pow(10, -3);
  }

  /**
   * Assumes that inter-arrival times for each function are exponentially distributed
   *
   * @return arrival rate of requests to function f (requests/second).
   */
  private double calculateLambda() {
    // Given the invocations in 30 days metric we can find the sample mean of invocations per second
    int secondsIn30Days = 30 * 24 * 60 * 60;
    return ((double) invocations30Days) / secondsIn30Days;
  }

  public int getFunctionID() {
    return functionID;
  }

  public double getAvgServiceTimeSeconds() {
    return avgServiceTimeSeconds;
  }

  public int getInvocations30Days() {
    return invocations30Days;
  }

  public double getLambda_f() {
    return lambda_f;
  }

  public int getMf() {
    return Mf;
  }

  public void logNewRequest() {
    this.requests++;
  }

  public void logNewInvocation() {
    this.invocations++;
  }

  public void logNewColdStart() {
    this.coldStarts++;
  }

  public void logNewPromotion() {
    this.promotions++;
  }

  public void logNewCompletion() {
    this.completions++;
  }

  public void logNewRejection() {
    rejections++;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ID: ").append(getFunctionID());
    return sb.toString();
  }

  public int getRequests() {
    return requests;
  }

  public int getInvocations() {
    return invocations;
  }

  public int getColdStarts() {
    return coldStarts;
  }

  public int getPromotions() {
    return promotions;
  }

  public int getCompletions() {
    return completions;
  }

  public int getRejectsion() {
    return rejections;
  }
}
