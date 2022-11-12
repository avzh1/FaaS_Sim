package Backend.Memory;

import static Backend.Memory.ServiceException.SERVICE_PROMOTION;
import static Backend.Memory.Status.ACTIVE;
import static Backend.Memory.Status.LOADING;

import Backend.Function;

public class Service {

  private final Function function;
  private Status status;

  private Service(Function function, Status status) {
    this.function = function;
    this.status = status;
  }

  public Function getFunction() {
    return function;
  }

  public static Service newActiveService(Function function) {
    return new Service(function, Status.ACTIVE);
  }

  public static Service newIdleService(Function function) {
    return new Service(function, Status.IDLE);
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
      default -> throw SERVICE_PROMOTION;
    }
  }

  public Status getStatus() {
    return status;
  }
}
