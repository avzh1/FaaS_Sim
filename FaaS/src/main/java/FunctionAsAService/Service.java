package FunctionAsAService;

import static Memory.Status.ACTIVE;
import static Memory.Status.LOADING;

import Memory.Status;

public class Service implements Comparable<Service> {

  private final Function function;
  private Status status;
  private int invocationOrder; // upperbound of Integer.MAX_INT isn't a problem as we can only have
  // up to Integer.MAX_INT idle services (bounded by maximumCapacity of memory)

  private static int serviceInvocationOrder = 0;

  private Service(Function function, Status status) {
    this(function, status, 0); // invocation doesn't matter
  }

  public Service(Function function, Status status, int invocationOrder) {
    this.function = function;
    this.status = status;
    this.invocationOrder = invocationOrder;
  }

  public Function getFunction() {
    return function;
  }

  public static Service newActiveService(Function function) {
    return new Service(function, Status.ACTIVE);
  }

  public static Service newIdleService(Function function) {
    return new Service(function, Status.IDLE, ++serviceInvocationOrder);
  }

  public static Service newLoadingService(Function function) {
    return new Service(function, LOADING);
  }

  public static Service newUnreservedService() {
    return new Service(null, Status.UNRESERVED);
  }

  /**
   * Promotes oneself to the next level of Status.
   * <p>
   * Loading -> Active
   * <p>
   * Idle -> Active
   */
  public void promote() throws ServiceException {
    switch (status) {
      case LOADING, IDLE -> this.status = ACTIVE;
      default -> throw ServiceException.SERVICE_PROMOTION;
    }
  }

  public Status getStatus() {
    return status;
  }

  public int getInvocationOrder() {
    return invocationOrder;
  }

  /**
   * Compares each service to each other based on virtual invocation order. If an object has the
   * same virtual invocation order, then compares via function_id.
   * <p>
   * services with larger invocationOrder parameters are considered to have happened later, you can
   * imagine it being a certain time along the time axis.
   *
   * @param service the object to be compared.
   * @return -ve: this was enqueued later (this.invok > service.invok)
   * <p>
   * 0: enqueued at the same time or invocationOrder doesn't matter
   * <p>
   * +ve: this was enqueued earlier (this.invok < service.invok)
   */
  @Override
  public int compareTo(Service service) {
    return (this.invocationOrder == service.invocationOrder)
        ? this.getFunction().getFunctionID() - service.getFunction().getFunctionID()
        : this.invocationOrder - service.invocationOrder;
  }

  public int getFunctionID() {
    return getFunction().getFunctionID();
  }
}
