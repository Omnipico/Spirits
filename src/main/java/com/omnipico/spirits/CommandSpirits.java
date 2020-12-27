package com.omnipico.spirits;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CommandSpirits implements CommandExecutor, TabCompleter {
    JavaPlugin plugin;
    Graveyard graveyard;

    public CommandSpirits(Spirits plugin) {
        this.plugin = plugin;
        this.graveyard = new Graveyard(plugin.getConfig(), plugin);
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("setlives") || args[0].equalsIgnoreCase("sl")) {
                if (sender.hasPermission("spirits.setlives")) {
                    if (args.length > 2) {
                        String target = args[1];
                        Player targetPlayer = Bukkit.getPlayerExact(target);
                        if (targetPlayer != null) {
                            try {
                                graveyard.setLives(targetPlayer, Integer.parseInt(args[2]));
                                sender.spigot().sendMessage(
                                        new ComponentBuilder().append(targetPlayer.getDisplayName()).append(" now has " + args[2] + " lives.").color(ChatColor.GREEN).create()
                                );
                            } catch (NumberFormatException e) {
                                sender.spigot().sendMessage(
                                        new ComponentBuilder().append("Life count must be a number.").color(ChatColor.RED).create()
                                );
                            }
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Player not found.").color(ChatColor.RED).create()
                            );
                        }
                    } else {
                        sender.spigot().sendMessage(
                                new ComponentBuilder().append("Usage: /spirits setlives <player> <amount>").color(ChatColor.RED).create()
                        );
                    }
                } else {
                    sender.spigot().sendMessage(
                            new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                    );
                }
            } else if (args[0].equalsIgnoreCase("revive") || args[0].equalsIgnoreCase("r")) {
                if (sender.hasPermission("spirits.revive")) {
                    if (args.length > 1) {
                        String target = args[1];
                        Player targetPlayer = Bukkit.getPlayerExact(target);
                        if (targetPlayer != null) {
                            graveyard.setAlive(targetPlayer, true);
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append(targetPlayer.getDisplayName()).append(" is now alive.").color(ChatColor.GREEN).create()
                            );
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Player not found.").color(ChatColor.RED).create()
                            );
                        }
                    } else {
                        sender.spigot().sendMessage(
                                new ComponentBuilder().append("Must specify a player.").color(ChatColor.RED).create()
                        );
                    }
                } else {
                    sender.spigot().sendMessage(
                            new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                    );
                }
            } else if (args[0].equalsIgnoreCase("kill") || args[0].equalsIgnoreCase("k")) {
                if (sender.hasPermission("spirits.revive")) {
                    if (args.length > 1) {
                        String target = args[1];
                        Player targetPlayer = Bukkit.getPlayerExact(target);
                        if (targetPlayer != null) {
                            graveyard.setAlive(targetPlayer, true);
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append(targetPlayer.getDisplayName()).append(" is now dead.").color(ChatColor.GREEN).create()
                            );
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Player not found.").color(ChatColor.RED).create()
                            );
                        }
                    } else {
                        sender.spigot().sendMessage(
                                new ComponentBuilder().append("Must specify a player.").color(ChatColor.RED).create()
                        );
                    }
                } else {
                    sender.spigot().sendMessage(
                            new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                    );
                }
            } else {
                sender.spigot().sendMessage(
                        new ComponentBuilder().append("Usage: /spirits <subcommand>").color(ChatColor.RED).create()
                );
            }
        } else {
            sender.spigot().sendMessage(
                    new ComponentBuilder().append("Usage: /spirits <subcommand>").color(ChatColor.RED).create()
            );
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //List<String> subCommands = new ArrayList<String>();
        return null;
    }
}
