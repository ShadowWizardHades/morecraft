package me.accountzero.morecraft.events;

import me.accountzero.morecraft.MorecraftTest;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation") // the 3-arg EntityDamageEvent(...) constructor is deprecated but still functional
class CustomMobsBloodMedalionTest extends MorecraftTest {

    private ItemStack bloodMedalion() {
        NamespacedKey key = new NamespacedKey(plugin, "blood_medalion");
        ShapedRecipe recipe = (ShapedRecipe) server.getRecipe(key);
        return recipe.getResult().clone();
    }

    private double damagePlayer(PlayerMock player, double baseDamage) {
        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, baseDamage);
        event.callEvent();
        return event.getDamage();
    }

    @Test
    void unarmedPlayerTakesUnmodifiedDamage() {
        PlayerMock player = server.addPlayer();

        assertEquals(10.0, damagePlayer(player, 10.0));
    }

    @Test
    void medallionInMainHandDoublesIncomingDamage() {
        PlayerMock player = server.addPlayer();
        player.getInventory().setItemInMainHand(bloodMedalion());

        assertEquals(20.0, damagePlayer(player, 10.0));
    }

    @Test
    void medallionInOffHandDoublesIncomingDamage() {
        PlayerMock player = server.addPlayer();
        player.getInventory().setItemInOffHand(bloodMedalion());

        assertEquals(20.0, damagePlayer(player, 10.0));
    }

    @Test
    void unrelatedRedstoneItemDoesNotDoubleDamage() {
        PlayerMock player = server.addPlayer();
        player.getInventory().setItemInMainHand(new ItemStack(Material.REDSTONE));

        assertEquals(10.0, damagePlayer(player, 10.0));
    }
}
