package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import static de.SurvivalChallengesPlugin.general.challenges.events.DelayedDamage.getSetDamage;

public class DamageJump implements Listener {
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE) && !getSetDamage()) return;
        if(challenges.isActive(Challenges.Challenge.DAMAGE_JUMP)) {
            player.setSneaking(false);
            if((player.getHealth() - event.getFinalDamage() <= 0)) return;
            Bukkit.getScheduler().runTaskLater(SurvivalChallengesPlugin.getInstance(), () -> player.setVelocity(new Vector(0, 3, 0)), 1L);
        }
    }
}
