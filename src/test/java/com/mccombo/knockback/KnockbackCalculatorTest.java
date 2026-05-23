package com.mccombo.knockback;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

class KnockbackCalculatorTest {

    @Test
    void groundedHitUsesConfiguredHorizontalAndVerticalValues() {
        KnockbackCalculator calculator = new KnockbackCalculator(new KnockbackSettings(
                0.72,
                0.28,
                0.90,
                0.75,
                1.05,
                0.40,
                0.15,
                false
        ));

        Vector result = calculator.compute(new Vector(1, 0, 0), false, false);

        assertEquals(0.72, result.getX(), 1.0E-9);
        assertEquals(0.28, result.getY(), 1.0E-9);
        assertEquals(0.0, result.getZ(), 1.0E-9);
    }

    @Test
    void airborneHitReducesVerticalAndHorizontal() {
        KnockbackCalculator calculator = new KnockbackCalculator(new KnockbackSettings(
                0.72,
                0.28,
                0.90,
                0.75,
                1.05,
                0.40,
                0.15,
                false
        ));

        Vector result = calculator.compute(new Vector(1, 0, 0), true, false);

        assertEquals(0.648, result.getX(), 1.0E-9);
        assertEquals(0.21, result.getY(), 1.0E-9);
    }

    @Test
    void sprintHitSlightlyIncreasesHorizontalKnockback() {
        KnockbackCalculator calculator = new KnockbackCalculator(new KnockbackSettings(
                0.72,
                0.28,
                0.90,
                0.75,
                1.05,
                0.40,
                0.15,
                false
        ));

        Vector result = calculator.compute(new Vector(1, 0, 0), false, true);

        assertEquals(0.756, result.getX(), 1.0E-9);
        assertEquals(0.28, result.getY(), 1.0E-9);
    }

    @Test
    void verticalCapLimitsUpwardVelocity() {
        KnockbackCalculator calculator = new KnockbackCalculator(new KnockbackSettings(
                0.72,
                2.50,
                0.90,
                0.75,
                1.05,
                0.40,
                0.15,
                false
        ));

        Vector result = calculator.compute(new Vector(1, 0, 0), false, false);

        assertEquals(0.40, result.getY(), 1.0E-9);
    }

    @Test
    void randomnessDisabledKeepsDirectionStable() {
        KnockbackCalculator calculator = new KnockbackCalculator(new KnockbackSettings(
                0.72,
                0.28,
                0.90,
                0.75,
                1.05,
                0.40,
                0.15,
                false
        ));

        Vector first = calculator.compute(new Vector(0.6, 0.0, 0.8), false, false);
        Vector second = calculator.compute(new Vector(0.6, 0.0, 0.8), false, false);

        assertEquals(first.getX(), second.getX(), 1.0E-9);
        assertEquals(first.getY(), second.getY(), 1.0E-9);
        assertEquals(first.getZ(), second.getZ(), 1.0E-9);
    }
}
