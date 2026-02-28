package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlyingFloor implements Listener {
    public static final HashMap<Location, Integer> locations = new HashMap<>();
    private static BukkitRunnable task;

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.FLYING_FLOOR)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if(timer.isRunning()){
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if (player.getGameMode() == GameMode.SPECTATOR) continue;
                        Location location = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
                        if (location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.LAVA) continue;
                        if (locations.containsKey(location)) continue;
                        locations.put(location, 10);
                    }
                    Iterator<Map.Entry<Location, Integer>> iterator = locations.entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry<Location, Integer> entry = iterator.next();
                        Location location = entry.getKey().clone().add(0.5, 0, 0.5);
                        Block block = location.getBlock();
                        int time = entry.getValue() - 1;
                        if(time <= 0){
                            var blockData = block.getBlockData();
                            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(location, blockData);
                            Vector velocity = new Vector(0, 1.2, 0);
                            fallingBlock.setVelocity(velocity);
                            block.setType(Material.AIR, true);
                            iterator.remove();
                            continue;
                        }
                        entry.setValue(time);
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1);
    }
}
