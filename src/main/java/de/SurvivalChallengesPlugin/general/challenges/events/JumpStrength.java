package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JumpStrength implements Listener {
    public static final Map<UUID, Double> playerJumpStrength = new HashMap<>();
    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.JUMP_STRENGTH)) {
            Player player = event.getPlayer();
            if(player.getGameMode() == GameMode.SPECTATOR) return;
            Location from = event.getFrom();
            Location to = event.getTo();
            if(to == null) return;
            double deltaY = to.getY() - from.getY();
            if (deltaY < 0.4) return;
            if (!wasOnGround(from)) return;
            increasePlayerJump(player.getUniqueId());
        }
    }
    
    private boolean wasOnGround(Location from) {
        Location below = from.clone().subtract(0, 0.01, 0);
        return below.getBlock().getType().isSolid();
    }
    
    private void increasePlayerJump(UUID uuid){
        if(!playerJumpStrength.containsKey(uuid)){
            double value = -0.1;
            playerJumpStrength.put(uuid, value);
        }
        Double value = playerJumpStrength.get(uuid);
        value = value + 0.1;
        Player target = Bukkit.getPlayer(uuid);
        if(target == null) return;
        target.setVelocity(target.getVelocity().setY(value));

        for (Map.Entry<UUID, Double> entry : playerJumpStrength.entrySet()) {
            UUID key = entry.getKey();
            Double value1 = entry.getValue();
            if(key == uuid) continue;
            value1 = value1 + 0.1;
            playerJumpStrength.put(key, value1);
        }
    }
}
