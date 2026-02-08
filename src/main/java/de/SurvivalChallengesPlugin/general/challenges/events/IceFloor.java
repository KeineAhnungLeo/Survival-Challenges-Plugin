package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IceFloor implements Listener {
    private static BukkitRunnable task;
    public static final List<UUID> ACTIVE_PLAYER = new ArrayList<>();

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.ICE_FLOOR)) {
            Player player = event.getPlayer();
            if(player.isSneaking()) return;
            if(ACTIVE_PLAYER.contains(player.getUniqueId())) {
                ACTIVE_PLAYER.remove(player.getUniqueId());
            }
            else {
                ACTIVE_PLAYER.add(player.getUniqueId());
            }
        }
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                if(!challenges.isActive(Challenges.Challenge.ICE_FLOOR)) {
                    task.cancel();
                    task = null;
                    return;
                }
                ACTIVE_PLAYER.forEach(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if(player == null) return;
                    if(player.getGameMode() == GameMode.SPECTATOR) return;
                    for (int x = -1; x < 2; x++) {
                        for (int z = -1; z < 2; z++) {
                            Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX()+x, player.getLocation().getBlockY()-1, player.getLocation().getBlockZ()+z);
                            if(block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.LAVA) {
                                block.setType(Material.PACKED_ICE);
                            }
                        }
                    }
                });
            }
        };
        task.runTaskTimer(plugin, 0L, 1);
    }
}
