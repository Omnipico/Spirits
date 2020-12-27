package com.omnipico.spirits;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProtocolListener {
    ProtocolManager manager;
    Spirits plugin;
    Graveyard graveyard;
    public ProtocolListener(ProtocolManager manager, Spirits plugin) {
        this.manager = manager;
        this.plugin = plugin;
        graveyard = new Graveyard(plugin.getConfig(), plugin);
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
                String team = packet.getStrings().read(0);
                Integer mode = packet.getIntegers().read(0);
                if (mode == 3) {
                    List<String> newEntities = new ArrayList<>((List<String>) packet.getSpecificModifier(Collection.class).read(0));
                    plugin.getLogger().info("Checking joins");
                    for (String entityIdentifier : newEntities.toArray(new String[0])) {
                        plugin.getLogger().info("Checking " + entityIdentifier + " to join " + team + " for " + player.getName());
                        if (entityIdentifier.length() < 32) {
                            /*If entity is a player, as far as I know, no players have a username 32 characters long
                            Note that I can't check for dashes, as there are also usernames with dashes.
                            I feel this is probably the safest bet.
                             */
                            Player teamPlayer = Bukkit.getPlayerExact(entityIdentifier);
                            if (teamPlayer == null) return;
                            if (teamPlayer.equals(player)) {
                                if (!plugin.getConfig().getString("dead_team", "Spirits").equals(team)) {
                                    plugin.getLogger().info("Not letting self (" + entityIdentifier + ") join " + team);
                                    newEntities.remove(entityIdentifier);
                                }
                            } else if ( (graveyard.isAlive(teamPlayer) && plugin.getConfig().getString("alive_team", "Humans").equals(team))
                                    || (!graveyard.isAlive(teamPlayer) && plugin.getConfig().getString("dead_team", "Spirits").equals(team))) {
                                //newEntities.add(entityIdentifier);
                            } else {
                                plugin.getLogger().info("Not letting " + entityIdentifier + " join " + team);
                                newEntities.remove(entityIdentifier);
                            }

                        } else {
                            //newEntities.add(entityIdentifier);
                        }
                    }
                    packet.getSpecificModifier(Collection.class).write(0, newEntities);
                    //packet.getStringArrays().write(0, (String[]) newEntities.toArray());
                } else if (mode == 4) {
                    //Strip out players from remove entity from team packet.
                    List<String> newEntities = new ArrayList<>((List<String>) packet.getSpecificModifier(Collection.class).read(0));
                    for (String entityIdentifier : newEntities.toArray(new String[0])) {
                        if (entityIdentifier.length() >= 32) {
                            /*If entity is not a player, as far as I know, no players have a username 32 characters long
                            Note that I can't check for dashes, as there are also usernames with dashes.
                            I feel this is probably the safest bet.
                             */
                            //newEntities.add(entityIdentifier);
                        } else {
                            newEntities.remove(entityIdentifier);
                        }
                    }
                    packet.getSpecificModifier(Collection.class).write(0, newEntities);
                    //packet.getStringArrays().write(0, (String[]) newEntities.toArray());
                }
            }
        });
    }
}
