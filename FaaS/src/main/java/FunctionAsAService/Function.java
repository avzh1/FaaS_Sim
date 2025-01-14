package FunctionAsAService;

public class Function {

  /* Metadata from CSV */
  private final int functionID; // ID
  private final double avgServiceRateSeconds; // requests / second
  private final int invocations30Days; // requests in 30 days

  /* Arrival of requests for this function */
  private final double arrivalRate; // requests / second

  /* Simulation tracking variables */
  private int requests = 0;
  private int coldStarts = 0;
  private int promotions = 0;
  private int completions = 0;
  private int rejections = 0;

  public Function(int functionID, double avgServiceTimeMilliseconds, int invocations30Days) {
    this.functionID = functionID;
    this.avgServiceRateSeconds = 1 / millisToSeconds(avgServiceTimeMilliseconds);
    this.invocations30Days = invocations30Days;
    this.arrivalRate = calculateArrivalRate();
  }

  private double millisToSeconds(double avgServiceTimeMilliseconds) {
    // milliseconds / job -> seconds / job means diving by 10^3
    return avgServiceTimeMilliseconds * Math.pow(10, -3);
  }

  /**
   * Assumes that inter-arrival times for each function are exponentially distributed
   *
   * @return arrival rate of requests to function f (requests/second).
   */
  private double calculateArrivalRate() {
    // Given the invocations in 30 days metric we can find the sample mean of invocations per second
    int secondsIn30Days = 30 * 24 * 60 * 60;
    return ((double) invocations30Days) / secondsIn30Days;
  }

  public int getFunctionID() {
    return functionID;
  }

  public double getServiceRate() {
    return avgServiceRateSeconds;
  }

  public int getInvocations30Days() {
    return invocations30Days;
  }

  public double getArrivalRate() {
    return arrivalRate;
  }

  /* Incrementing Simulation Tracking Variables */

  public void logNewRequest() {
    this.requests++;
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

  /* Fetching simulation tracking variables */

  public int getRequests() {
    return requests;
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

  public int getRejections() {
    return rejections;
  }

  @Override
  public String toString() {
    return "ID: " + getFunctionID();
  }

  public void resetMeasures() {
    requests = 0;
    coldStarts = 0;
    promotions = 0;
    completions = 0;
    rejections = 0;
  }
}
