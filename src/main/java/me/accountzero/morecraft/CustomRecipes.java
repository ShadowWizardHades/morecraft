package me.accountzero.morecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class CustomRecipes {

    // Tags the Blood Medallion so CustomMobs can recognize it in onBloodMedalionDamage
    // and double incoming damage to match the doubled outgoing damage.
    public static final String BLOOD_MEDALION_TAG = "blood_medalion_item";

    public static void register(Plugin plugin) {
        // Furnace Recipes
        // ZombieFlesh to Leather
        NamespacedKey zombieFleshToLeatherKey = new NamespacedKey(plugin, "zombie_flesh_to_leather");
        Bukkit.removeRecipe(zombieFleshToLeatherKey); // guards against a duplicate-key crash on /reload
        FurnaceRecipe zombieFleshToLeather = new FurnaceRecipe(zombieFleshToLeatherKey, ItemStack.of(Material.LEATHER), new RecipeChoice.ExactChoice(new ItemStack(Material.ROTTEN_FLESH)), 10, 20);
        Bukkit.addRecipe(zombieFleshToLeather);

        // Blood Medalion
        ItemStack bloodMedalion = new ItemStack(Material.REDSTONE);
        ItemMeta bloodMedalionMeta = bloodMedalion.getItemMeta();
        bloodMedalionMeta.lore(List.of(
                Component.text("Double the fun but double the pain")
        ));
        bloodMedalionMeta.displayName(Component.text("Blood Medalion")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        bloodMedalionMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(new NamespacedKey(plugin, "blood_medalion_attack_boost"), 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        bloodMedalionMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, BLOOD_MEDALION_TAG), PersistentDataType.BYTE, (byte) 1);
        bloodMedalion.setItemMeta(bloodMedalionMeta);

        NamespacedKey bloodMedalionRecipeKey = new NamespacedKey(plugin, "blood_medalion");
        Bukkit.removeRecipe(bloodMedalionRecipeKey); // guards against a duplicate-key crash on /reload
        ShapedRecipe bloodMedalionRecipe = new ShapedRecipe(bloodMedalionRecipeKey, bloodMedalion);
        bloodMedalionRecipe.shape("RRR", "RDR", "RRR");
        bloodMedalionRecipe.setIngredient('R', Material.REDSTONE_BLOCK);
        bloodMedalionRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        Bukkit.addRecipe(bloodMedalionRecipe);
    }
}
