package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Speedy{
    private static BukkitRunnable task;
    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!challenges.isActive(Challenges.Challenge.SPEEDY)) {
                    for (Player player : Bukkit.getOnlinePlayers()){
                        AttributeInstance attribute = (player.getAttribute(Attribute.STEP_HEIGHT));
                        if (attribute == null) continue;
                        attribute.setBaseValue(0.6);
                    }
                    task.cancel();
                    task = null;
                    return;
                }
                if (!timer.isRunning()) return;
                for (World world : Bukkit.getWorlds())
                    for (LivingEntity entity : world.getLivingEntities())
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 30));
                for (Player player : Bukkit.getOnlinePlayers()){
                    AttributeInstance attribute = (player.getAttribute(Attribute.STEP_HEIGHT));
                    if (attribute == null) continue;
                    attribute.setBaseValue(1);
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20);
    }
}
