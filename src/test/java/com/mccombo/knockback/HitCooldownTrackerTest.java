package com.mccombo.knockback;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class HitCooldownTrackerTest {

    @Test
    void firstHitIsAllowedThenCooldownBlocksUntilElapsed() {
        HitCooldownTracker tracker = new HitCooldownTracker();
        UUID attacker = UUID.randomUUID();
        UUID target = UUID.randomUUID();

        assertTrue(tracker.canApply(attacker, target, 1_000_000_000L, 150_000_000L));
        tracker.mark(attacker, target, 1_000_000_000L);

        assertFalse(tracker.canApply(attacker, target, 1_050_000_000L, 150_000_000L));
        assertTrue(tracker.canApply(attacker, target, 1_150_000_000L, 150_000_000L));
    }

    @Test
    void cooldownIsScopedToTheSameTarget() {
        HitCooldownTracker tracker = new HitCooldownTracker();
        UUID attacker = UUID.randomUUID();
        UUID firstTarget = UUID.randomUUID();
        UUID secondTarget = UUID.randomUUID();

        assertTrue(tracker.canApply(attacker, firstTarget, 1_000_000_000L, 150_000_000L));
        tracker.mark(attacker, firstTarget, 1_000_000_000L);

        assertTrue(tracker.canApply(attacker, secondTarget, 1_010_000_000L, 150_000_000L));
    }

    @Test
    void zeroCooldownAllowsImmediateConsecutiveHits() {
        HitCooldownTracker tracker = new HitCooldownTracker();
        UUID attacker = UUID.randomUUID();
        UUID target = UUID.randomUUID();

        assertTrue(tracker.canApply(attacker, target, 1_000_000_000L, 0L));
        tracker.mark(attacker, target, 1_000_000_000L);
        assertTrue(tracker.canApply(attacker, target, 1_000_000_001L, 0L));
    }
}
