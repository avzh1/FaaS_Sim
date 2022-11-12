package Server2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import Backend.Server2.Function;
import Backend.Server2.ReservedMemory;
import java.util.Map;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class ReservedMemoryTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  Map<Integer, Function> active = context.mock(Map.class);
  Map<Integer, Function> idle = context.mock(Map.class);
  Map<Integer, Function> loading = context.mock(Map.class);
  public static final int INITIAL_CAPACITY = 2;
  public ReservedMemory rm = new ReservedMemory(INITIAL_CAPACITY, loading, active,
      idle);

  Function dummyFunction = new Function(1, 0, 0);

  @Test
  public void cannotLoadWhenCapacityIsFull() {
    context.checking(new Expectations() {{
      // function not in loading, active or idle memory
      exactly(3).of(mapObjects).containsKey(dummyFunction.getFunctionID());
      will(returnValue(false));
      // size will return that it is at its maximum capacity
      exactly(3).of(mapObjects).size();
      will(returnValue(1));
    }});

    assertFalse(rm.load(dummyFunction));
  }
//
//  @Test
//  public void cannotLoadWhenAlreadyInMemory() {
//    context.checking(new Expectations() {{
//      // function in loading
//      exactly(1).of(mapObjects).containsKey(dummyFunction.getFunctionID());
//      will(returnValue(true));
//    }});
//
//    assertTrue(rm.load(dummyFunction));
//  }
//
//  @Test
//  public void canLoadWhenNotInMemoryAndCapacityNotFull() {
//    context.checking(new Expectations() {{
//      // function not in loading, active or idle memory
//      exactly(3).of(mapObjects).containsKey(dummyFunction.getFunctionID());
//      will(returnValue(false));
//      // size will return that it is at its maximum capacity
//      exactly(3).of(mapObjects).size();
//      will(returnValue(0));
//      // function is enqueued as loading
//      exactly(1).of(mapObjects).put(dummyFunction.getFunctionID(), dummyFunction);
//    }});
//
//    // set up trigger
//    assertTrue(rm.load(dummyFunction));
//  }
//
//  @Test
//  public void sizeCalculatedFromSizeOfThreePartitionsOfMemory() {
//    context.checking(new Expectations() {{
//      exactly(3).of(mapObjects).size();
//      will(returnValue(1));
//    }});
//
//    assertEquals(3, rm.size());
//  }
//
//  public void idleMapCheckedForIdleStatus() {
//
//  }

}
