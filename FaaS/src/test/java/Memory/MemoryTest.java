package Memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import FunctionAsAService.Function;
import FunctionAsAService.ServiceException;
import org.junit.Test;

public class MemoryTest {

  private final static int MAXIMUM_CAPACITY = 2;
  private final static Function DUMMY_FUNCTION1 = new Function(1, 0, 0);
  private final static Function DUMMY_FUNCTION2 = new Function(2, 0, 0);
  private final static Function DUMMY_FUNCTION3 = new Function(3, 0, 0);

  private final Memory memory = new Memory(MAXIMUM_CAPACITY);

  @Test
  public void memoryStartOffEmpty() {
    assertEquals(0, memory.size());
  }

  @Test
  public void reservingActiveFunctionsIncrementsSize() throws MemoryException {
    memory.enqueueActive(DUMMY_FUNCTION1);
    assertEquals(1, memory.size());
  }

  @Test(expected = MemoryException.class)
  public void cannotReserveDuplicateFunctions() throws MemoryException {
    memory.enqueueActive(DUMMY_FUNCTION1);
    memory.enqueueActive(DUMMY_FUNCTION1);
  }

  @Test
  public void isActiveCanIdentifyFunctionsThatAreInActive() throws MemoryException {
    memory.enqueueActive(DUMMY_FUNCTION1);
    assertTrue(memory.isActive(DUMMY_FUNCTION1.getFunctionID()));
    assertFalse(memory.isActive(DUMMY_FUNCTION2.getFunctionID()));
  }

  @Test
  public void reservingIdleFunctionsIncrementsSize() throws MemoryException {
    memory.enqueueIdle(DUMMY_FUNCTION1);
    assertEquals(1, memory.size());
  }

  @Test(expected = MemoryException.class)
  public void cannotReserveIdleIfAlreadyInMemory() throws MemoryException {
    memory.enqueueActive(DUMMY_FUNCTION1);
    memory.enqueueIdle(DUMMY_FUNCTION1);
  }

  @Test
  public void enqueueingSomethingAsIdleDoesNotMakeItActive() throws MemoryException {
    memory.enqueueIdle(DUMMY_FUNCTION1);
    assertFalse(memory.isActive(DUMMY_FUNCTION1.getFunctionID()));
  }

  @Test
  public void reservingLoadingFunctionsIncrementsSize() throws MemoryException {
    memory.enqueueLoading(DUMMY_FUNCTION1);
    assertEquals(1, memory.size());
  }

  @Test(expected = MemoryException.class)
  public void reservingActiveCannotExceedMaximumCapacity() throws MemoryException {
    memory.enqueueActive(DUMMY_FUNCTION1);
    memory.enqueueActive(DUMMY_FUNCTION2);
    memory.enqueueActive(DUMMY_FUNCTION3);
  }

  @Test(expected = MemoryException.class)
  public void reservingIdleCannotExceedMaximumCapacity() throws MemoryException {
    memory.enqueueIdle(DUMMY_FUNCTION1);
    memory.enqueueIdle(DUMMY_FUNCTION2);
    memory.enqueueIdle(DUMMY_FUNCTION3);
  }

  @Test(expected = MemoryException.class)
  public void reservingLoadingCannotExceedMaximumCapacity() throws MemoryException {
    memory.enqueueLoading(DUMMY_FUNCTION1);
    memory.enqueueLoading(DUMMY_FUNCTION2);
    memory.enqueueLoading(DUMMY_FUNCTION3);
  }

  @Test
  public void promotingALOADINGFunctionPromotesToACTIVE()
      throws MemoryException, ServiceException {
    memory.enqueueLoading(DUMMY_FUNCTION1);
    assertTrue(memory.isLoading(DUMMY_FUNCTION1.getFunctionID()));
    assertFalse(memory.isActive(DUMMY_FUNCTION1.getFunctionID()));
    assertEquals(1, memory.size());
    memory.promote(DUMMY_FUNCTION1);
    assertFalse(memory.isLoading(DUMMY_FUNCTION1.getFunctionID()));
    assertTrue(memory.isActive(DUMMY_FUNCTION1.getFunctionID()));
    assertEquals(1, memory.size());
  }

  @Test
  public void promotingAnIDLEFunctionPromotesToACTIVE()
      throws MemoryException, ServiceException {
    memory.enqueueIdle(DUMMY_FUNCTION1);
    assertTrue(memory.isIdle(DUMMY_FUNCTION1.getFunctionID()));
    assertFalse(memory.isActive(DUMMY_FUNCTION1.getFunctionID()));
    assertEquals(1, memory.size());
    memory.promote(DUMMY_FUNCTION1);
    assertFalse(memory.isIdle(DUMMY_FUNCTION1.getFunctionID()));
    assertTrue(memory.isActive(DUMMY_FUNCTION1.getFunctionID()));
    assertEquals(1, memory.size());
  }


}
