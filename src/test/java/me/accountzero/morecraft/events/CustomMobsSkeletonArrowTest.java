package me.accountzero.morecraft.events;

import me.accountzero.morecraft.MorecraftTest;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomMobsSkeletonArrowTest extends MorecraftTest {

    private Location spawnLocation() {
        return new Location(world, 0, 64, 0);
    }

    @Test
    void skeletonArrowsGetVelocityMultiplied() {
        Skeleton skeleton = world.spawn(spawnLocation(), Skeleton.class, null,
                CreatureSpawnEvent.SpawnReason.CUSTOM, true, false);
        Arrow arrow = world.spawn(spawnLocation(), Arrow.class);
        arrow.setVelocity(new Vector(1, 0, 0));

        new EntityShootBowEvent(skeleton, null, arrow, 1.0f).callEvent();

        assertEquals(new Vector(3, 0, 0), arrow.getVelocity());
    }

    @Test
    void nonSkeletonShooterArrowsAreUnaffected() {
        Zombie zombie = world.spawn(spawnLocation(), Zombie.class, null,
                CreatureSpawnEvent.SpawnReason.CUSTOM, true, false);
        Arrow arrow = world.spawn(spawnLocation(), Arrow.class);
        arrow.setVelocity(new Vector(1, 0, 0));

        new EntityShootBowEvent(zombie, null, arrow, 1.0f).callEvent();

        assertEquals(new Vector(1, 0, 0), arrow.getVelocity());
    }
}
