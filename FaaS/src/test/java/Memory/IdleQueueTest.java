package Memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import FunctionAsAService.Function;
import org.junit.Test;

public class IdleQueueTest {

  IdleQueue idleQueue = new IdleQueue();

  private static final Function DUMMY_FUNCTION1 = new Function(1, 0, 0);
  private static final Function DUMMY_FUNCTION2 = new Function(2, 0, 0);
  private static final Function DUMMY_FUNCTION3 = new Function(3, 0, 0);

  @Test
  public void addingToQueueIncrementsSize() throws MemoryException {
    idleQueue.add(DUMMY_FUNCTION1);
    assertEquals(1, idleQueue.size());
  }

  @Test
  public void containsCanSeeThingsInQueue() throws MemoryException {
    idleQueue.add(DUMMY_FUNCTION1);
    idleQueue.add(DUMMY_FUNCTION3);
    assertTrue(idleQueue.contains(DUMMY_FUNCTION1.getFunctionID()));
    assertTrue(idleQueue.contains(DUMMY_FUNCTION3.getFunctionID()));
    assertFalse(idleQueue.contains(DUMMY_FUNCTION2.getFunctionID()));
  }

  @Test
  public void peekingTopOfQueuePreservesStructure() throws MemoryException {
    idleQueue.add(DUMMY_FUNCTION1);
    assertEquals(DUMMY_FUNCTION1, idleQueue.peek());
    assertEquals(1, idleQueue.size());
  }

  @Test
  public void poppingTopOfQueueDecrementsSize() throws MemoryException {
    idleQueue.add(DUMMY_FUNCTION1);
    assertEquals(DUMMY_FUNCTION1, idleQueue.pop());
    assertEquals(0, idleQueue.size());
  }

  @Test
  public void containsCantSeePoppedServices() throws MemoryException {
    poppingTopOfQueueDecrementsSize();
    assertFalse(idleQueue.contains(DUMMY_FUNCTION1.getFunctionID()));
  }

  @Test(expected = MemoryException.class)
  public void cannotPopEmptyQueue() throws MemoryException {
    assertEquals(0, idleQueue.size());
    idleQueue.pop();
  }

  @Test
  public void removingElementFromMiddleOfQueuePreservesOtherOrders() throws MemoryException {
    idleQueue.add(DUMMY_FUNCTION1);
    idleQueue.add(DUMMY_FUNCTION2);
    idleQueue.add(DUMMY_FUNCTION3);
    assertEquals(3, idleQueue.size());

    assertEquals(DUMMY_FUNCTION2, idleQueue.remove(DUMMY_FUNCTION2.getFunctionID()));

    assertEquals(2, idleQueue.size());

    assertTrue(idleQueue.contains(DUMMY_FUNCTION1.getFunctionID()));
    assertTrue(idleQueue.contains(DUMMY_FUNCTION3.getFunctionID()));
    assertFalse(idleQueue.contains(DUMMY_FUNCTION2.getFunctionID()));
  }

  @Test
  public void removingElementFromMiddleAndAddingBackPlacesAtBackOfQueue() throws MemoryException {
    removingElementFromMiddleOfQueuePreservesOtherOrders();
    idleQueue.add(DUMMY_FUNCTION2);
    assertEquals(3, idleQueue.size());

    assertEquals(DUMMY_FUNCTION1, idleQueue.pop());
    assertEquals(DUMMY_FUNCTION3, idleQueue.pop());
    assertEquals(DUMMY_FUNCTION2, idleQueue.pop());
  }

  @Test(expected = MemoryException.class)
  public void cannotRemoveEmptyQueue() throws MemoryException {
    assertEquals(0, idleQueue.size());
    idleQueue.pop();
  }
}
