package Backend.Memory;

public class ServiceException extends Exception {

  public final static ServiceException SERVICE_PROMOTION = new ServiceException(
      "Attempted to promote a service that cannot be promoted");

  public ServiceException(String message) {
    super(message);
  }
}

