package com.mccombo.knockback;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class KnockbackPlugin extends JavaPlugin {
    private KnockbackManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        manager = new KnockbackManager(this, KnockbackManager.readSettings(getConfig()));
        getServer().getPluginManager().registerEvents(new KnockbackListener(this, manager), this);

        PluginCommand command = getCommand("kb");
        if (command != null) {
            command.setExecutor(new KnockbackCommand(this, manager));
            command.setTabCompleter(new KnockbackCommand(this, manager));
        }

        getLogger().info("McCombo knockback plugin enabled.");
    }

    public KnockbackManager getManager() {
        return manager;
    }
}
