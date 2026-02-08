package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;

public class MobSwap implements Listener {

    public static final Map<EntityType, EntityType> MOB_MAPPING = new HashMap<>();
    public static final Set<EntityType> USED_TARGETS = new HashSet<>();
    private static final Random random = new Random();

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.MOB_SWAP)) {
            EntityType original = event.getEntityType();

            if (!original.isAlive()) return;
            if (!original.isSpawnable()) return;
            if (original == EntityType.PLAYER) return;
            if (original == EntityType.ENDER_DRAGON) return;
            if (original == EntityType.GIANT) return;
            if (original == EntityType.ILLUSIONER) return;
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

            EntityType replacement = MOB_MAPPING.computeIfAbsent(
                    original,
                    this::getRandomEntity
            );

            if (replacement == null) return;

            event.setCancelled(true);
            if(event.getLocation().getWorld() == null) return;
            event.getLocation().getWorld().spawnEntity(event.getLocation(), replacement);
        }
    }

    private EntityType getRandomEntity(EntityType original) {

        List<EntityType> candidates = new ArrayList<>();

        for (EntityType type : EntityType.values()) {
            if (!type.isSpawnable()) continue;
            if (!type.isAlive()) continue;
            if (type == EntityType.PLAYER) continue;
            if (type == EntityType.ENDER_DRAGON) continue;
            if (type == original) continue;
            if (USED_TARGETS.contains(type)) continue;
            candidates.add(type);
        }
        if (candidates.isEmpty()) return null;
        EntityType chosen = candidates.get(random.nextInt(candidates.size()));
        USED_TARGETS.add(chosen);
        return chosen;
    }
}
