package com.example.elevator;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Request {
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    private final String requestId;
    private final Instant createdAt;
    private boolean assigned;
    private boolean served;

    protected Request(String prefix) {
        this.requestId = Objects.requireNonNull(prefix, "prefix") + "-" + SEQUENCE.getAndIncrement();
        this.createdAt = Instant.now();
    }

    public String getRequestId() {
        return requestId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void markAssigned() {
        assigned = true;
    }

    public boolean isServed() {
        return served;
    }

    public void markServed() {
        served = true;
    }

    public abstract int targetFloor();
}
