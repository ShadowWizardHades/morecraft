package me.accountzero.morecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CustomRecipes {

    public static void register() {
        // Furnace Recipes
        // ZombieFlesh to Leather
        FurnaceRecipe zombieFleshToLeather = new FurnaceRecipe(new NamespacedKey(Morecraft.getInstance(), "zombie_flesh_to_leather"), ItemStack.of(Material.LEATHER), new RecipeChoice.ExactChoice(new ItemStack(Material.ROTTEN_FLESH)), 10, 20);
        Bukkit.addRecipe(zombieFleshToLeather);

        // Blood Medalion
        ItemStack bloodMedalion = new ItemStack(Material.REDSTONE);
        ItemMeta bloodMedalionMeta = bloodMedalion.getItemMeta();
        bloodMedalionMeta.setLore(Arrays.asList(
                "Double the fun but double the pain"
        ));
        bloodMedalionMeta.setDisplayName("§6§lBlood Medalion");
        bloodMedalionMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(new NamespacedKey(Morecraft.getInstance(), "blood_medalion_attack_boost"), 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        bloodMedalion.setItemMeta(bloodMedalionMeta);

        ShapedRecipe bloodMedalionRecipe = new ShapedRecipe(new NamespacedKey(Morecraft.getInstance(), "blood_medalion"), bloodMedalion);
        bloodMedalionRecipe.shape("RRR", "RDR", "RRR");
        bloodMedalionRecipe.setIngredient('R', Material.REDSTONE_BLOCK);
        bloodMedalionRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        Bukkit.addRecipe(bloodMedalionRecipe);
    }
}
