package me.accountzero.morecraft.events;

import me.accountzero.morecraft.MorecraftTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomMobsWitchPotionTest extends MorecraftTest {

    // Mirrors the allowed-effects list in CustomMobs#createRandomLevelFivePotion; kept here so the
    // test verifies the actual observable contract (only these types, at level 5) rather than internals.
    private static final List<PotionEffectType> ALLOWED_EFFECTS = List.of(
            PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.MINING_FATIGUE, PotionEffectType.REGENERATION, PotionEffectType.SPEED,
            PotionEffectType.INSTANT_HEALTH, PotionEffectType.HASTE);

    private Location spawnLocation() {
        return new Location(world, 0, 64, 0);
    }

    @Test
    void witchThrownPotionIsReplacedWithARandomLevelFiveEffect() {
        Witch witch = world.spawn(spawnLocation(), Witch.class, null,
                CreatureSpawnEvent.SpawnReason.CUSTOM, true, false);
        ThrownPotion potion = world.spawn(spawnLocation(), ThrownPotion.class);
        potion.setShooter(witch);

        new ProjectileLaunchEvent(potion).callEvent();

        ItemStack item = potion.getItem();
        assertEquals(Material.SPLASH_POTION, item.getType());
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        List<PotionEffect> effects = meta.getCustomEffects();
        assertEquals(1, effects.size());
        PotionEffect effect = effects.get(0);
        assertTrue(ALLOWED_EFFECTS.contains(effect.getType()));
        assertEquals(240, effect.getDuration());
        assertEquals(4, effect.getAmplifier());
    }

    @Test
    void nonWitchThrownPotionIsUnaffected() {
        PlayerMock player = server.addPlayer();
        ThrownPotion potion = world.spawn(spawnLocation(), ThrownPotion.class);
        potion.setShooter(player);
        ItemStack originalItem = potion.getItem();

        new ProjectileLaunchEvent(potion).callEvent();

        assertEquals(originalItem, potion.getItem());
    }
}
