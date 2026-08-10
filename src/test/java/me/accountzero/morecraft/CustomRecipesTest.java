package me.accountzero.morecraft;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomRecipesTest extends MorecraftTest {

    @Test
    void registersZombieFleshToLeatherFurnaceRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "zombie_flesh_to_leather");
        Recipe recipe = server.getRecipe(key);

        FurnaceRecipe furnaceRecipe = assertInstanceOf(FurnaceRecipe.class, recipe);
        assertEquals(Material.LEATHER, furnaceRecipe.getResult().getType());
        assertEquals(Material.ROTTEN_FLESH, furnaceRecipe.getInputChoice().getItemStack().getType());
        assertEquals(10, furnaceRecipe.getExperience());
        assertEquals(20, furnaceRecipe.getCookingTime());
    }

    @Test
    void registersBloodMedalionShapedRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "blood_medalion");
        Recipe recipe = server.getRecipe(key);

        ShapedRecipe shapedRecipe = assertInstanceOf(ShapedRecipe.class, recipe);
        assertEquals(3, shapedRecipe.getShape().length);
        assertEquals("RRR", shapedRecipe.getShape()[0]);
        assertEquals("RDR", shapedRecipe.getShape()[1]);
        assertEquals("RRR", shapedRecipe.getShape()[2]);

        Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
        assertEquals(Material.REDSTONE_BLOCK, choiceMap.get('R').getItemStack().getType());
        assertEquals(Material.DIAMOND_BLOCK, choiceMap.get('D').getItemStack().getType());
    }

    @Test
    void bloodMedalionItemHasNameLoreAndAttackDamageBoost() {
        NamespacedKey key = new NamespacedKey(plugin, "blood_medalion");
        ShapedRecipe recipe = (ShapedRecipe) server.getRecipe(key);
        ItemMeta meta = recipe.getResult().getItemMeta();

        assertNotNull(meta.displayName());
        assertEquals(1, meta.lore().size());

        Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE);
        assertNotNull(modifiers);
        assertEquals(1, modifiers.size());
        AttributeModifier modifier = modifiers.iterator().next();
        assertEquals(1, modifier.getAmount());
        assertEquals(AttributeModifier.Operation.MULTIPLY_SCALAR_1, modifier.getOperation());
    }

    @Test
    void bloodMedalionItemIsTaggedForCustomMobsToRecognize() {
        NamespacedKey key = new NamespacedKey(plugin, "blood_medalion");
        ShapedRecipe recipe = (ShapedRecipe) server.getRecipe(key);
        ItemMeta meta = recipe.getResult().getItemMeta();

        NamespacedKey tagKey = new NamespacedKey(plugin, CustomRecipes.BLOOD_MEDALION_TAG);
        assertTrue(meta.getPersistentDataContainer().has(tagKey, PersistentDataType.BYTE));
        assertEquals((byte) 1, meta.getPersistentDataContainer().get(tagKey, PersistentDataType.BYTE));
    }

    @Test
    void reRegisteringDoesNotThrowOnDuplicateKeys() {
        // Simulates /reload calling CustomRecipes.register(...) again on an already-running server.
        assertDoesNotThrow(() -> CustomRecipes.register(plugin));

        NamespacedKey key = new NamespacedKey(plugin, "blood_medalion");
        assertNotNull(server.getRecipe(key));
    }
}
