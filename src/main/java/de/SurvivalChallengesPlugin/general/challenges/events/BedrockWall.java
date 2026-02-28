package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BedrockWall implements Listener {
    public static final HashMap<Location, Integer> locations = new HashMap<>();
    private static BukkitRunnable task;
    public static BossBar bossBar = Bukkit.createBossBar(ChatColor.RED + "BedrockWall disabled", BarColor.RED, BarStyle.SOLID);
    private static Integer jokerTime = 0;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            ItemStack itemStack = event.getItem();
            if (itemStack == null) return;
            ItemMeta meta = itemStack.getItemMeta();
            if(meta == null) return;
            if(!meta.hasDisplayName()) return;
            if(meta.getDisplayName().equals(ChatColor.RED + "Joker [BedrockWall]")){
                itemStack.setAmount(itemStack.getAmount() - 1);
                event.setCancelled(true);
                Player player = event.getPlayer();
                jokerTime = 60*20;
                locations.clear();
                for (Player player1 : Bukkit.getOnlinePlayers()){
                    player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BedrockWall" + ChatColor.GRAY + "] " + ChatColor.GREEN + player.getName() + " used a joker");
                    bossBar.addPlayer(player1);
                    player1.playSound(player1, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(jokerTime >= 1)
            bossBar.addPlayer(player);
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.BEDROCK_WALL)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if(timer.isRunning()){
                    if(jokerTime >= 1){
                        bossBar.setProgress(Math.max(0.0, Math.min(1.0, (double) jokerTime / (60*20))));
                        jokerTime--;
                    }
                    else {
                        for (Player player : Bukkit.getOnlinePlayers())
                            bossBar.removePlayer(player);

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getGameMode() == GameMode.SPECTATOR) continue;
                            Location location = player.getLocation().getBlock().getLocation();
                            if (!locations.containsKey(location))
                                locations.put(location.clone(), 140);
                        }
                        Iterator<Map.Entry<Location, Integer>> iterator = locations.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Location, Integer> entry = iterator.next();
                            Location location = entry.getKey().clone();
                            World world = location.getWorld();
                            if (world == null) continue;
                            int time = entry.getValue() - 1;
                            if (time <= 0) {
                                for (int i = world.getMinHeight(); i < world.getMaxHeight(); i++) {
                                    if (!world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4))
                                        continue;
                                    world.getBlockAt(location.getBlockX(), i, location.getBlockZ()).setType(Material.BEDROCK, false);
                                }
                                iterator.remove();
                                continue;
                            }
                            entry.setValue(time);
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1);
    }

    public static void stop(){
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }
}
