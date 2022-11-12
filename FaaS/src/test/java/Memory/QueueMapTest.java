package Memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import FunctionAsAService.Function;
import org.junit.Test;

public class QueueMapTest {

  QueueMap<Integer, Function> idleQueue = new QueueMap<Integer, Function>();

  private static final Function DUMMY_FUNCTION1 = new Function(1, 0, 0);
  private static final Function DUMMY_FUNCTION2 = new Function(2, 0, 0);
  private static final Function DUMMY_FUNCTION3 = new Function(3, 0, 0);

  @Test
  public void addingToQueueIncrementsSize() {
    idleQueue.put(DUMMY_FUNCTION1.getFunctionID(), DUMMY_FUNCTION1);
    assertEquals(1, idleQueue.size());
  }

  @Test
  public void containsCanSeeThingsInQueue() {
    idleQueue.put(DUMMY_FUNCTION1.getFunctionID(), DUMMY_FUNCTION1);
    idleQueue.put(DUMMY_FUNCTION3.getFunctionID(), DUMMY_FUNCTION3);
    assertTrue(idleQueue.containsKey(DUMMY_FUNCTION1.getFunctionID()));
    assertTrue(idleQueue.containsKey(DUMMY_FUNCTION3.getFunctionID()));
    assertFalse(idleQueue.containsKey(DUMMY_FUNCTION2.getFunctionID()));
  }

  @Test
  public void peekingTopOfQueuePreservesStructure() throws MemoryException {
    idleQueue.put(DUMMY_FUNCTION1.getFunctionID(), DUMMY_FUNCTION1);
    assertEquals(DUMMY_FUNCTION1, idleQueue.peek());
    assertEquals(1, idleQueue.size());
  }

  @Test
  public void poppingTopOfQueueDecrementsSize() throws MemoryException {
    idleQueue.put(DUMMY_FUNCTION1.getFunctionID(), DUMMY_FUNCTION1);
    assertEquals(DUMMY_FUNCTION1, idleQueue.pop());
    assertEquals(0, idleQueue.size());
  }

  @Test
  public void containsCantSeePoppedServices() throws MemoryException {
    poppingTopOfQueueDecrementsSize();
    assertFalse(idleQueue.containsKey(DUMMY_FUNCTION1.getFunctionID()));
  }

  @Test(expected = MemoryException.class)
  public void cannotPopEmptyQueue() throws MemoryException {
    assertEquals(0, idleQueue.size());
    idleQueue.pop();
  }

  @Test
  public void removingElementFromMiddleOfQueuePreservesOtherOrders() {
    idleQueue.put(DUMMY_FUNCTION1.getFunctionID(), DUMMY_FUNCTION1);
    idleQueue.put(DUMMY_FUNCTION2.getFunctionID(), DUMMY_FUNCTION2);
    idleQueue.put(DUMMY_FUNCTION3.getFunctionID(), DUMMY_FUNCTION3);
    assertEquals(3, idleQueue.size());

    assertEquals(DUMMY_FUNCTION2, idleQueue.remove(DUMMY_FUNCTION2.getFunctionID()));

    assertEquals(2, idleQueue.size());

    assertTrue(idleQueue.containsKey(DUMMY_FUNCTION1.getFunctionID()));
    assertTrue(idleQueue.containsKey(DUMMY_FUNCTION3.getFunctionID()));
    assertFalse(idleQueue.containsKey(DUMMY_FUNCTION2.getFunctionID()));
  }

  @Test
  public void removingElementFromMiddleAndAddingBackPlacesAtBackOfQueue() throws MemoryException {
    removingElementFromMiddleOfQueuePreservesOtherOrders();
    idleQueue.put(DUMMY_FUNCTION2.getFunctionID(), DUMMY_FUNCTION2);
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
