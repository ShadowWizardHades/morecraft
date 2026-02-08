package me.accountzero.morecraft;

import me.accountzero.morecraft.events.CustomMobs;
import me.accountzero.morecraft.events.CustomPlayers;
import org.bukkit.plugin.java.JavaPlugin;

public final class Morecraft extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CustomMobs(), this);
        getServer().getPluginManager().registerEvents(new CustomPlayers(), this);
        CustomRecipes.register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Morecraft getInstance() {
        return getPlugin(Morecraft.class);
    }
}
