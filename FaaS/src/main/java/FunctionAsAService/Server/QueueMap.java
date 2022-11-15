package FunctionAsAService.Server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An extension to the Map interface but also offering queue support. Under the hood, this is a
 * HashMap, however, it offers additional ordering support, such that you can peek/pop from the top
 * of the entries like a queue.
 *
 * @param <K> Key
 * @param <V> Values
 */
public class QueueMap<K, V> implements Map<K, V> {

  private final Map<K, Node<K, V>> map = new HashMap<>();
  private Node<K, V> top = null;
  private Node<K, V> bottom = null;

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return map.get(key).value;
  }

  @Override
  public V put(K key, V value) {
    // check if entry already present
    if (map.containsKey(key)) {
      return map.get(key).value;
    }
    // create a new Node to record the order in a linked fashion
    Node<K, V> node;
    if (size() == 0) {
      // we're the first to put in an entry
      node = new Node<>(key, value, null, null);
      top = node;
    } else {
      node = new Node<>(key, value, bottom, null);
      bottom.bottom = node;
    }
    bottom = node;
    map.put(key, node);
    return value;
  }

  @Override
  public V remove(Object key) {
    // remove from the internal map structure
    Node<K, V> node = map.remove(key);

    // regardless of the case, we want to update the bottom pointer of the node above
    if (node.top == null) {
      top = node.bottom; // only the top has no top
    } else {
      node.top.bottom = node.bottom;
    }

    // regardless of the case, we want to update the top pointer of the node below
    if (node.bottom == null) {
      bottom = node.top; // only the bottom has no bottom
    } else {
      node.bottom.top = node.top;
    }

    return node.value;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    m.forEach(this::put);
  }

  @Override
  public void clear() {
    top = null;
    map.clear();
  }

  @Override
  public Set<K> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<V> values() {
    return map.values().stream().map(n -> n.value).collect(Collectors.toList());
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return map.entrySet().stream().map(entry -> (Entry<K, V>) entry).collect(Collectors.toSet());
  }

  public V peek() throws MemoryException {
    if (size() == 0) {
      throw MemoryException.MEMORY_UNDERFLOW;
    }
    return top.value;
  }

  public V pop() throws MemoryException {
    if (size() == 0) {
      throw MemoryException.MEMORY_UNDERFLOW;
    }
    Node<K, V> node = top;
    top = node.bottom;
    remove(node.key);
    return node.value;
  }

  /**
   * Private class for keeping the entries in a 'linked-list' order. This way, lookups into QueueMap
   * are O(1), removal is O(1) and lookup is O(1)
   */
  private static class Node<K, V> {

    public final K key;
    public final V value;
    public Node<K, V> top;
    public Node<K, V> bottom;

    public Node(K key, V value, Node<K, V> top, Node<K, V> bottom) {
      this.key = key;
      this.value = value;
      this.top = top;
      this.bottom = bottom;
    }
  }
}
