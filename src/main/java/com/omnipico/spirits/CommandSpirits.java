package com.omnipico.spirits;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
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
    BaseComponent[] helpMessage = new ComponentBuilder("Commands").color(ChatColor.GREEN)
            .append("\n/spirits help").color(ChatColor.AQUA)
            .append(" -- Lists the Spirits commands").color(ChatColor.GRAY)
            .append("\n/spirits setlives <player> <amount>").color(ChatColor.AQUA)
            .append(" -- Sets <player>'s non-canon life count to <amount>").color(ChatColor.GRAY)
            .append("\n/spirits revive [player]").color(ChatColor.AQUA)
            .append(" -- Makes <player> no longer a spirit").color(ChatColor.GRAY)
            .append("\n/spirits kill [player]").color(ChatColor.AQUA)
            .append(" -- Makes <player> a spirit").color(ChatColor.GRAY)
            .append("\n/spirits hardcore [player]").color(ChatColor.AQUA)
            .append(" -- Toggles \"hardcore\", setting their lives to 1 (useful for RP)").color(ChatColor.GRAY)
            .create();

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
                    Player targetPlayer = null;
                    if (args.length > 1) {
                        String target = args[1];
                        if (sender.hasPermission("spirits.revive.others")) {
                            targetPlayer = Bukkit.getPlayerExact(target);
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                            );
                            return true;
                        }
                    } else {
                        if (sender instanceof Player) {
                            targetPlayer = (Player) sender;
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Must specify a player").color(ChatColor.RED).create()
                            );
                            return true;
                        }
                    }
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
                            new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                    );
                }
            } else if (args[0].equalsIgnoreCase("kill") || args[0].equalsIgnoreCase("k")) {
                if (sender.hasPermission("spirits.kill")) {
                    Player targetPlayer = null;
                    if (args.length > 1) {
                        String target = args[1];
                        if (sender.hasPermission("spirits.kill.others")) {
                            targetPlayer = Bukkit.getPlayerExact(target);
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                            );
                            return true;
                        }
                    } else {
                        if (sender instanceof Player) {
                            targetPlayer = (Player) sender;
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Must specify a player").color(ChatColor.RED).create()
                            );
                            return true;
                        }
                    }
                    if (targetPlayer != null) {
                        graveyard.setAlive(targetPlayer, false);
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
                            new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                    );
                }
            } else if (args[0].equalsIgnoreCase("hardcore") || args[0].equalsIgnoreCase("hc")) {
                if (sender.hasPermission("spirits.hardcore")) {
                    Player targetPlayer = null;
                    if (args.length > 1) {
                        String target = args[1];
                        if (sender.hasPermission("spirits.hardcore.others")) {
                            targetPlayer = Bukkit.getPlayerExact(target);
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                            );
                            return true;
                        }
                    } else {
                        if (sender instanceof Player) {
                            targetPlayer = (Player) sender;
                        } else {
                            sender.spigot().sendMessage(
                                    new ComponentBuilder().append("Must specify a player").color(ChatColor.RED).create()
                            );
                            return true;
                        }
                    }
                    if (targetPlayer != null) {
                        graveyard.setLives(targetPlayer, 1);
                        sender.spigot().sendMessage(
                                new ComponentBuilder().append(targetPlayer.getDisplayName()).append(" is now in hardcore mode.").color(ChatColor.GREEN).create()
                        );
                    } else {
                        sender.spigot().sendMessage(
                                new ComponentBuilder().append("Player not found.").color(ChatColor.RED).create()
                        );
                    }
                } else {
                    sender.spigot().sendMessage(
                            new ComponentBuilder().append("Insufficient Permissions.").color(ChatColor.RED).create()
                    );
                }
            } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
                sender.spigot().sendMessage(helpMessage);
            } else {
                sender.spigot().sendMessage(
                        new ComponentBuilder().append("Invalid subcommand, try /spirits help").color(ChatColor.RED).create()
                );
            }
        } else {
            sender.spigot().sendMessage(helpMessage);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //List<String> subCommands = new ArrayList<String>();
        return null;
    }
}
