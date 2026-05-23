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

        assertTrue(tracker.canApply(attacker, 1_000_000_000L, 150_000_000L));
        tracker.mark(attacker, 1_000_000_000L);

        assertFalse(tracker.canApply(attacker, 1_050_000_000L, 150_000_000L));
        assertTrue(tracker.canApply(attacker, 1_150_000_000L, 150_000_000L));
    }

    @Test
    void zeroCooldownAllowsImmediateConsecutiveHits() {
        HitCooldownTracker tracker = new HitCooldownTracker();
        UUID attacker = UUID.randomUUID();

        assertTrue(tracker.canApply(attacker, 1_000_000_000L, 0L));
        tracker.mark(attacker, 1_000_000_000L);
        assertTrue(tracker.canApply(attacker, 1_000_000_001L, 0L));
    }
}
