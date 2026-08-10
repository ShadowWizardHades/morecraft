package me.accountzero.morecraft.events;

import me.accountzero.morecraft.MorecraftTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomMobsZombieSpawnTest extends MorecraftTest {

    private NamespacedKey zombieTierKey() {
        return new NamespacedKey(plugin, "zombie_tier");
    }

    // Forces CustomMobs' weighted roll so each tier can be tested deterministically instead of statistically.
    private static void forceRoll(CustomMobs mobs, int roll) throws ReflectiveOperationException {
        Field field = CustomMobs.class.getDeclaredField("random");
        field.setAccessible(true);
        field.set(mobs, new Random() {
            @Override
            public int nextInt(int bound) {
                return roll;
            }
        });
    }

    private Zombie spawnUnbuffedZombie() {
        return world.spawn(new Location(world, 0, 64, 0), Zombie.class, null,
                CreatureSpawnEvent.SpawnReason.CUSTOM, true, false);
    }

    private void assertZeroDropChances(Zombie zombie) {
        var equipment = zombie.getEquipment();
        assertEquals(0f, equipment.getHelmetDropChance());
        assertEquals(0f, equipment.getChestplateDropChance());
        assertEquals(0f, equipment.getLeggingsDropChance());
        assertEquals(0f, equipment.getBootsDropChance());
        assertEquals(0f, equipment.getItemInMainHandDropChance());
    }

    @Test
    void leatherTierGetsHelmetOnlyAndLeatherStats() throws ReflectiveOperationException {
        Zombie zombie = spawnUnbuffedZombie();
        CustomMobs mobs = new CustomMobs();
        forceRoll(mobs, 0); // 0 < 111 (leather weight)

        mobs.onCreatureSpawn(new CreatureSpawnEvent(zombie, CreatureSpawnEvent.SpawnReason.CUSTOM));

        assertEquals(Material.LEATHER_HELMET, zombie.getEquipment().getHelmet().getType());
        assertEquals(Material.AIR, zombie.getEquipment().getItemInMainHand().getType());
        assertEquals(30.0, zombie.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(0.3, zombie.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
        assertEquals(30.0, zombie.getHealth());
        assertEquals(0, zombie.getPersistentDataContainer().get(zombieTierKey(), PersistentDataType.INTEGER));
        assertZeroDropChances(zombie);
    }

    @Test
    void goldTierGetsHelmetAndSwordAndGoldStats() throws ReflectiveOperationException {
        Zombie zombie = spawnUnbuffedZombie();
        CustomMobs mobs = new CustomMobs();
        forceRoll(mobs, 150); // 111 <= 150 < 181 (leather + gold weight)

        mobs.onCreatureSpawn(new CreatureSpawnEvent(zombie, CreatureSpawnEvent.SpawnReason.CUSTOM));

        assertEquals(Material.GOLDEN_HELMET, zombie.getEquipment().getHelmet().getType());
        assertEquals(Material.GOLDEN_SWORD, zombie.getEquipment().getItemInMainHand().getType());
        assertEquals(36.0, zombie.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(0.33, zombie.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
        assertEquals(1, zombie.getPersistentDataContainer().get(zombieTierKey(), PersistentDataType.INTEGER));
        assertZeroDropChances(zombie);
    }

    @Test
    void ironTierGetsHelmetChestplateAndSwordAndIronStats() throws ReflectiveOperationException {
        Zombie zombie = spawnUnbuffedZombie();
        CustomMobs mobs = new CustomMobs();
        forceRoll(mobs, 190); // 181 <= 190 < 199 (leather + gold + iron weight)

        mobs.onCreatureSpawn(new CreatureSpawnEvent(zombie, CreatureSpawnEvent.SpawnReason.CUSTOM));

        assertEquals(Material.IRON_HELMET, zombie.getEquipment().getHelmet().getType());
        assertEquals(Material.IRON_CHESTPLATE, zombie.getEquipment().getChestplate().getType());
        assertEquals(Material.IRON_SWORD, zombie.getEquipment().getItemInMainHand().getType());
        assertEquals(42.0, zombie.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(0.36, zombie.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
        assertEquals(2, zombie.getPersistentDataContainer().get(zombieTierKey(), PersistentDataType.INTEGER));
        assertZeroDropChances(zombie);
    }

    @Test
    void diamondTierGetsFullArmorAndSwordAndDiamondStats() throws ReflectiveOperationException {
        Zombie zombie = spawnUnbuffedZombie();
        CustomMobs mobs = new CustomMobs();
        forceRoll(mobs, 199); // the last roll, reserved for diamond (weight 1)

        mobs.onCreatureSpawn(new CreatureSpawnEvent(zombie, CreatureSpawnEvent.SpawnReason.CUSTOM));

        assertEquals(Material.DIAMOND_HELMET, zombie.getEquipment().getHelmet().getType());
        assertEquals(Material.DIAMOND_CHESTPLATE, zombie.getEquipment().getChestplate().getType());
        assertEquals(Material.DIAMOND_LEGGINGS, zombie.getEquipment().getLeggings().getType());
        assertEquals(Material.DIAMOND_BOOTS, zombie.getEquipment().getBoots().getType());
        assertEquals(Material.DIAMOND_SWORD, zombie.getEquipment().getItemInMainHand().getType());
        assertEquals(60.0, zombie.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(0.42, zombie.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
        assertEquals(3, zombie.getPersistentDataContainer().get(zombieTierKey(), PersistentDataType.INTEGER));
        assertZeroDropChances(zombie);
    }

    @Test
    void naturalSpawnDispatchAlwaysProducesAConsistentKnownTier() {
        // End-to-end: goes through the plugin's real registered listener and real Random,
        // exercising the Morecraft.onEnable -> CreatureSpawnEvent wiring rather than calling
        // CustomMobs directly. Run many trials since the tier is randomly weighted.
        for (int i = 0; i < 300; i++) {
            Zombie zombie = world.spawn(new Location(world, 0, 64, 0), Zombie.class);

            Integer tier = zombie.getPersistentDataContainer().get(zombieTierKey(), PersistentDataType.INTEGER);
            assertNotNull(tier, "every spawned zombie must be tagged with a tier");
            assertTrue(tier >= 0 && tier <= 3, "tier must be one of the four known tiers");
            assertNotNull(zombie.getEquipment().getHelmet());
            assertTrue(zombie.getHealth() > 0);
            assertZeroDropChances(zombie);
        }
    }
}
