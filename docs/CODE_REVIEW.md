# Morecraft — Code Review

A blunt, honest review of the plugin as it stands, as requested. The goal isn't to make you
feel bad — it's to point at the specific things that will bite you (or your friends) and the
stuff that'll make the plugin painful to keep building on. You're clearly not a full-time
Java dev, and for a hobby plugin this is a solid start. But you asked for criticism, so:

Findings are ordered by severity. File/line references point at the current code.

**Resolution status (as of 2026-08-09):** all items below are resolved except #4, #5
(intentional, not bugs), #6 (tracked in its own issue), and the ⚪ Polish section
(deferred, not yet scheduled).

---

## 🔴 Critical — these are real bugs

### 1. The zombie tier randomization has gaps — rare tier leaks
**Status:** ✅ Fixed — cumulative thresholds with one comparison style.
`events/CustomMobs.java:26-54`

```java
int zombieRand = random.nextInt(200);          // produces 0..199
if (zombieRand < 110) { ... }                  // 0–109
else if (zombieRand > 110 && zombieRand < 180) // 111–179  ← 110 is skipped
else if (zombieRand > 180 && zombieRand < 199) // 181–198  ← 180 is skipped
else { ... }                                   // 110, 180, 199  ← lands here
```

Because you use strict `>` and `<`, the values **110, 180, and 199** match *none* of the
first three branches and fall through to the `else` — which is the **full-diamond zombie**.
So your "ultra rare" tier silently fires on three specific rolls it was never meant to. It's
not catastrophic, but it's an accidental logic bug, not a designed probability.

**Fix:** use contiguous cumulative thresholds and one comparison style:
```java
if (r < 110)       // leather
else if (r < 180)  // gold
else if (r < 199)  // iron
else               // diamond
```

### 2. `getAttribute(...)` is dereferenced without a null check — NPE risk
**Status:** ✅ Fixed — `setAttr(...)` null-safe helper.
`events/CustomMobs.java` (every mob branch)

`entity.getAttribute(Attribute.X)` returns `null` if that entity type doesn't have attribute
`X`. You immediately call `.setBaseValue(...)` on the result. The day that returns null,
you throw a `NullPointerException` *inside a spawn event* — which can abort the spawn and
spam the console on every mob. It happens to work for the exact mob/attribute pairs you use
today, but it's fragile. Wrap it in a small null-safe helper:

```java
private void setAttr(LivingEntity e, Attribute a, double v) {
    AttributeInstance inst = e.getAttribute(a);
    if (inst != null) inst.setBaseValue(v);
}
```

### 3. Buffed mobs drop their gear → free loot farm
**Status:** ✅ Fixed — `zeroGearDropChances(...)`.
`events/CustomMobs.java` (all the `setHelmet`/`setChestplate`/`setItemInMainHand` calls)

Mobs have a default chance (~8.5%, increased by Looting) to drop equipped armor and weapons
on death. So your rare diamond/iron/gold zombies become **free diamond armor and sword
dispensers** — players will hunt them specifically. That's the exact opposite of "make the
game harder." Zero out the drop chances for any gear you give them:

```java
zombie.getEquipment().setHelmetDropChance(0f);
zombie.getEquipment().setItemInMainHandDropChance(0f); // etc.
```

---

## 🟠 Logic & gameplay problems

### 4. Spiders got *weaker*, not stronger
**Status:** 🚫 Won't fix — confirmed intentional, not a typo.
`events/CustomMobs.java:67`

```java
spider.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
```

A vanilla spider does ~2 damage. You set it to **0.5** — a nerf. Given everything else in
this file makes mobs harder, this is almost certainly a typo (extra speed/jump + *less*
damage is a weird combo). Bump it up.

### 5. The witch potion logic helps the player as often as it hurts them
**Status:** 🚫 Won't fix — confirmed intentional (mixed beneficial/harmful is by design).
`events/CustomMobs.java:127-146`

Two problems here:
- You replace **every** potion a witch throws — including the witch's own defensive/healing
  potions it lobs at itself.
- Your allowed list contains **beneficial** effects: `SPEED`, `REGENERATION`,
  `INSTANT_HEALTH`, `HASTE`. When a witch throws one of those at a player, you just *buffed
  the player*. Meanwhile `INSTANT_DAMAGE` at amplifier 4 (level 5) can nearly one-shot
  someone.

It's a coin flip between "trivial" and "instant death." Use a harmful-only list and pick a
sane amplifier, ideally from config so you can tune it without recompiling.

### 6. The snow golem comment promises things the code doesn't do
**Status:** ⏸ Deferred — will be handled in its own issue (sun-immunity/sentry feature work).
`events/CustomMobs.java:84-88`

```java
// They will not die when exposed to sun + maybe repurpose as sentry gun ?
Snowman snowGolem = (Snowman) event.getEntity();
snowGolem.getAttribute(...).setBaseValue(40);
```

The comment describes sun-immunity and a sentry concept, but the code only sets 40 HP.
Anyone reading this (including future-you) will think the feature exists. Either implement
it (cancel the melt/weather damage in an `EntityDamageEvent` handler) or delete the comment.

### 7. The Blood Medallion's "double the pain" is never implemented
**Status:** ✅ Fixed — `CustomMobs.onBloodMedalionDamage` doubles incoming damage while the
medallion is held, matching the doubled outgoing damage. (The "plain `REDSTONE` is easy to
lose in an inventory" cosmetic note is still open — folded into ⚪ Polish.)
`CustomRecipes.java:22-29`

The lore says *"Double the fun but double the pain,"* but mechanically it only gives the
upside: +100% attack damage (`MULTIPLY_SCALAR_1`, value `1`). There's no downside at all.
If the drawback is part of the design, add it (e.g. a max-health reduction modifier on the
same item, so it's a real glass-cannon trade). Also: the base item is `REDSTONE` — it
stacks and is visually identical to plain redstone, so it's easy to lose in an inventory.
Consider a distinct base item and/or `CustomModelData`.

### 8. Setting health inside `EntitySpawnEvent` is timing-fragile
**Status:** ✅ Fixed — moved to `CreatureSpawnEvent`.
`events/CustomMobs.java:23`

Some entities have their health normalized during the spawn process, so setting it in
`EntitySpawnEvent` is occasionally unreliable. Prefer `CreatureSpawnEvent` (fires for actual
creature spawns) or a 1-tick `runTaskLater`. Bonus: `CreatureSpawnEvent#getSpawnReason()`
lets you decide whether spawner/spawn-egg/breeding mobs should also get buffed.

---

## 🟡 Architecture & maintainability

### 9. One giant method with nine `if`s and no early exit
**Status:** ✅ Fixed — `switch` dispatch to per-mob handler methods.
`events/CustomMobs.java:22-102`

`onEntitySpawn` is a flat wall of `if (event.getEntityType() == ...)`. Every single spawn in
the world runs through all nine checks. It works, but it doesn't scale and it's hard to
read. A `switch (event.getEntityType())` with `return`, or a per-mob handler method each,
would be far cleaner and faster to extend.

### 10. Everything is hardcoded — there's no `config.yml` (this is the big one for you)
**Status:** ✅ Fixed — `config.yml` + `saveDefaultConfig()`/`getConfig()`.
Health, speed, the tier probabilities, drop rates, potion strength — all magic numbers baked
into the code. That means **every time you and your friends want to tweak the balance, you
have to recompile and redeploy the jar.** For a "play with friends and iterate" plugin,
that's the single biggest quality-of-life problem. Move the numbers into `config.yml`
(`saveDefaultConfig()` + `getConfig()`), and you can rebalance live.

### 11. Dead code
**Status:** ✅ Fixed — `CustomItems.java`, `enums/CreeperTypeEnum.java`, and the commented-out
health-bar code are all deleted.
- `CustomItems.register()` (`CustomItems.java`) is an empty stub **and is never even called**
  from `onEnable()`.
- `enums/CreeperTypeEnum.java` is defined but used nowhere.
- The commented-out health-bar code (`CustomMobs.java:99-101, 114-124, 148-159`) is just
  sitting there.

Delete it or wire it up. Commented-out code rots and confuses.

### 12. `Morecraft.getInstance()` does a lookup every call
**Status:** ✅ Fixed — `CustomRecipes.register(Plugin)` takes the instance as a parameter.
`CustomRecipes.java` calls it 4 times. `getPlugin(...)` isn't free. Pass the plugin instance
into `register(plugin)` instead of fetching the singleton repeatedly.

---

## 🔵 API usage & build

### 13. Deprecated text APIs
**Status:** ✅ Fixed — switched to the Adventure `Component` API.
`CustomRecipes.java:24-27` — `setLore(...)`, `setDisplayName(...)`, and the `§6§l` legacy
color codes are deprecated in Paper. The modern path is the Adventure `Component` API:
`meta.displayName(Component.text(...))`, `meta.lore(List.of(Component.text(...)))`.

### 14. `Attribute.GENERIC_*` names will break if you upgrade Paper
They're correct on your pinned `paper-api 1.21.1`, but these enums were renamed shortly after
(`MAX_HEALTH`, `MOVEMENT_SPEED`, …). Don't change them now — just know that bumping the Paper
version is not a free lunch.

### 15. The spawn handler ignores cancellation
**Status:** ✅ Fixed — `onCreatureSpawn` now has `@EventHandler(ignoreCancelled = true)`.

Use `@EventHandler(ignoreCancelled = true)` so you don't buff a mob whose spawn another
plugin / region protection is cancelling.

### 16. `maven-shade-plugin` currently does nothing
**Status:** ⏸ No action taken — not a bug, nothing to fix without adding a fake dependency.
`paper-api` is still the only dependency and it's `provided`, so there's genuinely nothing to
bundle yet. Leave as-is; this resolves itself naturally the day a real compile-scope
dependency gets added.

`pom.xml:31-43` — `paper-api` is `provided`, so there are no dependencies to bundle. The
shaded jar equals the normal jar. Harmless, but it's dead config until you add a real
dependency to shade.

### 17. Recipes can throw on `/reload`
**Status:** ✅ Fixed — `CustomRecipes.register(...)` now calls `Bukkit.removeRecipe(key)`
before each `Bukkit.addRecipe(...)`, so a second `onEnable` no longer throws on a duplicate
key.

`CustomRecipes.java` registers recipes with fixed `NamespacedKey`s but never removes them.
A second `onEnable` (e.g. `/reload`) can fail on a duplicate key. Remove them in `onDisable`
with `Bukkit.removeRecipe(key)`, or guard registration.

---

## ⚪ Polish

**Status:** ⏸ Deferred — ignored for now, not scheduled.

- **"Medalion" is misspelled** — should be "Medallion." It's not just cosmetic: it's baked
  into the persistent `NamespacedKey` (`blood_medalion`), so fixing it later is a migration.
- **`README.md` is all TODOs** and **`plugin.yml`** has no `description`/`author`.
- Mixed construction styles: `ItemStack.of(...)` in one place, `new ItemStack(...)` in
  another. Pick one.
- No license / tests / CI — low priority for a hobby plugin, just noting it.
- Blood Medallion base item is plain `REDSTONE` — stacks and looks identical to vanilla
  redstone, easy to lose in an inventory (from #7). Consider a distinct base item and/or
  `CustomModelData`.

---

## If you only do five things, do these
**Status:** Done, with two calls kept as-is on purpose — the "spider damage typo" (#4) and
"rebalance the witch potions" (#5) turned out to be intentional design, not bugs.
1. ~~Fix the zombie range bug (#1) and the spider damage typo (#4).~~ #1 fixed; #4 is
   intentional.
2. ~~Zero out gear drop chances on buffed mobs (#3).~~ Fixed.
3. ~~Add null-safety to attribute access (#2).~~ Fixed.
4. ~~Move the tunable numbers into `config.yml` (#10) — biggest day-to-day win.~~ Fixed.
5. ~~Rebalance the witch potions to harmful-only (#5), then clean up the dead code (#11) and
   the misspelling.~~ #5 is intentional; #11 (dead code) fixed; the misspelling is deferred
   to ⚪ Polish.
