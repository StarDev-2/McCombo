package com.mccombo.knockback;

import org.bukkit.util.Vector;

public final class KnockbackCalculator {
    private final KnockbackSettings settings;

    public KnockbackCalculator(KnockbackSettings settings) {
        this.settings = settings;
    }

    public Vector compute(Vector direction, boolean airborne, boolean sprinting) {
        Vector normalized = direction.clone();
        if (normalized.lengthSquared() < 1.0E-9) {
            normalized.setX(1.0);
            normalized.setZ(0.0);
        }

        normalized.setY(0.0);
        normalized.normalize();

        double horizontal = settings.horizontalKnockback();
        if (airborne) {
            horizontal *= settings.airHorizontalMultiplier();
        }
        if (sprinting) {
            horizontal *= settings.sprintMultiplier();
        }

        double vertical = settings.verticalKnockback();
        if (airborne) {
            vertical *= settings.airVerticalMultiplier();
        }

        if (vertical > settings.maxVerticalCap()) {
            vertical = settings.maxVerticalCap();
        }

        if (settings.randomness()) {
            double jitter = 0.02;
            normalized.add(new Vector(
                    Math.random() * jitter,
                    0.0,
                    Math.random() * jitter
            ));
            normalized.normalize();
        }

        return new Vector(
                normalized.getX() * horizontal,
                vertical,
                normalized.getZ() * horizontal
        );
    }
}
