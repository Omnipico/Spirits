package com.omnipico.spirits;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Spirits extends JavaPlugin {
    Chat chat;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null){
            chat = getServer().getServicesManager().load(Chat.class);
        }
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        ProtocolListener listener = new ProtocolListener(manager, this);
        FileConfiguration config = this.getConfig();
        //Fired when the server enables the plugin
        CommandSpirits commandSpirits = new CommandSpirits(this);
        this.getCommand("spirits").setExecutor(commandSpirits);
        this.getCommand("spirits").setTabCompleter(commandSpirits);
        getServer().getPluginManager().registerEvents(new EventListener(manager, config, this), this);
    }

    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins
    }
}
