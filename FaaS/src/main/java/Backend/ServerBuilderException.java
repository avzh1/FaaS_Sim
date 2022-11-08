package Backend;

// Exception class for SeverBuilder function shenanigans. This case shouldn't be hit but is good
// as a sanity check during development.
public class ServerBuilderException extends Exception {

  public ServerBuilderException(String message) {
    super(message);
  }
}
