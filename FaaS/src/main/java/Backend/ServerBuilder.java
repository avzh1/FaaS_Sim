package Backend;

public class ServerBuilder {

  private int memoryCapacity = 40;
  private int loadedFunctionMemory = 1;
  private double lambda_f;
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
    return this;
  }

  public ServerBuilder setAlpha(double alpha) {
    this.alpha = alpha;
    return this;
  }

  public Server createServer() {
    return new Server(memoryCapacity, loadedFunctionMemory, lambda_f, alpha);
  }
}