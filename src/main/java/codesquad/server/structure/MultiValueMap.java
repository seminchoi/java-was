package codesquad.server.structure;

import java.util.*;

public class MultiValueMap <K, V> {
    private Map<K, List<V>> map = new HashMap<>();

    public void put(K key, V value) {
        List<V> values = map.getOrDefault(key, new ArrayList<>());
        values.add(value);
        map.put(key, values);
    }

    public V getFirst(K key) {
        List<V> values = map.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public List<V> get(K key) {
        List<V> values = map.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return map.getOrDefault(key, new ArrayList<>()).stream().toList();
    }

    public Set<K> keySet() {
        return map.keySet();
    }
}
