package Backend.Server;

// Exception class for Idle function shenanigans, those being when you try to add a function
// back in as idle, when it is already idle. This case shouldn't be hit but is good as a sanity
// check during development.
public class IdleFunctionException extends Exception {

  public IdleFunctionException(String message) {
    super(message);
  }
}
