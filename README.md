# McCombo

CubeCraft-style PvP knockback plugin for Paper.

## Features
- Custom PvP knockback override for melee and projectile damage
- Configurable horizontal/vertical knockback, airborne multipliers, sprint scaling, vertical cap, and hit cooldown
- Admin commands for reload, tuning, and profile save/load

## Build
1. Install Java 21
2. Run `gradle build`
3. Drop `build/libs/McCombo-1.0.0.jar` into your Paper plugin folder

## Admin Commands
- `/kb reload`
- `/kb set <setting> <value>`
- `/kb get`
- `/kb profile save <name>`
- `/kb profile load <name>`
