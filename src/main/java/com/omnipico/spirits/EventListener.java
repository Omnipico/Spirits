package com.omnipico.spirits;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.chat.Chat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class EventListener implements Listener {
    //TODO: Make this a config option
    FileConfiguration config;
    ProtocolManager manager;
    Spirits plugin;
    Graveyard graveyard;

    public EventListener(ProtocolManager manager, FileConfiguration config, Spirits plugin) {
        this.manager = manager;
        this.config = config;
        this.plugin = plugin;
        graveyard = new Graveyard(config, plugin);
    }

    private PacketContainer createTeamJoinPacket(List<String> players, boolean alive) {
        PacketContainer teamJoinPacket = manager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        teamJoinPacket.getModifier().writeDefaults();
        if (alive) {
            teamJoinPacket.getStrings().write(0, config.getString("alive_team", "Humans"));
        } else {
            teamJoinPacket.getStrings().write(0, config.getString("dead_team", "Spirits"));
        }
        teamJoinPacket.getIntegers().write(0, 3);
        teamJoinPacket.getSpecificModifier(Collection.class).write(0, players);
        return teamJoinPacket;
    }

    @EventHandler(priority = EventPriority.NORMAL) // Listening for the event.
    public void onPlayerJoin(PlayerJoinEvent event) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        ArrayList<String> alive = new ArrayList<>();
        ArrayList<String> aliveClient = new ArrayList<>();
        ArrayList<String> dead = new ArrayList<>();
        ArrayList<String> deadClient = new ArrayList<>();
        for (Player player : players) {
            if (graveyard.isAlive(player)) {
                plugin.getLogger().info(player.getName() + " is alive, adding to alive ");
                alive.add(player.getName());
            } else {
                plugin.getLogger().info(player.getName() + " is dead, adding to dead ");
                dead.add(player.getName());
            }
            if (graveyard.isAlive(player) && !event.getPlayer().equals(player)) {
                aliveClient.add(player.getName());
            } else {
                deadClient.add(player.getName());
            }
        }
        PacketContainer aliveAllPacket = createTeamJoinPacket(alive, true);
        PacketContainer deadAllPacket = createTeamJoinPacket(dead, false);
        PacketContainer aliveClientPacket = createTeamJoinPacket(aliveClient, true);
        PacketContainer deadClientPacket = createTeamJoinPacket(deadClient, false);
        for (Player player : players) {
            try {
                if (!event.getPlayer().equals(player)) {
                    manager.sendServerPacket(player, aliveAllPacket);
                    manager.sendServerPacket(player, deadAllPacket);
                } else {
                    manager.sendServerPacket(player, aliveClientPacket);
                    manager.sendServerPacket(player, deadClientPacket);
                }

            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL) // Listening for the event.
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        //TODO allow for canon deaths
        if (graveyard.isAlive(player)) {
            boolean died = graveyard.useLife(player);
            if (died) {
                player.spigot().sendMessage(new ComponentBuilder().append("You lost your last life, you are now a Spirit!").color(ChatColor.GREEN).create());
                //Join them to the ghost team for everyone.
                ArrayList<String> playersGhosted = new ArrayList<>();
                playersGhosted.add(player.getName());
                PacketContainer ghostedPacket = createTeamJoinPacket(playersGhosted, false);
                for (Player playerTo : Bukkit.getOnlinePlayers()) {
                    try {
                        if (!player.equals(playerTo)) {
                            manager.sendServerPacket(playerTo, ghostedPacket);
                        }
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                int lives = graveyard.getLives(player);
                if (lives != 1) {
                    player.spigot().sendMessage(new ComponentBuilder().append("You are now down to " + String.valueOf(lives) + " lives.").color(ChatColor.GREEN).create());
                } else {
                    player.spigot().sendMessage(new ComponentBuilder().append("You are now down to " + String.valueOf(lives) + " life.").color(ChatColor.GREEN).create());
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL) // Listening for the event.
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!graveyard.isAlive(player)) {
            //Perma invisibility
            //Must be ran one tick after respawning.
            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false, false);
                    player.addPotionEffect(invisibility);
                }
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL) // Listening for the event.
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType().equals(Material.MILK_BUCKET) && !graveyard.isAlive(player)) {
            //Give their invis back 1 tick later.
            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false, false);
                    player.addPotionEffect(invisibility);
                }
            }, 1L);
        }
    }

}