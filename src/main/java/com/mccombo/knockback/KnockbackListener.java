package com.mccombo.knockback;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class KnockbackListener implements Listener {
    private final JavaPlugin plugin;
    private final KnockbackManager manager;

    public KnockbackListener(JavaPlugin plugin, KnockbackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) {
            return;
        }

        Player attacker = resolveAttacker(event);
        if (attacker == null || attacker.equals(target)) {
            return;
        }

        if (!manager.canApplyHit(attacker, target)) {
            return;
        }

        manager.recordHit(attacker, target);

        final var velocity = manager.calculate(attacker, target);
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!target.isValid()) {
                return;
            }
            target.setVelocity(velocity);
            target.setFallDistance(0.0F);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.clearCooldown(event.getPlayer());
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            return player;
        }

        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            return player;
        }

        return null;
    }
}
