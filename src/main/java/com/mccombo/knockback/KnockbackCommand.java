package com.mccombo.knockback;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class KnockbackCommand implements CommandExecutor, TabCompleter {
    private static final String PERMISSION = "knockback.admin";
    private final JavaPlugin plugin;
    private final KnockbackManager manager;

    public KnockbackCommand(JavaPlugin plugin, KnockbackManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                manager.reload();
                sender.sendMessage("Knockback configuration reloaded.");
                return true;
            }
            case "set" -> {
                if (args.length != 3) {
                    sender.sendMessage("Usage: /kb set <setting> <value>");
                    return true;
                }
                boolean success = manager.setSetting(args[1], args[2]);
                sender.sendMessage(success
                        ? "Updated " + args[1] + " to " + args[2]
                        : "Unknown setting or invalid value: " + args[1]);
                return true;
            }
            case "get" -> {
                sendCurrentSettings(sender);
                return true;
            }
            case "profile" -> {
                if (args.length != 3) {
                    sender.sendMessage("Usage: /kb profile save <name> or /kb profile load <name>");
                    return true;
                }
                if ("save".equalsIgnoreCase(args[1])) {
                    boolean saved = manager.saveProfile(args[2]);
                    sender.sendMessage(saved ? "Profile saved: " + args[2] : "Failed to save profile: " + args[2]);
                    return true;
                }
                if ("load".equalsIgnoreCase(args[1])) {
                    boolean loaded = manager.loadProfile(args[2]);
                    sender.sendMessage(loaded ? "Profile loaded: " + args[2] : "Profile not found: " + args[2]);
                    return true;
                }
                sender.sendMessage("Usage: /kb profile save <name> or /kb profile load <name>");
                return true;
            }
            default -> {
                sendHelp(sender);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("reload");
            completions.add("set");
            completions.add("get");
            completions.add("profile");
            return completions;
        }

        if (args.length == 2 && "set".equalsIgnoreCase(args[0])) {
            completions.add("horizontal-knockback");
            completions.add("vertical-knockback");
            completions.add("air-horizontal-multiplier");
            completions.add("air-vertical-multiplier");
            completions.add("sprint-multiplier");
            completions.add("max-vertical-cap");
            completions.add("hit-cooldown");
            completions.add("randomness");
            return completions;
        }

        if (args.length == 2 && "profile".equalsIgnoreCase(args[0])) {
            completions.add("save");
            completions.add("load");
            return completions;
        }

        return List.of();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("CubeCraft knockback commands:");
        sender.sendMessage("/kb reload");
        sender.sendMessage("/kb set <setting> <value>");
        sender.sendMessage("/kb get");
        sender.sendMessage("/kb profile save <name>");
        sender.sendMessage("/kb profile load <name>");
    }

    private void sendCurrentSettings(CommandSender sender) {
        KnockbackSettings settings = manager.currentSettings();
        sender.sendMessage("Current knockback settings:");
        sender.sendMessage("horizontal-knockback: " + settings.horizontalKnockback());
        sender.sendMessage("vertical-knockback: " + settings.verticalKnockback());
        sender.sendMessage("air-horizontal-multiplier: " + settings.airHorizontalMultiplier());
        sender.sendMessage("air-vertical-multiplier: " + settings.airVerticalMultiplier());
        sender.sendMessage("sprint-multiplier: " + settings.sprintMultiplier());
        sender.sendMessage("max-vertical-cap: " + settings.maxVerticalCap());
        sender.sendMessage("hit-cooldown: " + settings.hitCooldownSeconds());
        sender.sendMessage("randomness: " + settings.randomness());
    }
}
