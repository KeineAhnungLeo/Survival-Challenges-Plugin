package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

import static de.SurvivalChallengesPlugin.general.challenges.events.DelayedDamage.getSetDamage;

public class DamageRandomTeleport implements Listener {
    private static final Random random = new Random();
    @EventHandler
    public static void onPlayerDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE) && !getSetDamage()) return;
        if(challenges.isActive(Challenges.Challenge.DAMAGE_RANDOM_TELEPORT)) {
            if((player.getHealth() - event.getFinalDamage() <= 0)) return;
            World world = player.getWorld();
            if (world.getEnvironment() == World.Environment.NORMAL || world.getEnvironment() == World.Environment.THE_END) {
                for (int i = 0; i < 20; i++) {
                    int x = player.getLocation().getBlockX() + random.nextInt(100) - 50;
                    int z = player.getLocation().getBlockZ() + random.nextInt(100) - 50;
                    int y = world.getHighestBlockYAt(x, z);
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.WATER) {
                        Location target = block.getLocation().clone();
                        for (int j = block.getY(); j > world.getMinHeight(); j--) {
                            Block current = world.getBlockAt(x, j, z);
                            if (current.getType() != Material.WATER && current.getType() != Material.AIR) {
                                target.setY(j);
                                for (Player player1 : Bukkit.getOnlinePlayers()) {
                                    player1.playSound(player1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                    player1.teleport(target.add(0.5, 1, 0.5));
                                }
                                return;
                            }
                        }
                    }
                    if (!block.getType().isSolid() || block.getType() == Material.LAVA) continue;
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.playSound(player1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        player1.teleport(block.getLocation().add(0.5, 1, 0.5));
                    }
                    return;
                }
            }
            if (world.getEnvironment() == World.Environment.NETHER) {
                for (int i = 0; i < 20; i++) {
                    int x = player.getLocation().getBlockX() + random.nextInt(100) - 50;
                    int z = player.getLocation().getBlockZ() + random.nextInt(100) - 50;
                    for (int j = world.getMinHeight(); j < 127; j++) {
                        Block block = world.getBlockAt(x, j, z);
                        if (!block.getType().isSolid() || block.getType() == Material.LAVA) continue;
                        if (block.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                            if (block.getLocation().add(0, 2, 0).getBlock().getType() == Material.AIR) {
                                for (Player player1 : Bukkit.getOnlinePlayers()) {
                                    player1.playSound(player1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                    player1.teleport(block.getLocation().add(0.5, 1, 0.5));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
