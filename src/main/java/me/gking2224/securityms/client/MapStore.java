package me.gking2224.securityms.client;

import java.util.HashMap;
import java.util.Map;

public class MapStore<K, V> implements Store<K, V> {

    private Map<K, V> map = new HashMap<K, V>();
    
    @Override
    public void store(K key, V object) {
        map.put(key, object);
    }

    @Override
    public V load(K key) {
        return map.get(key);
    }

}
