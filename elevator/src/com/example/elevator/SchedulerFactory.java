package com.example.elevator;

public final class SchedulerFactory {
    private SchedulerFactory() {
    }

    public static Scheduler forMode(SystemMode mode) {
        if (mode == SystemMode.HIGH_TRAFFIC) {
            return new LookScheduler();
        }
        if (mode == SystemMode.EMERGENCY) {
            return new NearestCarScheduler();
        }
        return new ScanScheduler();
    }
}
