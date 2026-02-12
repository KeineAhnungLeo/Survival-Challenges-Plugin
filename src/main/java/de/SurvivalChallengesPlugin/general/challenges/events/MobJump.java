package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.Random;

public class MobJump implements Listener {

    private static final Random random = new Random();

    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.MOB_JUMP)) {
            Player player = event.getPlayer();
            if(player.getGameMode() == GameMode.SPECTATOR) return;
            Location from = event.getFrom();
            Location to = event.getTo();
            if(to == null) return;
            double deltaY = to.getY() - from.getY();
            if (deltaY < 0.4) return;
            if (!wasOnGround(from)) return;
            spawnRandomMob(player);
        }
    }
    private boolean wasOnGround(Location from) {
        Location below = from.clone().subtract(0, 0.01, 0);
        return below.getBlock().getType().isSolid();
    }

    public static void spawnRandomMob(Player player) {

        EntityType[] types = EntityType.values();
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        for (int i = 0; i < 30; i++) {
            EntityType type = types[random.nextInt(types.length)];
            if (!type.isSpawnable()) continue;
            if (!type.isAlive()) continue;
            if (type == EntityType.PLAYER) continue;
            if (type == EntityType.ENDER_DRAGON && settings.getSettingBossRequired() == 1) continue;
            if (type == EntityType.WITHER && settings.getSettingBossRequired() == 2) continue;
            if (type == EntityType.ELDER_GUARDIAN && settings.getSettingBossRequired() == 3) continue;
            player.getWorld().spawnEntity(player.getLocation(), type);
            return;
        }
    }
}
