package com.mccombo.knockback;

public record KnockbackSettings(
        double horizontalKnockback,
        double verticalKnockback,
        double airHorizontalMultiplier,
        double airVerticalMultiplier,
        double sprintMultiplier,
        double maxVerticalCap,
        double hitCooldownSeconds,
        boolean randomness
) {
    public KnockbackSettings {
        horizontalKnockback = clamp(horizontalKnockback, 0.0, 4.0);
        verticalKnockback = clamp(verticalKnockback, 0.0, 4.0);
        airHorizontalMultiplier = clamp(airHorizontalMultiplier, 0.0, 1.5);
        airVerticalMultiplier = clamp(airVerticalMultiplier, 0.0, 1.5);
        sprintMultiplier = clamp(sprintMultiplier, 0.0, 2.0);
        maxVerticalCap = clamp(maxVerticalCap, 0.0, 4.0);
        hitCooldownSeconds = clamp(hitCooldownSeconds, 0.0, 5.0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
