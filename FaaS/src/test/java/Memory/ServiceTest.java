package Memory;

import static FunctionAsAService.Service.newActiveService;
import static FunctionAsAService.Service.newIdleService;
import static FunctionAsAService.Service.newLoadingService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import FunctionAsAService.Function;
import FunctionAsAService.Service;
import org.junit.Test;

public class ServiceTest {

  private static final Function DUMMY_FUNCTION1 = new Function(1, 0, 0);
  private static final Function DUMMY_FUNCTION2 = new Function(2, 0, 0);
  private static final Function DUMMY_FUNCTION3 = new Function(3, 0, 0);

  @Test
  public void activeServicesHaveInvocationOrdersOf0() {
    Service newService1 = newActiveService(DUMMY_FUNCTION1);
    Service newService2 = newActiveService(DUMMY_FUNCTION2);
    Service newService3 = newActiveService(DUMMY_FUNCTION3);

    assertEquals(0, newService1.getInvocationOrder());
    assertEquals(0, newService2.getInvocationOrder());
    assertEquals(0, newService3.getInvocationOrder());
  }

  @Test
  public void loadingServicesHaveInvocationOrdersOf0() {
    Service newService1 = newLoadingService(DUMMY_FUNCTION1);
    Service newService2 = newLoadingService(DUMMY_FUNCTION2);
    Service newService3 = newLoadingService(DUMMY_FUNCTION3);

    assertEquals(0, newService1.getInvocationOrder());
    assertEquals(0, newService2.getInvocationOrder());
    assertEquals(0, newService3.getInvocationOrder());
  }

  @Test
  public void idleServicesHaveIncrementalInvocationOrders() {
    Service newService1 = newIdleService(DUMMY_FUNCTION1);
    Service newService2 = newIdleService(DUMMY_FUNCTION2);
    Service newService3 = newIdleService(DUMMY_FUNCTION3);

    assertEquals(1, newService1.getInvocationOrder());
    assertEquals(2, newService2.getInvocationOrder());
    assertEquals(3, newService3.getInvocationOrder());
  }

  @Test
  public void idleServicesAreSortedViaInvocationOrder() {
    Service newService1 = newIdleService(DUMMY_FUNCTION1);
    Service newService2 = newIdleService(DUMMY_FUNCTION2);
    Service newService3 = newIdleService(DUMMY_FUNCTION3);

    assertEquals(0, newService1.compareTo(newService1));
    assertTrue(1 >= newService1.compareTo(newService2));
    assertTrue(1 >= newService1.compareTo(newService3));
    assertTrue(-1 <= newService2.compareTo(newService1));
    assertTrue(1 >= newService2.compareTo(newService3));
    assertTrue(-1 <= newService3.compareTo(newService1));
    assertTrue(-1 <= newService3.compareTo(newService2));
  }
}
