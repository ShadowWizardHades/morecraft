package me.accountzero.morecraft.events;

import me.accountzero.morecraft.Morecraft;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class CustomMobs implements Listener {
    Random random = new Random();
    private final NamespacedKey zombieTierKey = new NamespacedKey(Morecraft.getInstance(), "zombie_tier");

    private static final int TIER_LEATHER = 0;
    private static final int TIER_GOLD = 1;
    private static final int TIER_IRON = 2;
    private static final int TIER_DIAMOND = 3;

    private void setAttr(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }

    // Zeroes drop chance on the gear we equipped so it isn't a free armor/weapon farm, while natural loot (rotten flesh, carrots, potatoes, etc.) is untouched.
    private void zeroGearDropChances(Zombie zombie) {
        EntityEquipment equipment = zombie.getEquipment();
        equipment.setHelmetDropChance(0f);
        equipment.setChestplateDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setItemInMainHandDropChance(0f);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            int zombieRand = random.nextInt(200);
            int zombieTier;
            if(zombieRand <= 110){
                zombieTier = TIER_LEATHER;
                zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                setAttr(zombie, Attribute.GENERIC_MOVEMENT_SPEED, 0.3);
                setAttr(zombie, Attribute.GENERIC_MAX_HEALTH, 30);
                zombie.setHealth(30.0);
            } else if(zombieRand <= 180){
                zombieTier = TIER_GOLD;
                zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
                zombie.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
                setAttr(zombie, Attribute.GENERIC_MOVEMENT_SPEED, 0.33);
                setAttr(zombie, Attribute.GENERIC_MAX_HEALTH, 36);
                zombie.setHealth(36.0);
            } else if(zombieRand < 199){
                zombieTier = TIER_IRON;
                zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                setAttr(zombie, Attribute.GENERIC_MOVEMENT_SPEED, 0.36);
                setAttr(zombie, Attribute.GENERIC_MAX_HEALTH, 42);
                zombie.setHealth(42.0);
            } else {
                zombieTier = TIER_DIAMOND;
                zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                setAttr(zombie, Attribute.GENERIC_MOVEMENT_SPEED, 0.42);
                setAttr(zombie, Attribute.GENERIC_MAX_HEALTH, 60);
                zombie.setHealth(60.0);
            }
            zeroGearDropChances(zombie);
            zombie.getPersistentDataContainer().set(zombieTierKey, PersistentDataType.INTEGER, zombieTier);
        }
        if (event.getEntityType() == EntityType.SKELETON) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            skeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            setAttr(skeleton, Attribute.GENERIC_MOVEMENT_SPEED, 0.35);
            setAttr(skeleton, Attribute.GENERIC_MAX_HEALTH, 30);
            skeleton.setHealth(30.0);
        }
        if (event.getEntityType() == EntityType.SPIDER) {
            Spider spider = (Spider) event.getEntity();
            setAttr(spider, Attribute.GENERIC_MOVEMENT_SPEED, 0.69);
            setAttr(spider, Attribute.GENERIC_JUMP_STRENGTH, 0.8);
            setAttr(spider, Attribute.GENERIC_ATTACK_DAMAGE, 0.5);
            setAttr(spider, Attribute.GENERIC_MAX_HEALTH, 24);
            spider.setHealth(24.0);
        }
        if (event.getEntityType() == EntityType.RABBIT) {
            Rabbit rabbit = (Rabbit) event.getEntity();
            setAttr(rabbit, Attribute.GENERIC_JUMP_STRENGTH, 1.2);
            setAttr(rabbit, Attribute.GENERIC_SAFE_FALL_DISTANCE, 8);
            setAttr(rabbit, Attribute.GENERIC_MAX_HEALTH, 6);
            rabbit.setHealth(6.0);
        }
        if (event.getEntityType() == EntityType.IRON_GOLEM) {
            IronGolem ironGolem = (IronGolem) event.getEntity();
            setAttr(ironGolem, Attribute.GENERIC_MOVEMENT_SPEED, 0.50);
            setAttr(ironGolem, Attribute.GENERIC_MAX_HEALTH, 150);
            ironGolem.setHealth(150.0);
        }
        if (event.getEntityType() == EntityType.SNOW_GOLEM) {
            // They will not die when exposed to sun + maybe repurpose as sentry gun ?
            Snowman snowGolem = (Snowman) event.getEntity();
            setAttr(snowGolem, Attribute.GENERIC_MAX_HEALTH, 40);
            snowGolem.setHealth(40.0);
        }
        if (event.getEntityType() == EntityType.PHANTOM) {
            Phantom phantom = (Phantom) event.getEntity();
            setAttr(phantom, Attribute.GENERIC_MAX_HEALTH, 40);
            phantom.setHealth(40.0);
        }
        if (event.getEntityType() == EntityType.CHICKEN) {
            Chicken chicken = (Chicken) event.getEntity();
            setAttr(chicken, Attribute.GENERIC_MOVEMENT_SPEED, 1);
        }
//        if (event.getEntity() instanceof LivingEntity) {
//            updateHealthBar((LivingEntity) event.getEntity());
//        }
    }

    @EventHandler
    public void onZombieDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;

        Integer tier = zombie.getPersistentDataContainer().get(zombieTierKey, PersistentDataType.INTEGER);
        if (tier == null) return;

        int bonusXp = switch (tier) {
            case TIER_GOLD -> 10;
            case TIER_IRON -> 25;
            case TIER_DIAMOND -> 50;
            default -> 0;
        };
        event.setDroppedExp(event.getDroppedExp() + bonusXp);
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
