package me.accountzero.morecraft.events;

import me.accountzero.morecraft.Morecraft;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomPlayers implements Listener {

    private Map<UUID, Boolean> playersInAir = new HashMap<>();
    private Map<UUID, Long> lastJumpTime = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Sprawdź czy to nasze custom buty
        if (!isJumpBoots(item)) return;

        // Sprawdź czy kliknął PPM
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            // Cooldown - 3 sekundy
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();

            if (lastJumpTime.containsKey(playerId)) {
                if (currentTime - lastJumpTime.get(playerId) < 3000) {
                    player.sendMessage("§cMusisz poczekać przed następnym skokiem!");
                    return;
                }
            }

            // Wykonaj super skok
            performSuperJump(player);
            lastJumpTime.put(playerId, currentTime);
            playersInAir.put(playerId, true);
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // Sprawdź czy gracz ma nasze buty
        ItemStack boots = player.getInventory().getBoots();
        if (!isJumpBoots(boots)) return;

        // Sprawdź czy gracz zaczyna sneakować i jest w powietrzu
        if (event.isSneaking() && isPlayerInAir(player)) {
            UUID playerId = player.getUniqueId();

            // Sprawdź czy gracz był w powietrzu przez super skok
            if (playersInAir.getOrDefault(playerId, false)) {
                performSlamAttack(player);
                playersInAir.put(playerId, false);
            }
        }
    }

    private boolean isJumpBoots(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey(Morecraft.getInstance(), "stomp_boots"),
                org.bukkit.persistence.PersistentDataType.STRING
        );
    }

    private boolean isPlayerInAir(Player player) {
        return !player.isOnGround() && player.getVelocity().getY() < 0;
    }

    private void performSuperJump(Player player) {
        // Nadaj graczowi prędkość do góry
        Vector velocity = player.getVelocity();
        velocity.setY(2.0); // Siła skoku
        player.setVelocity(velocity);

        // Efekty wizualne i dźwiękowe
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.5, 0.1, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.5f);

        player.sendMessage("§a§lWHOOSH! Super skok!");
    }

    private void performSlamAttack(Player player) {
        // Zwiększ prędkość w dół
        Vector velocity = player.getVelocity();
        velocity.setY(-3.0); // Siła slam attack
        player.setVelocity(velocity);

        // Zadaj obrażenia pobliskim mobom po uderzeniu w ziemię
        Bukkit.getScheduler().runTaskLater(Morecraft.getInstance(), () -> {
            if (player.isOnGround()) {
                performGroundSlam(player);
            }
        }, 5L); // Sprawdź po 5 tickach

        // Efekty podczas spadania
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 30, 0.3, 0.3, 0.3, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.8f);

        player.sendMessage("§c§lSLAM ATTACK!");
    }

    private void performGroundSlam(Player player) {
        World world = player.getWorld();

        // Wielkie efekty wizualne
        world.spawnParticle(Particle.EXPLOSION, player.getLocation(), 5, 2.0, 0.1, 2.0, 0);
        world.spawnParticle(Particle.EGG_CRACK, player.getLocation(), 100, 3.0, 0.1, 3.0, 0.5,
                Material.STONE.createBlockData());

        // Dźwięk eksplozji
        world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.8f);

        // Znajdź pobliskie żywe istoty w promieniu 5 bloków
        for (Entity entity : world.getNearbyEntities(player.getLocation(), 5, 5, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity target = (LivingEntity) entity;

                // Zadaj obrażenia
                target.damage(8.0, player);

                // Odepchnij od gracza
                Vector knockback = target.getLocation().toVector().subtract(player.getLocation().toVector());
                knockback.normalize().multiply(2.0).setY(0.8);
                target.setVelocity(knockback);

                // Dodaj efekt spowolnienia
//                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
            }
        }

        player.sendMessage("§6§lBOOM! Slam attack zadał obrażenia pobliskim wrogom!");
    }
}
