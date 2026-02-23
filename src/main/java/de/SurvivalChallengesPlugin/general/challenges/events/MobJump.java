package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.Random;

public class MobJump implements Listener {

    private static final Random random = new Random();
    private static final double jumpVelocity = 0.40;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.MOB_JUMP)) {
            Player player = event.getPlayer();
            if(player.getGameMode() == GameMode.SPECTATOR) return;
            Location to = event.getTo();
            double velocityY = player.getVelocity().getY();
            if (to == null) return;
            if (velocityY > jumpVelocity && !player.isSwimming() && !player.isFlying() && !(to.getBlock().getType() == Material.LADDER))
                spawnRandomMob(player);
        }
    }

    public static void spawnRandomMob(Player player) {
        EntityType[] types = EntityType.values();
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        Difficulty difficulty = player.getWorld().getDifficulty();
        for (int i = 0; i < 30; i++) {
            EntityType type = types[random.nextInt(types.length)];
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
            player.getWorld().spawnEntity(player.getLocation(), type);
            return;
        }
    }
}
