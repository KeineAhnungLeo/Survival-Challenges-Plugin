package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import de.SurvivalChallengesPlugin.general.settings.events.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChunkRandomMob implements Listener {
    public static final Set<String> doneChunks = new HashSet<>();
    private static final Random random = new Random();
    private static BukkitRunnable task;
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_MOB)) {
            if (event.getTo() == null) return;
            Player player = event.getPlayer();
            if(player.getGameMode() == GameMode.SPECTATOR) return;
            Chunk oldChunk = event.getFrom().getChunk();
            Chunk newChunk = event.getTo().getChunk();
            if (oldChunk.equals(newChunk)) return;
            for (LivingEntity entity : player.getWorld().getLivingEntities()) {
                if (entity.hasMetadata("target")) return;
            }
            String chunkId = newChunk.getX() + ";" + newChunk.getZ();
            if (doneChunks.contains(chunkId)) return;
            doneChunks.add(chunkId);
            int centerX = newChunk.getX() * 16 + 8;
            int centerZ = newChunk.getZ() * 16 + 8;
            int centerY = player.getWorld().getHighestBlockYAt(newChunk.getX() * 16 + 7, newChunk.getZ() * 16 + 7);
            player.getWorld().getWorldBorder().setCenter(centerX, centerZ);
            player.getWorld().getWorldBorder().setSize(16);
            player.getWorld().getWorldBorder().setDamageBuffer(0.1);
            player.getWorld().getWorldBorder().setDamageAmount(2);
            player.getWorld().getWorldBorder().setWarningDistance(1);
            player.getWorld().getWorldBorder().setWarningTime(2);
            Location location = new Location(player.getWorld(), centerX + 0.5, centerY + 3, centerZ + 0.5);
            if(player.getWorld().getEnvironment() == World.Environment.NETHER){
                boolean foundPos = false;
                for (int j = player.getWorld().getMinHeight(); j < 127; j++) {
                    Block block = player.getWorld().getBlockAt(centerX, j, centerZ);
                    if(!block.getType().isSolid() || block.getType() == Material.LAVA) continue;
                    if (block.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                        location = new Location(player.getWorld(), centerX + 0.5, j + 1, centerZ + 0.5);
                        foundPos = true;
                    }
                }
                if(!foundPos){
                    location = new Location(player.getWorld(), centerX + 0.5, 63, centerZ + 0.5);
                }
            }
            Entity newMob = player.getWorld().spawnEntity(location, getRandomEntity(player));
            newMob.setMetadata("target", new FixedMetadataValue(SurvivalChallengesPlugin.getInstance(), true));
            newMob.setGlowing(true);
        }
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_MOB)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if(!timer.isRunning()) return;
                boolean target = false;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    for(LivingEntity entity : player.getWorld().getLivingEntities()){
                        if(entity.hasMetadata("target")) target = true;
                    }
                    if(target) continue;
                    World world = player.getWorld();
                    world.getWorldBorder().setCenter(0, 0);
                    world.getWorldBorder().setSize(999999999);
                    world.getWorldBorder().setDamageBuffer(5);
                    world.getWorldBorder().setDamageAmount(1);
                    world.getWorldBorder().setWarningDistance(5);
                    world.getWorldBorder().setWarningTime(5);
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20);
    }

    @EventHandler
    public void onEntityPortalEnter(EntityPortalEvent event) {
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        if(challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_MOB)) {
            if(event.getEntity().hasMetadata("target"))
                event.setCancelled(true);
        }
    }



    private EntityType getRandomEntity(Player player) {
        List<EntityType> candidates = new ArrayList<>();
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        Difficulty difficulty = player.getWorld().getDifficulty();
        for (EntityType type : EntityType.values()) {
            if (!type.isSpawnable()) continue;
            if (!type.isAlive()) continue;
            if (type == EntityType.PLAYER) continue;
            Class<?> entityClass = type.getEntityClass();
            if (entityClass == null) continue;
            if (difficulty == Difficulty.PEACEFUL)
                if (Monster.class.isAssignableFrom(entityClass)) continue;
            if (type == EntityType.WITHER && settings.getSettingBossRequired() == 2) continue;
            if (type == EntityType.ELDER_GUARDIAN && settings.getSettingBossRequired() == 3) continue;
            if (type == EntityType.ENDER_DRAGON) continue;
            candidates.add(type);
        }
        return candidates.get(random.nextInt(candidates.size()));
    }
    public static void reset(){
        doneChunks.clear();
        if(task != null) {
            task.cancel();
            task = null;
        }
        for(World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity.hasMetadata("target"))
                    entity.remove();
                entity.getWorld().getWorldBorder().setCenter(0, 0);
                entity.getWorld().getWorldBorder().setSize(999999999);
                entity.getWorld().getWorldBorder().setDamageBuffer(5);
                entity.getWorld().getWorldBorder().setDamageAmount(1);
                entity.getWorld().getWorldBorder().setWarningDistance(5);
                entity.getWorld().getWorldBorder().setWarningTime(5);
            }
        }
    }
}
