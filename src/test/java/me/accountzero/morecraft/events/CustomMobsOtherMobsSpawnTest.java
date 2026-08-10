package me.accountzero.morecraft.events;

import me.accountzero.morecraft.MorecraftTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.attribute.AttributeInstanceMock;
import org.mockbukkit.mockbukkit.entity.LivingEntityMock;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomMobsOtherMobsSpawnTest extends MorecraftTest {

    private Location spawnLocation() {
        return new Location(world, 0, 64, 0);
    }

    // MockBukkit 4.110.0 has no default value for SAFE_FALL_DISTANCE, so Rabbit#registerAttribute(...)
    // NPEs for it. Inject the AttributeInstance directly to route around that mocking-library gap.
    private static void forceRegisterAttribute(LivingEntity entity, Attribute attribute, double value) throws ReflectiveOperationException {
        Field field = LivingEntityMock.class.getDeclaredField("attributes");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Attribute, AttributeInstanceMock> attributes = (Map<Attribute, AttributeInstanceMock>) field.get(entity);
        attributes.put(attribute, new AttributeInstanceMock(attribute, value));
    }

    @Test
    void skeletonGetsHelmetAndConfiguredStats() {
        Skeleton skeleton = world.spawn(spawnLocation(), Skeleton.class);

        assertEquals(Material.LEATHER_HELMET, skeleton.getEquipment().getHelmet().getType());
        assertEquals(0.35, skeleton.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
        assertEquals(30.0, skeleton.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(30.0, skeleton.getHealth());
    }

    @Test
    void spiderGetsConfiguredStats() {
        Spider spider = world.spawn(spawnLocation(), Spider.class, s -> {
            s.registerAttribute(Attribute.JUMP_STRENGTH);
            s.registerAttribute(Attribute.ATTACK_DAMAGE);
        }, CreatureSpawnEvent.SpawnReason.NATURAL);

        assertEquals(0.69, spider.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
        assertEquals(0.8, spider.getAttribute(Attribute.JUMP_STRENGTH).getBaseValue());
        assertEquals(0.5, spider.getAttribute(Attribute.ATTACK_DAMAGE).getBaseValue());
        assertEquals(24.0, spider.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(24.0, spider.getHealth());
    }

    @Test
    void rabbitGetsConfiguredStats() {
        Rabbit rabbit = world.spawn(spawnLocation(), Rabbit.class, r -> {
            r.registerAttribute(Attribute.JUMP_STRENGTH);
            try {
                forceRegisterAttribute(r, Attribute.SAFE_FALL_DISTANCE, 0.0);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }, CreatureSpawnEvent.SpawnReason.NATURAL);

        assertEquals(1.2, rabbit.getAttribute(Attribute.JUMP_STRENGTH).getBaseValue());
        assertEquals(8.0, rabbit.getAttribute(Attribute.SAFE_FALL_DISTANCE).getBaseValue());
        assertEquals(6.0, rabbit.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(6.0, rabbit.getHealth());
    }

    @Test
    void ironGolemGetsConfiguredStats() {
        IronGolem ironGolem = world.spawn(spawnLocation(), IronGolem.class);

        assertEquals(0.50, ironGolem.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
        assertEquals(150.0, ironGolem.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(150.0, ironGolem.getHealth());
    }

    @Test
    void snowGolemGetsConfiguredHealth() {
        Snowman snowman = world.spawn(spawnLocation(), Snowman.class);

        assertEquals(40.0, snowman.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(40.0, snowman.getHealth());
    }

    @Test
    void phantomGetsConfiguredHealth() {
        Phantom phantom = world.spawn(spawnLocation(), Phantom.class);

        assertEquals(40.0, phantom.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        assertEquals(40.0, phantom.getHealth());
    }

    @Test
    void chickenGetsConfiguredSpeed() {
        Chicken chicken = world.spawn(spawnLocation(), Chicken.class);

        assertEquals(1.0, chicken.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue());
    }
}
