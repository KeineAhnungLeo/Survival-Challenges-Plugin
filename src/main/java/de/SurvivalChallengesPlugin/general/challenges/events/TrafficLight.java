package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public class TrafficLight implements Listener {
    private static BukkitRunnable task;
    private static final Random random = new Random();
    private static int randomMin;
    private static int randomSec;
    private static BukkitRunnable task1;
    private static int timeSwitch;
    public static BossBar bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        if (!Objects.equals(from.getWorld(), to.getWorld())) return;
        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        double dz = from.getZ() - to.getZ();
        if ((dx * dx + dy * dy + dz * dz) < 0.01) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(challenges.isActive(Challenges.Challenge.TRAFFIC_LIGHT)) {
            if (timer.isRunning()) {
                if (timeSwitch <= 10 && task1 != null) {
                    if(player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE)
                        de.SurvivalChallengesPlugin.general.settings.events.Settings.killPlayerCustom(player);
                }
            }
        }
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        randomSec = 0;
        randomMin = 0;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.TRAFFIC_LIGHT)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        bossBar.removePlayer(player);
                    }
                    task.cancel();
                    task = null;
                    return;
                }
                if (!timer.isRunning()){
                    for (Player player : Bukkit.getOnlinePlayers())
                        bossBar.removePlayer(player);
                }
                else {
                    for (Player player : Bukkit.getOnlinePlayers())
                        bossBar.addPlayer(player);
                    if (randomSec == 0 && randomMin == 0) {
                        randomSec = random.nextInt(59) + 1;
                        randomMin = random.nextInt(3)+1 + timer.getTimeM();
                        if (randomMin >= 60)
                            randomMin = randomMin - 60;
                    }
                    if (randomSec != -1 && randomMin != -1) {
                        bossBar.setTitle(ChatColor.GREEN + "Move");
                        bossBar.setColor(BarColor.GREEN);
                    }
                    if (timer.getTimeS() == randomSec && timer.getTimeM() == randomMin) {
                        randomSec = -1;
                        randomMin = -1;
                        switchLight(SurvivalChallengesPlugin.getInstance());
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 15);
    }

    private static void switchLight(JavaPlugin plugin){
        timeSwitch = 12;
        bossBar.setColor(BarColor.YELLOW);
        bossBar.setTitle(ChatColor.YELLOW + "Hold");
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        bossBar.setProgress(1);
        if (task1 != null) return;
        task1 = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!challenges.isActive(Challenges.Challenge.TRAFFIC_LIGHT)) {
                    task1.cancel();
                    task1 = null;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        bossBar.removePlayer(player);
                    }
                    return;
                }
                if (timer.isRunning()) {
                    if (timeSwitch >= 0)
                        timeSwitch--;
                    if (timeSwitch == 10){
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                        bossBar.setColor(BarColor.RED);
                        bossBar.setTitle(ChatColor.RED + "Stop");
                        }
                    if(timeSwitch <= 0){
                        randomSec = 0;
                        randomMin = 0;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                        bossBar.setTitle(ChatColor.GREEN + "Move");
                        bossBar.setColor(BarColor.GREEN);
                        task1.cancel();
                        task1 = null;
                    }
                }
            }
        };
        task1.runTaskTimer(plugin, 0L, 20);
    }

    public static void stop(){
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }

    public static void setRandomTime(int randomMin, int randomSec) {
        TrafficLight.randomMin = randomMin;
        TrafficLight.randomSec = randomSec;
    }
}
