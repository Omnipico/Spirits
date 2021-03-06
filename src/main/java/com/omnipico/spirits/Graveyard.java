package com.omnipico.spirits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class Graveyard {
    FileConfiguration config;
    Spirits plugin;
    public Graveyard(FileConfiguration config, Spirits plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    public boolean isAlive(Player player) {
        List<String> spirits = this.config.getStringList("spirits");
        return !spirits.contains(player.getUniqueId().toString());
    }

    public int getLives(Player player) {
        ConfigurationSection lives = this.config.getConfigurationSection("lives");
        if (lives != null) {
            if (!lives.contains(player.getUniqueId().toString())) {
                plugin.getLogger().info("Initializing " + player.getName() + " to X lives");
                lives.set(player.getUniqueId().toString(), this.config.getInt("non_canon_lives", 5));
                plugin.saveConfig();
            }
            plugin.getLogger().info("Retrieving lives: " + lives.getInt(player.getUniqueId().toString(), this.config.getInt("non_canon_lives", 5)));
            return lives.getInt(player.getUniqueId().toString(), this.config.getInt("non_canon_lives", 5));
        }

        return this.config.getInt("non_canon_lives", 5);
    }

    public void setLives(Player player, int lives) {
        ConfigurationSection livesSection = this.config.getConfigurationSection("lives");
        if (livesSection != null) {
            livesSection.set(player.getUniqueId().toString(), lives);
            plugin.saveConfig();
        }
    }

    public void setAlive(Player player, boolean alive) {
        if (alive && !isAlive(player)) {
            List<String> spirits = this.config.getStringList("spirits");
            spirits.remove(player.getUniqueId().toString());
            this.config.set("spirits", spirits);
            plugin.saveConfig();
            setLives(player, this.config.getInt("non_canon_lives", 5));
            PotionEffect noInvisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 0, 2, true, false, false);
            player.addPotionEffect(noInvisibility);
            if (this.config.getBoolean("use_reincarnation_location", true)) {
                List<World> worlds = Bukkit.getWorlds();
                if (worlds.size() > 0) {
                    World world = Bukkit.getWorlds().get(0);
                    Location reincarnationLocation;
                    if (this.config.getBoolean("reincarnate_at_spawn", true)) {
                        reincarnationLocation = world.getSpawnLocation().clone();
                        reincarnationLocation.add(0.5,0, 0.5);
                    } else {
                        reincarnationLocation = new Location(world,
                                this.config.getDouble("reincarnation_x", 0),
                                this.config.getDouble("reincarnation_y", 64),
                                this.config.getDouble("reincarnation_z", 0)
                                );
                    }
                    player.teleport(reincarnationLocation);
                }
            }
        } else if (!alive && isAlive(player)) {
            List<String> spirits = this.config.getStringList("spirits");
            spirits.add(player.getUniqueId().toString());
            this.config.set("spirits", spirits);
            plugin.saveConfig();
            setLives(player, 0);
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

    public boolean useLife(Player player) {
        if (isAlive(player)) {
            int lives = getLives(player) - 1;
            if (lives <= 0) {
                setAlive(player, false);
                return true;
            } else {
                setLives(player, lives);
                return false;
            }
        } else {
            return false;
        }
    }
}
