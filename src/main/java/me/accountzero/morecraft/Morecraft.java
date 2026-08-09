package me.accountzero.morecraft;

import me.accountzero.morecraft.events.CustomMobs;
import org.bukkit.plugin.java.JavaPlugin;

public final class Morecraft extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new CustomMobs(), this);
        CustomRecipes.register(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Morecraft getInstance() {
        return getPlugin(Morecraft.class);
    }
}
