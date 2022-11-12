package Backend.Server;

public class ServerBuilder {

  private int memoryCapacity = 40;
  private int loadedFunctionMemory = 1;
  private double lambda_f;
  private boolean lambda_f_set = false;
  private double alpha = 0.5;

  public static ServerBuilder makeServerBuilder()
  {
    return new ServerBuilder();
  }

  public ServerBuilder setMemoryCapacity(int memoryCapacity) {
    this.memoryCapacity = memoryCapacity;
    return this;
  }

  public ServerBuilder setLoadedFunctionMemory(int loadedFunctionMemory) {
    this.loadedFunctionMemory = loadedFunctionMemory;
    return this;
  }

  public ServerBuilder setLambda_f(double lambda_f) {
    this.lambda_f = lambda_f;
    this.lambda_f_set = true;
    return this;
  }

  public ServerBuilder setAlpha(double alpha) {
    this.alpha = alpha;
    return this;
  }

  public Server createServer() throws ServerBuilderException {
    if ( !lambda_f_set )
    {
      throw new ServerBuilderException("Lambda_f field hasn't been set");
    }
    return new Server(memoryCapacity, loadedFunctionMemory, lambda_f, alpha);
  }
}

