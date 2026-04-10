package com.example.elevator;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ElevatorSystemCache {
    private final GenericCache<String, Integer> elevatorPositions;
    private final GenericCache<String, Request> pendingRequests;
    private final Map<Integer, Integer> floorFrequency;

    public ElevatorSystemCache(int cacheSize, EvictionPolicy evictionPolicy) {
        if (cacheSize <= 0) {
            throw new IllegalArgumentException("Cache size must be positive.");
        }
        this.elevatorPositions = evictionPolicy == EvictionPolicy.LFU ? new LfuCache<>(cacheSize) : new LruCache<>(cacheSize);
        this.pendingRequests = evictionPolicy == EvictionPolicy.LFU ? new LfuCache<>(cacheSize) : new LruCache<>(cacheSize);
        this.floorFrequency = new ConcurrentHashMap<>();
    }

    public void updateElevatorPosition(String elevatorId, int floor) {
        elevatorPositions.put(elevatorId, floor);
    }

    public void addPendingRequest(Request request) {
        pendingRequests.put(request.getRequestId(), request);
        floorFrequency.merge(request.targetFloor(), 1, Integer::sum);
    }

    public void markFloorRequested(int floor) {
        floorFrequency.merge(floor, 1, Integer::sum);
    }

    public boolean isActiveFloor(int floor) {
        return floorFrequency.getOrDefault(floor, 0) >= 2;
    }

    public Optional<Integer> mostActiveFloor() {
        return floorFrequency.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    public Map<String, Integer> elevatorPositions() {
        return elevatorPositions.snapshot();
    }

    public Map<String, Request> pendingRequests() {
        return pendingRequests.snapshot();
    }
}
