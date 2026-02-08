package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnlyOneBlockUse implements Listener {
    Map<UUID, Map<Material, Boolean>> map = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.ONLY_ONE_BLOCK_USE)) {
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to == null) return;

            if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
                player.sendMessage("moved");
                Location belowTo = to.clone().subtract(0, 0.1, 0);
                Material newMaterial = belowTo.getBlock().getType();

                Location belowFrom = from.clone().subtract(0, 0.1, 0);
                Material oldMaterial = belowFrom.getBlock().getType();
                if (newMaterial == Material.WATER || newMaterial == Material.LAVA || newMaterial == Material.AIR || newMaterial.name().endsWith("_PRESSURE_PLATE")) return;
                if(newMaterial == oldMaterial) return;

                Map<Material, Boolean> playerMap = map.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

                    playerMap.put(oldMaterial, true);

                    if (playerMap.getOrDefault(newMaterial, false)) {
                        player.sendMessage("tot");
                    } else {
                        playerMap.putIfAbsent(newMaterial, false);
                        player.sendMessage("+ " + newMaterial);
                    }
            }
        }
    }
}