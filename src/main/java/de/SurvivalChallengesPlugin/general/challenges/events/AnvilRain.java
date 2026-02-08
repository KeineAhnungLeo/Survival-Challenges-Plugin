package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AnvilRain{
    private static BukkitRunnable task;
    private static final Random random = new Random();
    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.ANVIL_RAIN)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if(timer.isRunning()){
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getGameMode() == GameMode.SPECTATOR) continue;
                        for (int i = 0; i < 6; i++) {
                            int x = random.nextInt(20)-10;
                            int z = random.nextInt(20)-10;
                            Block block = player.getWorld().getBlockAt(player.getLocation().getBlockX()+x, player.getLocation().getBlockY()+15, player.getLocation().getBlockZ()+z);
                            if(block.getType() != Material.AIR) continue;
                            block.setType(Material.ANVIL);
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 15);
    }
}
