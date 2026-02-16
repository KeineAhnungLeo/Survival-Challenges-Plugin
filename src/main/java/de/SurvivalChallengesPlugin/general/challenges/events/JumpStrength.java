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
            increasePlayerJump(player);
        }
    }
    
    private boolean wasOnGround(Location from) {
        Location below = from.clone().subtract(0, 0.01, 0);
        return below.getBlock().getType().isSolid();
    }
    
    private void increasePlayerJump(Player jumper){
        UUID jumperUUID = jumper.getUniqueId();
        double jumperValue = playerJumpStrength.getOrDefault(jumperUUID, -0.1);
        jumper.setVelocity(jumper.getVelocity().setY(jumperValue));

        for(Player player : Bukkit.getOnlinePlayers()){
            UUID uuid = player.getUniqueId();
            if(uuid.equals(jumperUUID)) continue;
            double value = playerJumpStrength.getOrDefault(uuid, -0.1);
            value = value + 0.1;
            playerJumpStrength.put(uuid, value);
        }
    }
}
