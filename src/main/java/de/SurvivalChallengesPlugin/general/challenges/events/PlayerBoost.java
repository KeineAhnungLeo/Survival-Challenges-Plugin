package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class PlayerBoost {
    private static int randomMin;
    private static int randomSec;
    private static BukkitRunnable task;
    private static final Random random = new Random();

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        randomSec = 0;
        randomMin = 0;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.PLAYER_BOOST)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if (timer.isRunning()){
                    if (randomSec == 0 && randomMin == 0) {
                        randomSec = random.nextInt(59) + 1;
                        randomMin = random.nextInt(2) + timer.getTimeM();
                        if(randomMin == timer.getTimeM() && randomSec < timer.getTimeS()) {
                            randomSec = 0;
                            randomMin = 0;
                        }
                        if (randomMin >= 60)
                            randomMin = randomMin - 60;
                        for(Player player : Bukkit.getOnlinePlayers())
                            player.sendMessage(randomMin + " : " + randomSec);
                    }
                    if (timer.getTimeS() == randomSec && timer.getTimeM() == randomMin) {
                        randomSec = 0;
                        randomMin = 0;
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            if(player.getGameMode() == GameMode.SPECTATOR) continue;
                            playerBoost(player);
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 15);
    }

    private static void playerBoost(Player player){
        for (int i = 0; i < 30; i++) {
            boolean boostX = random.nextBoolean();
            boolean boostZ = random.nextBoolean();
            double velocityY;
            double minY = 0.3;
            double maxY = 2.25;
            velocityY = minY + (maxY - minY) * random.nextDouble();
            double velocityX = 0;
            if (boostX) {
                double minX = -3;
                double maxX = 3;
                velocityX = minX + (maxX - minX) * random.nextDouble();
            }
            double velocityZ = 0;
            if (boostZ) {
                double minZ = -3;
                double maxZ = 3;
                velocityZ = minZ + (maxZ - minZ) * random.nextDouble();
            }
            player.setVelocity(new Vector(velocityX, velocityY, velocityZ));
        }
    }

    public static void setRandomTime(int randomMin, int randomSec) {
        PlayerBoost.randomMin = randomMin;
        PlayerBoost.randomSec = randomSec;
    }
}
