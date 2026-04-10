package com.example.elevator;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LruCache<K, V> implements GenericCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> values;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.values = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    @Override
    public synchronized void put(K key, V value) {
        values.put(key, value);
        if (values.size() > capacity) {
            K eldest = values.keySet().iterator().next();
            values.remove(eldest);
        }
    }

    @Override
    public synchronized V get(K key) {
        return values.get(key);
    }

    @Override
    public synchronized Map<K, V> snapshot() {
        return Map.copyOf(values);
    }
}
