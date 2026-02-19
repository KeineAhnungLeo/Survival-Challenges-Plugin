package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChunkDisappear implements Listener {
    public static final Map<UUID, BossBar> barMap = new HashMap<>();
    public static final HashMap<Chunk, Integer> activeChunks = new HashMap<>();
    public static final Set<Chunk> doneChunks = new HashSet<>();
    private static BukkitRunnable task;
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.CHUNK_DISAPPEAR)) {
            if (event.getTo() == null) return;
            Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.SPECTATOR) return;
            if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;
            if (activeChunks.containsKey(event.getTo().getChunk())) return;
            World world = event.getTo().getWorld();
            if(world == null) return;
            if(doneChunks.contains(event.getTo().getChunk())) return;
            activeChunks.put(event.getTo().getChunk(), 1180);
        }
    }
    public static void start(JavaPlugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()){
            activeChunks.put(player.getLocation().getChunk(), 1180);
        }
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                if(!challenges.isActive(Challenges.Challenge.CHUNK_DISAPPEAR)) {
                    for (Map.Entry<UUID, BossBar> map : barMap.entrySet()){
                        BossBar bar = barMap.remove(map.getKey());
                        if(bar != null){
                            bar.removeAll();
                        }
                    }
                    task.cancel();
                    task = null;
                    return;
                }
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!timer.isRunning()) return;
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(barMap.containsKey(player.getUniqueId())) continue;
                    BossBar bar = Bukkit.createBossBar(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "00:59", BarColor.PURPLE, BarStyle.SOLID);
                    bar.setProgress(1);
                    bar.addPlayer(player);
                    barMap.put(player.getUniqueId(), bar);
                }
                for (Map.Entry<UUID, BossBar> map : barMap.entrySet()){
                    if(Bukkit.getPlayer(map.getKey()) == null){
                        BossBar bar = barMap.remove(map.getKey());
                        if(bar != null){
                            bar.removeAll();
                        }
                    }
                }
                activeChunks.replaceAll((k, v) -> Math.max(0, v - 1));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Integer ticks = activeChunks.get(player.getLocation().getChunk());
                    if(ticks != null) {
                        int time = ticks / 20;
                        if (activeChunks.get(player.getLocation().getChunk()) != null) {
                            barMap.get(player.getUniqueId()).setProgress((double) time / 59.0);
                            if (time <= 9)
                                barMap.get(player.getUniqueId()).setTitle(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "00:0" + time);
                            else
                                barMap.get(player.getUniqueId()).setTitle(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "00:" + time);
                        }
                    }
                    else{
                        barMap.get(player.getUniqueId()).setTitle(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "00:00");
                        barMap.get(player.getUniqueId()).setProgress(0.0);
                    }
                }
                for (Map.Entry<Chunk, Integer> map : activeChunks.entrySet()){
                    if(map.getValue() == 0){
                        int chunkX = map.getKey().getX();
                        int chunkZ = map.getKey().getZ();
                        for (int x = 0; x < 16; x++) {
                            for (int y = map.getKey().getWorld().getMinHeight(); y < map.getKey().getWorld().getMaxHeight(); y++) {
                                for (int z = 0; z < 16; z++) {
                                    World world = map.getKey().getWorld();
                                    Block block = world.getBlockAt(chunkX * 16 + x, y, chunkZ * 16 + z);
                                    if(block.getType() != Material.AIR)
                                        block.setType(Material.AIR, false);
                                }
                            }
                        }
                        doneChunks.add(map.getKey());
                        activeChunks.remove(map.getKey());
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1);
    }

    public static void stop(){
        for (Map.Entry<UUID, BossBar> map : barMap.entrySet()){
            BossBar bar = barMap.remove(map.getKey());
            if(bar != null){
                bar.removeAll();
            }
        }
    }
}