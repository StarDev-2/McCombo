package com.mccombo.knockback;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class KnockbackManager {
    private final JavaPlugin plugin;
    private final HitCooldownTracker cooldownTracker;
    private KnockbackSettings settings;
    private KnockbackCalculator calculator;
    private long hitCooldownNanos;

    public KnockbackManager(JavaPlugin plugin, KnockbackSettings settings) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.cooldownTracker = new HitCooldownTracker();
        this.settings = settings;
        this.calculator = new KnockbackCalculator(settings);
        this.hitCooldownNanos = toNanos(settings.hitCooldownSeconds());
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        this.settings = readSettings(config);
        this.calculator = new KnockbackCalculator(settings);
        this.hitCooldownNanos = toNanos(settings.hitCooldownSeconds());
    }

    public Vector calculate(Player attacker, Player victim) {
        Vector direction = victim.getLocation().toVector().subtract(attacker.getLocation().toVector());
        boolean airborne = !victim.isOnGround();
        boolean sprinting = attacker.isSprinting();
        return calculator.compute(direction, airborne, sprinting);
    }

    public KnockbackSettings currentSettings() {
        return settings;
    }

    public boolean canApplyHit(Player attacker, Player victim) {
        return cooldownTracker.canApply(attacker.getUniqueId(), victim.getUniqueId(), System.nanoTime(), hitCooldownNanos);
    }

    public void recordHit(Player attacker, Player victim) {
        cooldownTracker.mark(attacker.getUniqueId(), victim.getUniqueId(), System.nanoTime());
    }

    public void clearCooldown(Player attacker) {
        cooldownTracker.remove(attacker.getUniqueId());
    }

    public boolean setSetting(String key, String value) {
        if (key == null || value == null) {
            return false;
        }

        String normalized = key.toLowerCase(Locale.ROOT);
        FileConfiguration config = plugin.getConfig();
        boolean success = true;

        try {
            switch (normalized) {
                case "horizontal-knockback" -> config.set(normalized, Double.parseDouble(value));
                case "vertical-knockback" -> config.set(normalized, Double.parseDouble(value));
                case "air-horizontal-multiplier" -> config.set(normalized, Double.parseDouble(value));
                case "air-vertical-multiplier" -> config.set(normalized, Double.parseDouble(value));
                case "sprint-multiplier" -> config.set(normalized, Double.parseDouble(value));
                case "max-vertical-cap" -> config.set(normalized, Double.parseDouble(value));
                case "hit-cooldown" -> config.set(normalized, Double.parseDouble(value));
                case "randomness" -> config.set(normalized, parseBoolean(value));
                default -> success = false;
            }
        } catch (NumberFormatException exception) {
            return false;
        }

        if (!success) {
            return false;
        }

        plugin.saveConfig();
        reload();
        return true;
    }

    public boolean saveProfile(String profileName) {
        File profileDir = new File(plugin.getDataFolder(), "profiles");
        if (!profileDir.exists() && !profileDir.mkdirs()) {
            return false;
        }

        String safeName = profileName.replaceAll("[^A-Za-z0-9_-]", "_");
        File file = new File(profileDir, safeName + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("horizontal-knockback", settings.horizontalKnockback());
        yaml.set("vertical-knockback", settings.verticalKnockback());
        yaml.set("air-horizontal-multiplier", settings.airHorizontalMultiplier());
        yaml.set("air-vertical-multiplier", settings.airVerticalMultiplier());
        yaml.set("sprint-multiplier", settings.sprintMultiplier());
        yaml.set("max-vertical-cap", settings.maxVerticalCap());
        yaml.set("hit-cooldown", settings.hitCooldownSeconds());
        yaml.set("randomness", settings.randomness());

        try {
            yaml.save(file);
            return true;
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save knockback profile: " + exception.getMessage());
            return false;
        }
    }

    public boolean loadProfile(String profileName) {
        File profileDir = new File(plugin.getDataFolder(), "profiles");
        String safeName = profileName.replaceAll("[^A-Za-z0-9_-]", "_");
        File file = new File(profileDir, safeName + ".yml");
        if (!file.exists()) {
            return false;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        this.settings = readSettings(yaml);
        this.calculator = new KnockbackCalculator(settings);

        FileConfiguration config = plugin.getConfig();
        config.set("horizontal-knockback", settings.horizontalKnockback());
        config.set("vertical-knockback", settings.verticalKnockback());
        config.set("air-horizontal-multiplier", settings.airHorizontalMultiplier());
        config.set("air-vertical-multiplier", settings.airVerticalMultiplier());
        config.set("sprint-multiplier", settings.sprintMultiplier());
        config.set("max-vertical-cap", settings.maxVerticalCap());
        config.set("hit-cooldown", settings.hitCooldownSeconds());
        config.set("randomness", settings.randomness());
        plugin.saveConfig();
        return true;
    }

    public static KnockbackSettings readSettings(ConfigurationSection section) {
        return new KnockbackSettings(
                section.getDouble("horizontal-knockback", 0.72),
                section.getDouble("vertical-knockback", 0.28),
                section.getDouble("air-horizontal-multiplier", 0.90),
                section.getDouble("air-vertical-multiplier", 0.75),
                section.getDouble("sprint-multiplier", 1.05),
                section.getDouble("max-vertical-cap", 0.40),
                section.getDouble("hit-cooldown", 0.15),
                section.getBoolean("randomness", false)
        );
    }

    private long toNanos(double seconds) {
        return (long) (seconds * 1_000_000_000L);
    }

    private boolean parseBoolean(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> Boolean.parseBoolean(value);
        };
    }
}
