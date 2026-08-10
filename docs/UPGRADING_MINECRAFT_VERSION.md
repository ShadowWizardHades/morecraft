# Upgrading the Minecraft/Paper version

How to bump Morecraft to a newer Minecraft version, based on the 1.21.1 → 1.21.11 upgrade.

## 1. Find the target Paper API version

Paper API artifacts are published to `https://repo.papermc.io/repository/maven-public/`.
List available versions for the `paper-api` artifact:

```bash
curl -s "https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/maven-metadata.xml" \
  | grep -o "1.21[^<]*"
```

Pick a stable `X.Y.Z-R0.1-SNAPSHOT` entry (avoid `-pre`/`-rc` build candidates unless you
specifically need an unreleased fix).

## 2. Bump the dependency

In `pom.xml`, update the `paper-api` version:

```xml
<dependency>
    <groupId>io.papermc.paper</groupId>
    <artifactId>paper-api</artifactId>
    <version>1.21.11-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

`plugin.yml`'s `api-version` field only needs to match the `X.Y` line (`'1.21'`), not the
patch version — it doesn't need to change for a patch-level bump like this one.

## 3. Rebuild and read the compiler errors

```bash
mvn clean package
```

Paper occasionally renames or removes API symbols between versions (deprecation → removal),
and `mvn` will fail fast on it rather than breaking at runtime. Compare each error against the
Paper changelog / Javadoc for the new version. If unsure what a symbol was renamed to, check
the API's source jar directly rather than guessing:

```bash
find ~/.m2 -path "*paper-api/<new-version>*sources.jar"
unzip -p <sources-jar> org/bukkit/attribute/Attribute.java | grep MAX_HEALTH
```

**Example from this upgrade:** `Attribute.GENERIC_MAX_HEALTH`, `GENERIC_MOVEMENT_SPEED`,
`GENERIC_ATTACK_DAMAGE`, `GENERIC_JUMP_STRENGTH`, and `GENERIC_SAFE_FALL_DISTANCE` all lost
their `GENERIC_` prefix at some point after 1.21.1 (part of Bukkit's registry-based attribute
rework). This was a known, anticipated break — see `docs/CODE_REVIEW.md` §14. Fixed with a
straight rename in `events/CustomMobs.java` and `CustomRecipes.java`:

```bash
sed -i 's/Attribute\.GENERIC_/Attribute./g' \
  src/main/java/me/accountzero/morecraft/events/CustomMobs.java \
  src/main/java/me/accountzero/morecraft/CustomRecipes.java
```

Repeat build → fix → build until `mvn clean package` is clean.

## 4. Sanity-check other version references

Grep for the old version string to catch anything the compiler wouldn't flag (docs, comments):

```bash
grep -rn "1\.21\.1" --include="*.md" --include="*.xml" --include="*.yml" .
```

`README.md` and `CLAUDE.md` intentionally refer to the game version generically (`1.21.x` /
`1.21`), so they usually don't need edits for a patch bump.

## 5. Verify

There are no automated tests (per `CLAUDE.md`), so verification is:
1. `mvn clean package` succeeds with no errors/warnings.
2. Copy `target/morecraft-*.jar` into a Paper `<target-version>` server's `plugins/` folder and
   confirm it loads (`/plugins` shows it, no errors in console) and the core features still
   work in-game (a zombie spawns geared up, the Blood Medallion still doubles damage, etc).

## Notes for bigger jumps (minor version, e.g. 1.21 → 1.22)

A patch bump (1.21.1 → 1.21.11) is usually just API renames. A minor version bump can also
involve:
- Behavior changes to vanilla mechanics that your buff values were tuned against (re-check
  `config.yml` defaults still feel right).
- New/removed entity types, attributes, or materials.
- Attribute/stat value range changes (Mojang has changed max-health caps before).

Budget more manual testing time for those; the same "bump version → build → fix compiler
errors → grep for stale references → smoke test" loop still applies.
