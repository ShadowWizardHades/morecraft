package me.accountzero.morecraft.events;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class CustomMobs implements Listener {
    Random random = new Random();
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
            zombie.setHealth(30.0);
        }
        if (event.getEntityType() == EntityType.SKELETON) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            skeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
            skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
            skeleton.setHealth(30.0);
        }
        if (event.getEntityType() == EntityType.SPIDER) {
            Spider spider = (Spider) event.getEntity();
            spider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.69);
            spider.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(0.8);
            spider.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
            spider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
            spider.setHealth(24.0);
        }
        if (event.getEntityType() == EntityType.RABBIT) {
            Rabbit rabbit = (Rabbit) event.getEntity();
            rabbit.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(1.2);
            rabbit.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(8);
            rabbit.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
            rabbit.setHealth(6.0);
        }
        if (event.getEntityType() == EntityType.IRON_GOLEM) {
            IronGolem ironGolem = (IronGolem) event.getEntity();
            ironGolem.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.50);
            ironGolem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
            ironGolem.setHealth(150.0);
        }
        if (event.getEntityType() == EntityType.SNOW_GOLEM) {
            // They will not die when exposed to sun + maybe repurpose as sentry gun ?
            Snowman snowGolem = (Snowman) event.getEntity();
            snowGolem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            snowGolem.setHealth(40.0);
        }
        if (event.getEntityType() == EntityType.PHANTOM) {
            Phantom phantom = (Phantom) event.getEntity();
            phantom.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            phantom.setHealth(40.0);
        }
        if (event.getEntityType() == EntityType.CHICKEN) {
            Chicken chicken = (Chicken) event.getEntity();
            chicken.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(1);
        }
//        if (event.getEntity() instanceof LivingEntity) {
//            updateHealthBar((LivingEntity) event.getEntity());
//        }
    }

    @EventHandler
    public void onEntityShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Skeleton) {
            Arrow arrow = (Arrow) event.getProjectile();

            // Increase speed (multiply vector)
            arrow.setVelocity(arrow.getVelocity().multiply(3)); // shoots faster/further
        }
    }

//    @EventHandler
//    public void onEntityDamage(EntityDamageEvent event) {
//        if (event.getEntity() instanceof LivingEntity) {
//            LivingEntity entity = (LivingEntity) event.getEntity();
//
//            double newHealth = entity.getHealth() - event.getFinalDamage();
//            entity.setHealth(Math.max(newHealth, 0)); // Prevent negative health
//
//            updateHealthBar(entity);
//        }
//    }

    @EventHandler
    public void onWitchThrowPotion(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Witch && event.getEntity() instanceof ThrownPotion potion) {

            ItemStack customPotion = createRandomLevelFivePotion();
            potion.setItem(customPotion);
        }
    }

    private ItemStack createRandomLevelFivePotion() {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        List<PotionEffectType> ALLOWED_POTION_TYPES = List.of(PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.INSTANT_DAMAGE, PotionEffectType.MINING_FATIGUE, PotionEffectType.REGENERATION, PotionEffectType.SPEED, PotionEffectType.INSTANT_HEALTH, PotionEffectType.HASTE);

        PotionEffectType randomEffect = (PotionEffectType) ALLOWED_POTION_TYPES.get(random.nextInt(ALLOWED_POTION_TYPES.size()));

        meta.addCustomEffect(new PotionEffect(randomEffect, 240, 4), true);

        potion.setItemMeta(meta);
        return potion;
    }

//    private void updateHealthBar(LivingEntity entity) {
//        // Get current and max health
//        double currentHealth = Math.max(entity.getHealth(), 0); // Prevent negatives
//        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
//
//        // Format health bar as "currentHealth / maxHealth"
//        String healthBar = String.format("§c%.0f §f/ §a%.0f", currentHealth, maxHealth);
//
//        // Set the custom name to the health bar and make it visible
//        entity.setCustomName(healthBar);
//        entity.setCustomNameVisible(true);
//    }
}
