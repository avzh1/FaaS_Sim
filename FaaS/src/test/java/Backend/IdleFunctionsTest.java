package Backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import Backend.Server.IdleFunctionException;
import Backend.Server.IdleFunctions;
import org.junit.Test;

public class IdleFunctionsTest {

  public IdleFunctions idleFunctions = new IdleFunctions();

  @Test
  public void addOneElementIncrementsSize() throws IdleFunctionException {
    idleFunctions.add(1);
    assertEquals(1, idleFunctions.size());
    assertFalse(idleFunctions.isEmpty());
  }

  @Test
  public void addingElementCanBeSeenWithContains() throws IdleFunctionException {
    idleFunctions.add(1);
    assertTrue(idleFunctions.contains(1));
  }

  @Test
  public void containsCannotSeeElementInList() throws IdleFunctionException {
    assertFalse(idleFunctions.contains(2));
    idleFunctions.add(1);
    assertFalse(idleFunctions.contains(2));
  }

  @Test(expected = IdleFunctionException.class)
  public void cannotAddElementsAlreadyInTheCollection() throws IdleFunctionException {
    idleFunctions.add(1);
    idleFunctions.add(1);
  }

  @Test
  public void canRemoveElementsInTheCollection() throws IdleFunctionException {
    idleFunctions.add(1);
    assertTrue(idleFunctions.remove(1));
  }

  @Test(expected = IdleFunctionException.class)
  public void cannotRemoveElementsNotInTheCollection() throws IdleFunctionException {
    idleFunctions.add(1);
    idleFunctions.remove(2);
  }

  @Test
  public void peekOldestReturnsFirstAddedElementAndDoesntRemove() throws IdleFunctionException
  {
    idleFunctions.add(1000);
    idleFunctions.add(2);
    idleFunctions.add(3);

    assertEquals(1000, idleFunctions.peekOldest());
    assertEquals(1000, idleFunctions.peekOldest());
  }

  @Test(expected = IdleFunctionException.class)
  public void peekEmptyGivesErrorTest() throws IdleFunctionException {
    idleFunctions.peekOldest();
  }

  @Test
  public void pollOldestReturnsFirstAddedElementAndRemoves() throws IdleFunctionException {
    idleFunctions.add(1);
    idleFunctions.add(2);
    idleFunctions.add(3);
    assertEquals(1, idleFunctions.pollOldest());
    assertEquals(2, idleFunctions.pollOldest());
    assertEquals(3, idleFunctions.pollOldest());
  }

  @Test
  public void removingElementManuallyUpdatesTheTop() throws IdleFunctionException {
    idleFunctions.add(1234);
    idleFunctions.add(282);
    idleFunctions.add(82346);
    assertTrue(idleFunctions.remove(1234));
    assertEquals(282, idleFunctions.peekOldest());
    idleFunctions.add(1234); // 282, 82346, 1234
    assertEquals(282, idleFunctions.peekOldest());
    assertTrue(idleFunctions.remove(82346));
    assertEquals(282, idleFunctions.peekOldest());
    assertEquals(2, idleFunctions.size());
    assertEquals(282, idleFunctions.pollOldest());
    assertEquals(1234, idleFunctions.pollOldest());
  }
}
