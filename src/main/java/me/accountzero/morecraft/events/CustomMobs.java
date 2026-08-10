package me.accountzero.morecraft.events;

import me.accountzero.morecraft.CustomRecipes;
import me.accountzero.morecraft.Morecraft;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
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
    private final NamespacedKey bloodMedalionKey = new NamespacedKey(Morecraft.getInstance(), CustomRecipes.BLOOD_MEDALION_TAG);

    private static final int TIER_LEATHER = 0;
    private static final int TIER_GOLD = 1;
    private static final int TIER_IRON = 2;
    private static final int TIER_DIAMOND = 3;

    private void setAttr(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }

    private FileConfiguration config() {
        return Morecraft.getInstance().getConfig();
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

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        switch (event.getEntityType()) {
            case ZOMBIE -> buffZombie((Zombie) event.getEntity());
            case SKELETON -> buffSkeleton((Skeleton) event.getEntity());
            case SPIDER -> buffSpider((Spider) event.getEntity());
            case RABBIT -> buffRabbit((Rabbit) event.getEntity());
            case IRON_GOLEM -> buffIronGolem((IronGolem) event.getEntity());
            case SNOW_GOLEM -> buffSnowGolem((Snowman) event.getEntity());
            case PHANTOM -> buffPhantom((Phantom) event.getEntity());
            case CHICKEN -> buffChicken((Chicken) event.getEntity());
            default -> {}
        }
    }

    private void buffZombie(Zombie zombie) {
        FileConfiguration config = config();
        int leatherWeight = config.getInt("mobs.zombie.tiers.leather.weight", 111);
        int goldWeight = config.getInt("mobs.zombie.tiers.gold.weight", 70);
        int ironWeight = config.getInt("mobs.zombie.tiers.iron.weight", 18);
        int diamondWeight = config.getInt("mobs.zombie.tiers.diamond.weight", 1);

        int roll = random.nextInt(leatherWeight + goldWeight + ironWeight + diamondWeight);
        int zombieTier;
        if (roll < leatherWeight) {
            zombieTier = TIER_LEATHER;
            zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            applyZombieTierStats(zombie, config, "leather", 0.3, 30.0);
        } else if (roll < leatherWeight + goldWeight) {
            zombieTier = TIER_GOLD;
            zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
            applyZombieTierStats(zombie, config, "gold", 0.33, 36.0);
        } else if (roll < leatherWeight + goldWeight + ironWeight) {
            zombieTier = TIER_IRON;
            zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            applyZombieTierStats(zombie, config, "iron", 0.36, 42.0);
        } else {
            zombieTier = TIER_DIAMOND;
            zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
            applyZombieTierStats(zombie, config, "diamond", 0.42, 60.0);
        }
        zeroGearDropChances(zombie);
        zombie.getPersistentDataContainer().set(zombieTierKey, PersistentDataType.INTEGER, zombieTier);
    }

    private void applyZombieTierStats(Zombie zombie, FileConfiguration config, String tier, double defaultSpeed, double defaultHealth) {
        double speed = config.getDouble("mobs.zombie.tiers." + tier + ".speed", defaultSpeed);
        double health = config.getDouble("mobs.zombie.tiers." + tier + ".health", defaultHealth);
        setAttr(zombie, Attribute.MOVEMENT_SPEED, speed);
        setAttr(zombie, Attribute.MAX_HEALTH, health);
        zombie.setHealth(health);
    }

    private void buffSkeleton(Skeleton skeleton) {
        FileConfiguration config = config();
        skeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        setAttr(skeleton, Attribute.MOVEMENT_SPEED, config.getDouble("mobs.skeleton.speed", 0.35));
        double health = config.getDouble("mobs.skeleton.health", 30.0);
        setAttr(skeleton, Attribute.MAX_HEALTH, health);
        skeleton.setHealth(health);
    }

    private void buffSpider(Spider spider) {
        FileConfiguration config = config();
        setAttr(spider, Attribute.MOVEMENT_SPEED, config.getDouble("mobs.spider.speed", 0.69));
        setAttr(spider, Attribute.JUMP_STRENGTH, config.getDouble("mobs.spider.jump-strength", 0.8));
        setAttr(spider, Attribute.ATTACK_DAMAGE, config.getDouble("mobs.spider.attack-damage", 0.5));
        double health = config.getDouble("mobs.spider.health", 24.0);
        setAttr(spider, Attribute.MAX_HEALTH, health);
        spider.setHealth(health);
    }

    private void buffRabbit(Rabbit rabbit) {
        FileConfiguration config = config();
        setAttr(rabbit, Attribute.JUMP_STRENGTH, config.getDouble("mobs.rabbit.jump-strength", 1.2));
        setAttr(rabbit, Attribute.SAFE_FALL_DISTANCE, config.getDouble("mobs.rabbit.safe-fall-distance", 8.0));
        double health = config.getDouble("mobs.rabbit.health", 6.0);
        setAttr(rabbit, Attribute.MAX_HEALTH, health);
        rabbit.setHealth(health);
    }

    private void buffIronGolem(IronGolem ironGolem) {
        FileConfiguration config = config();
        setAttr(ironGolem, Attribute.MOVEMENT_SPEED, config.getDouble("mobs.iron-golem.speed", 0.50));
        double health = config.getDouble("mobs.iron-golem.health", 150.0);
        setAttr(ironGolem, Attribute.MAX_HEALTH, health);
        ironGolem.setHealth(health);
    }

    private void buffSnowGolem(Snowman snowGolem) {
        // They will not die when exposed to sun + maybe repurpose as sentry gun ?
        double health = config().getDouble("mobs.snow-golem.health", 40.0);
        setAttr(snowGolem, Attribute.MAX_HEALTH, health);
        snowGolem.setHealth(health);
    }

    private void buffPhantom(Phantom phantom) {
        double health = config().getDouble("mobs.phantom.health", 40.0);
        setAttr(phantom, Attribute.MAX_HEALTH, health);
        phantom.setHealth(health);
    }

    private void buffChicken(Chicken chicken) {
        setAttr(chicken, Attribute.MOVEMENT_SPEED, config().getDouble("mobs.chicken.speed", 1.0));
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
    public void onBloodMedalionDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (isHoldingBloodMedalion(player)) {
            event.setDamage(event.getDamage() * 2);
        }
    }

    private boolean isHoldingBloodMedalion(Player player) {
        return hasBloodMedalionTag(player.getInventory().getItemInMainHand())
                || hasBloodMedalionTag(player.getInventory().getItemInOffHand());
    }

    private boolean hasBloodMedalionTag(ItemStack item) {
        if (item.getItemMeta() == null) return false;
        return item.getItemMeta().getPersistentDataContainer().has(bloodMedalionKey, PersistentDataType.BYTE);
    }

    @EventHandler
    public void onEntityShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Skeleton) {
            Arrow arrow = (Arrow) event.getProjectile();
            double multiplier = config().getDouble("mobs.skeleton.arrow-velocity-multiplier", 3.0);
            arrow.setVelocity(arrow.getVelocity().multiply(multiplier));
        }
    }

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

        FileConfiguration config = config();
        int durationTicks = config.getInt("witch.potion-duration-ticks", 240);
        int amplifier = config.getInt("witch.potion-amplifier", 4);
        meta.addCustomEffect(new PotionEffect(randomEffect, durationTicks, amplifier), true);

        potion.setItemMeta(meta);
        return potion;
    }
}
