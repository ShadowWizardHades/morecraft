package me.accountzero.morecraft.events;

import me.accountzero.morecraft.MorecraftTest;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomMobsZombieDeathTest extends MorecraftTest {

    private NamespacedKey zombieTierKey() {
        return new NamespacedKey(plugin, "zombie_tier");
    }

    private Zombie spawnUnbuffedZombie() {
        return world.spawn(new Location(world, 0, 64, 0), Zombie.class, null,
                CreatureSpawnEvent.SpawnReason.CUSTOM, true, false);
    }

    private int killAndGetDroppedExp(Zombie zombie) {
        EntityDeathEvent event = new EntityDeathEvent(zombie, DamageSource.builder(DamageType.GENERIC).build(), List.of(), 5);
        event.callEvent();
        return event.getDroppedExp();
    }

    @ParameterizedTest
    @CsvSource({
            "0, 5",  // leather: no bonus
            "1, 15", // gold: +10
            "2, 30", // iron: +25
            "3, 55", // diamond: +50
    })
    void zombieDeathGrantsBonusXpMatchingItsTier(int tier, int expectedDroppedExp) {
        Zombie zombie = spawnUnbuffedZombie();
        zombie.getPersistentDataContainer().set(zombieTierKey(), PersistentDataType.INTEGER, tier);

        assertEquals(expectedDroppedExp, killAndGetDroppedExp(zombie));
    }

    @Test
    void untaggedZombieGetsNoBonusXp() {
        Zombie zombie = spawnUnbuffedZombie();

        assertEquals(5, killAndGetDroppedExp(zombie));
    }
}
