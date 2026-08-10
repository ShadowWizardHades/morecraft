package me.accountzero.morecraft;

import me.accountzero.morecraft.events.CustomMobs;
import org.bukkit.plugin.java.JavaPlugin;

public class Morecraft extends JavaPlugin {

    private static Morecraft instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new CustomMobs(), this);
        CustomRecipes.register(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Morecraft getInstance() {
        return instance;
    }
}
