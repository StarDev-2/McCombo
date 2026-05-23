package com.mccombo.knockback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HitCooldownTracker {
    private final Map<UUID, Map<UUID, Long>> lastHitsByTarget = new HashMap<>();

    public boolean canApply(UUID attackerId, UUID targetId, long nowNanos, long cooldownNanos) {
        Map<UUID, Long> targetHits = lastHitsByTarget.get(attackerId);
        if (targetHits == null) {
            return true;
        }

        Long last = targetHits.get(targetId);
        return last == null || nowNanos - last >= cooldownNanos;
    }

    public void mark(UUID attackerId, UUID targetId, long nowNanos) {
        lastHitsByTarget
                .computeIfAbsent(attackerId, ignored -> new HashMap<>())
                .put(targetId, nowNanos);
    }

    public void remove(UUID attackerId) {
        lastHitsByTarget.remove(attackerId);
    }
}
