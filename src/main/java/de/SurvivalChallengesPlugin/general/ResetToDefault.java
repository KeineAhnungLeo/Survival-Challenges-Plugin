package de.SurvivalChallengesPlugin.general;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetToDefault implements Listener {
    private static BukkitRunnable task;

    public static void run(JavaPlugin plugin){
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                if (!challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH) && !challenges.isActive(Challenges.Challenge.JUMP_STRENGTH)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Entity entity : player.getWorld().getEntities()) {
                            if (!(entity instanceof LivingEntity)) continue;
                            AttributeInstance attribute = ((LivingEntity) entity).getAttribute(Attribute.GRAVITY);
                            if (attribute == null) continue;
                            attribute.setBaseValue(0.08);
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 40);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (!challenges.isActive(Challenges.Challenge.SPEEDY)) {
            AttributeInstance attribute = (player.getAttribute(Attribute.STEP_HEIGHT));
            if (attribute == null) return;
            attribute.setBaseValue(0.6);
        }
    }
}
