package me.gking2224.securityms.client;

public interface Store<K, V> {

    void store(K key, V object);
    
    V load(K key);
}
