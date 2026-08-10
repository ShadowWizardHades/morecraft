# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Karpathy-rules

1. Think Before Coding
   Don't assume. Don't hide confusion. Surface tradeoffs.

2. Simplicity First
   Minimum code that solves the problem. Nothing speculative.

3. Surgical Changes
   Touch only what you must. Clean up only your own mess.

4. Goal-Driven Execution
   Define success criteria. Loop until verified.

## Project

Morecraft is a Paper (Minecraft 1.21) server plugin written in Java 21 that increases game difficulty and adds new content. Built with Maven.

## Commands

```bash
mvn clean package   # Build — outputs shaded JAR to target/
mvn compile         # Compile only
```

There are no tests. To deploy, copy the JAR from `target/` into a Paper server's `plugins/` folder.

## Architecture

**Entry point**: `Morecraft.java` — extends `JavaPlugin`. `onEnable()` calls `saveDefaultConfig()`, registers `CustomMobs` as a listener, and calls `CustomRecipes.register(this)`. Provides a `getInstance()` singleton (`getPlugin(Morecraft.class)`).

**Two active systems**:

- `events/CustomMobs.java` — Bukkit event listener that dispatches on `CreatureSpawnEvent` (`ignoreCancelled = true`) via a `switch` on entity type to a per-mob `buffX(...)` method: zombie, skeleton, spider, rabbit, iron golem, snow golem, phantom, chicken. Zombies get a weighted-random gear tier (leather/gold/iron/diamond) tagged via `PersistentDataContainer` and grant bonus XP on death (`onZombieDeath`) matching tier. Also handles `EntityShootBowEvent` to multiply skeleton arrow velocity, `ProjectileLaunchEvent` to replace witch-thrown potions with a random level-5 effect, and `EntityDamageEvent` to double incoming damage on players holding the Blood Medallion (matching its doubled outgoing damage).

- `CustomRecipes.java` — `register(Plugin)` (takes the plugin instance, not a `getInstance()` lookup) registers a furnace recipe (rotten flesh → leather) and a shaped recipe (Blood Medallion: 8 redstone blocks + 1 diamond block center) that gives +100% attack damage via an `AttributeModifier` and tags the item's `PersistentDataContainer` with `BLOOD_MEDALION_TAG` so `CustomMobs` can recognize it. Both recipes call `Bukkit.removeRecipe(key)` before `addRecipe(...)` to avoid duplicate-key crashes on `/reload`.

**Tuning**: `src/main/resources/config.yml` holds all balance numbers (mob health/speed/damage, zombie tier weights, witch potion duration/amplifier) with `FileConfiguration.getX(path, default)` fallbacks in code, so values can be rebalanced via `/reload` without recompiling.

**Known intentional non-obvious behavior** (see `docs/CODE_REVIEW.md` for full history): spider attack damage is deliberately lowered (not a typo), and witches' replacement potions intentionally mix harmful and beneficial effects. Don't "fix" these without confirming with the user first.

## Paper API patterns

- Mob stats are set directly on the entity after spawn via the null-safe `setAttr(entity, attribute, value)` helper in `CustomMobs`, which calls `entity.getAttribute(attribute).setBaseValue(...)` only if the attribute instance is non-null (not all entity types have all attributes), plus `entity.setHealth(...)` and `entity.getEquipment().set*(...)`.
- When equipping mobs with custom gear, zero out drop chances (`equipment.set*DropChance(0f)`) so buffed mobs don't become a free loot farm.
- Recipes are registered on `Bukkit`/`Server`: `Bukkit.removeRecipe(key)` then `Bukkit.addRecipe(...)`.
- Item text uses the Adventure `Component` API (`meta.displayName(Component.text(...))`, `meta.lore(List.of(...))`) — not the deprecated `setDisplayName`/`setLore` string APIs.
- All listeners must be registered via `getServer().getPluginManager().registerEvents(listener, plugin)` in `onEnable()`.
