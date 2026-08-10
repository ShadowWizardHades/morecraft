# Morecraft: Minecraft but more
The simple idea behind morecraft is to enhance minecraft in a way, so there is more things to do and harder to play, so the fun of minecraft does not die within 2 days or weeks

## Features
- **Tougher zombies** — zombies spawn with a weighted-random gear tier (leather, gold, iron, diamond), each with matching armor/weapon, health, and speed boosts. Diamond zombies are rare. Killing gold/iron/diamond-tier zombies grants bonus XP.
- **Tougher skeletons** — boosted health and speed, and their arrows fly 3x faster.
- **Faster, jumpier spiders** — boosted speed and jump strength.
- **Buffed rabbits, iron golems, snow golems, phantoms, and chickens** — health, speed, and jump tweaks across the board.
- **Randomized witch potions** — witches throw a random level-5 potion effect (a mix of harmful and beneficial) instead of their usual poison splash.
- **Rotten flesh → leather** furnace recipe.
- **Blood Medallion** — a craftable item (8 redstone blocks + 1 diamond block) that doubles your attack damage, but also doubles incoming damage while you hold it. A glass-cannon trade-off.

All of the above is tunable — mob stats, zombie tier weights, and potion settings live in `config.yml` and can be rebalanced with `/reload`, no recompiling needed.

## Requirements
- A [Paper](https://papermc.io/) 1.21.x server
- Java 21

## Build & Install
```bash
mvn clean package
```
Copy the resulting jar from `target/` into your server's `plugins/` folder and restart (or `/reload`).

## Configuration
See `src/main/resources/config.yml` for all tunable values (mob health/speed/damage, zombie tier weights, witch potion duration and strength). Edit the copy in your server's `plugins/morecraft/config.yml` and run `/reload` to apply changes without restarting.

## Progress
Not sure how to measure progress or give updates on it yet, I started working on it again just out of fun, so will have to think about it
