package Backend;

import static Backend.Server.ServerBuilder.makeServerBuilder;
import static junit.framework.TestCase.assertEquals;

import Backend.Server.Server;
import Backend.Server.ServerBuilderException;
import org.junit.Test;

public class ServerBuilderTest {

  @Test(expected = ServerBuilderException.class)
  public void serverBuilderFailsWhenNoLambdaSet() throws ServerBuilderException {
    Server s = makeServerBuilder().createServer();
  }

  @Test
  public void serverBuilderDefaultsToPredefinedValues() throws ServerBuilderException {
    Server s = makeServerBuilder().setLambda_f(2.0).createServer();
    assertEquals(0.5, s.getAlpha());
    assertEquals(2.0, s.getLambda_f());
    assertEquals(40, s.getM());
    assertEquals(1, s.getM_f());
  }

  @Test
  public void serverBuilderMakesAServerWithCustomArguments() throws ServerBuilderException {
    Server s = makeServerBuilder().setMemoryCapacity(100).setLambda_f(5.0).setAlpha(1)
        .setLoadedFunctionMemory(10).createServer();
    assertEquals(1.0, s.getAlpha());
    assertEquals(5.0, s.getLambda_f());
    assertEquals(100, s.getM());
    assertEquals(10, s.getM_f());
  }
}
