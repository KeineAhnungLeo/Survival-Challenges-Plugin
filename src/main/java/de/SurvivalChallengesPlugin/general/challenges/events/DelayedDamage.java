package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedDamage implements Listener {
    static Double totalDamage = 0.0;
    public static Boolean setDamage = false;
    private static BukkitRunnable task;

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE) && !setDamage) {
            totalDamage = totalDamage + event.getFinalDamage();
            event.getEntity().sendMessage(totalDamage.toString());
            event.setCancelled(true);
        }
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE)) {
                        task.cancel();
                        task = null;
                        return;
                }
                if(timer.getTimeM() % 5 == 0 && timer.getTimeS() == 1 && !(timer.getTimeS() == 0 && timer.getTimeM() == 0 && timer.getTimeH() == 0 && timer.getTimeD() == 0)){
                    if(setDamage){
                        setDamage = false;
                        return;
                    }
                    setDamage = true;
                    if(totalDamage <= 0) return;
                    for (Player player : Bukkit.getOnlinePlayers()){
                        player.damage(totalDamage);
                        player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "DelayedDamage" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "You have suffered " + ChatColor.BLUE + totalDamage + " Hearts" + ChatColor.GRAY + " damage");
                    }
                    totalDamage = 0.0;
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 10);
    }

    public static Boolean getSetDamage() {
        return setDamage;
    }
}
