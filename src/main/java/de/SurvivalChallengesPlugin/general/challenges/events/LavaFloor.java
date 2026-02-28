package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LavaFloor implements Listener {
    public static final HashMap<Location, Integer> locations = new HashMap<>();
    private static BukkitRunnable task;
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.LAVA_FLOOR)) {
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to == null) return;
            if (from.getBlock() != to.getBlock()) {
                Location location = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
                if (!location.getBlock().getType().isSolid() || location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.LAVA)
                    return;
                if (locations.containsKey(location)) return;
                locations.put(location, 40);
            }
        }
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.LAVA_FLOOR)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if(timer.isRunning()){
                    Iterator<Map.Entry<Location, Integer>> iterator = locations.entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry<Location, Integer> entry = iterator.next();
                        Location location = entry.getKey();
                        int time = entry.getValue() - 1;
                        if(time == 20)
                            location.getBlock().setType(Material.MAGMA_BLOCK, true);
                        if(time <= 0){
                            location.getBlock().setType(Material.LAVA, true);
                            iterator.remove();
                            continue;
                        }
                        entry.setValue(time);
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 5);
    }
}
