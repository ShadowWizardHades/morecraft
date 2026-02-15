package me.accountzero.morecraft.events;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

public class CustomMobs implements Listener {
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            zombie.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            zombie.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
            zombie.setHealth(30.0);
        }
        if (event.getEntityType() == EntityType.SKELETON) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            skeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            skeleton.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
            skeleton.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
            skeleton.setHealth(30.0);
        }
        if (event.getEntityType() == EntityType.SPIDER) {
            Spider spider = (Spider) event.getEntity();
            spider.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.69);
            spider.getAttribute(org.bukkit.attribute.Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(1.8);
            spider.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
            spider.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
            spider.setHealth(24.0);
        }

        if (event.getEntityType() == EntityType.RABBIT) {
            Rabbit rabbit = (Rabbit) event.getEntity();
            rabbit.getAttribute(org.bukkit.attribute.Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(1.2);
            rabbit.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(8);
            rabbit.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
            rabbit.setHealth(6.0);
        }
        if (event.getEntityType() == EntityType.IRON_GOLEM) {
            IronGolem iron_golem = (IronGolem) event.getEntity();
            iron_golem.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.50);
            iron_golem.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
            iron_golem.setHealth(150.0);
        }
        if (event.getEntityType() == EntityType.SNOW_GOLEM) {
            // They will not die when exposed to sun + maybe repurpose as sentry gun ?
            Snowman snow_golem = (Snowman) event.getEntity();
            snow_golem.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            snow_golem.setHealth(40.0);
        }
        if (event.getEntityType() == EntityType.PHANTOM) {
            Phantom phantom = (Phantom) event.getEntity();
            phantom.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            phantom.setHealth(40.0);
        }
        if (event.getEntity() instanceof LivingEntity) {
            updateHealthBar((LivingEntity) event.getEntity());
        }
    }

    @EventHandler
    public void onEntityShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Skeleton) {
            Arrow arrow = (Arrow) event.getProjectile();

            // Increase speed (multiply vector)
            arrow.setVelocity(arrow.getVelocity().multiply(3)); // shoots faster/further
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();

            double newHealth = entity.getHealth() - event.getFinalDamage();
            entity.setHealth(Math.max(newHealth, 0)); // Prevent negative health

            updateHealthBar(entity);
        }
    }

    private void updateHealthBar(LivingEntity entity) {
        // Get current and max health
        double currentHealth = Math.max(entity.getHealth(), 0); // Prevent negatives
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        // Format health bar as "currentHealth / maxHealth"
        String healthBar = String.format("§c%.0f §f/ §a%.0f", currentHealth, maxHealth);

        // Set the custom name to the health bar and make it visible
        entity.setCustomName(healthBar);
        entity.setCustomNameVisible(true);
    }
}
