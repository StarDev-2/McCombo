package com.mccombo.knockback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HitCooldownTracker {
    private final Map<UUID, Long> lastHitNanos = new HashMap<>();

    public boolean canApply(UUID attackerId, long nowNanos, long cooldownNanos) {
        Long last = lastHitNanos.get(attackerId);
        return last == null || nowNanos - last >= cooldownNanos;
    }

    public void mark(UUID attackerId, long nowNanos) {
        lastHitNanos.put(attackerId, nowNanos);
    }

    public void remove(UUID attackerId) {
        lastHitNanos.remove(attackerId);
    }
}
