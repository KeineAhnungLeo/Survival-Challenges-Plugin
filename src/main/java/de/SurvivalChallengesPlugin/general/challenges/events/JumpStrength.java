package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JumpStrength implements Listener {
    public static final Map<UUID, Double> playerJumpStrength = new HashMap<>();
    private static final double jumpVelocity = 0.40;
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.JUMP_STRENGTH)) {
            Player player = event.getPlayer();
            if(player.getGameMode() == GameMode.SPECTATOR) return;
            Location to = event.getTo();
            double velocityY = player.getVelocity().getY();
            if (to == null) return;
            if (velocityY > jumpVelocity && !player.isSwimming() && !player.isFlying() && !(to.getBlock().getType() == Material.LADDER))
                increasePlayerJump(player);
        }
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
