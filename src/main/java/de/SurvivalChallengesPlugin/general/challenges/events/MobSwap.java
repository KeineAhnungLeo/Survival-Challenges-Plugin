package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;

public class MobSwap implements Listener {

    public static final Map<EntityType, EntityType> mobMapping = new HashMap<>();
    public static final Set<EntityType> usedTasks = new HashSet<>();
    private static final Random random = new Random();

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.MOB_SWAP)) {
            EntityType original = event.getEntityType();
            de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
            if (!original.isAlive()) return;
            if (!original.isSpawnable()) return;
            if (original == EntityType.PLAYER) return;
            if (original == EntityType.ENDER_DRAGON && settings.getSettingBossRequired() == 1) return;
            if (original == EntityType.WITHER && settings.getSettingBossRequired() == 2) return;
            if (original == EntityType.ELDER_GUARDIAN && settings.getSettingBossRequired() == 3) return;
            if (original == EntityType.GIANT) return;
            if (original == EntityType.ILLUSIONER) return;
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
            EntityType replacement = mobMapping.computeIfAbsent(
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
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        Difficulty difficulty = null;
        for (Player player : Bukkit.getOnlinePlayers()){
            difficulty = player.getWorld().getDifficulty();
            break;
        }
        for (EntityType type : EntityType.values()) {
            if (!type.isSpawnable()) continue;
            if (!type.isAlive()) continue;
            if (type == EntityType.PLAYER) continue;
            Class<?> entityClass = type.getEntityClass();
            if (entityClass == null) continue;
            if (difficulty == Difficulty.PEACEFUL)
                if (Monster.class.isAssignableFrom(entityClass)) continue;
            if (type == EntityType.ENDER_DRAGON && settings.getSettingBossRequired() == 1) continue;
            if (type == EntityType.WITHER && settings.getSettingBossRequired() == 2) continue;
            if (type == EntityType.ELDER_GUARDIAN && settings.getSettingBossRequired() == 3) continue;
            if (type == EntityType.SQUID) continue;
            if (type == EntityType.GLOW_SQUID) continue;
            if (type == EntityType.SALMON) continue;
            if (type == EntityType.TROPICAL_FISH) continue;
            if (type == EntityType.COD) continue;
            if (type == original) continue;
            if (usedTasks.contains(type)) continue;
            candidates.add(type);
        }
        if (candidates.isEmpty()) return null;
        EntityType chosen = candidates.get(random.nextInt(candidates.size()));
        if(difficulty != Difficulty.PEACEFUL)
            usedTasks.add(chosen);
        return chosen;
    }
}
