package com.example.elevator;

import java.util.HashMap;
import java.util.Map;

public final class LfuCache<K, V> implements GenericCache<K, V> {
    private final int capacity;
    private final Map<K, V> values;
    private final Map<K, Integer> frequencies;

    public LfuCache(int capacity) {
        this.capacity = capacity;
        this.values = new HashMap<>();
        this.frequencies = new HashMap<>();
    }

    @Override
    public synchronized void put(K key, V value) {
        if (!values.containsKey(key) && values.size() >= capacity) {
            K evictKey = frequencies.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElseThrow();
            values.remove(evictKey);
            frequencies.remove(evictKey);
        }
        values.put(key, value);
        frequencies.put(key, frequencies.getOrDefault(key, 0) + 1);
    }

    @Override
    public synchronized V get(K key) {
        if (values.containsKey(key)) {
            frequencies.put(key, frequencies.getOrDefault(key, 0) + 1);
        }
        return values.get(key);
    }

    @Override
    public synchronized Map<K, V> snapshot() {
        return Map.copyOf(values);
    }
}
