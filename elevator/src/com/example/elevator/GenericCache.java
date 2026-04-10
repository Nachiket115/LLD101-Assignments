package com.example.elevator;

import java.util.Map;

public interface GenericCache<K, V> {
    void put(K key, V value);

    V get(K key);

    Map<K, V> snapshot();
}
