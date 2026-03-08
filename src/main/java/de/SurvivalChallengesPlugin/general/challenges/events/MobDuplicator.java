package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class MobDuplicator implements Listener {

    private static boolean ClearMobs = false;

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        var timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;

        var challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.MOB_DUPLICATOR)) {
            if(ClearMobs) {
                event.setCancelled(true);
                return;
            }
            if(!event.getEntity().hasMetadata("duplication"))
                event.getEntity().setMetadata("duplication", new FixedMetadataValue(SurvivalChallengesPlugin.getInstance(), 1));
        }
    }

    @EventHandler
    public void onMobDies(EntityDeathEvent event) {
        var timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        var challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (!challenges.isActive(Challenges.Challenge.MOB_DUPLICATOR)) return;
        int count = event.getEntity().getWorld().getLivingEntities().size();
        if (count >= 1500) {
            ClearMobs = true;
            for (LivingEntity entity : event.getEntity().getWorld().getLivingEntities()) {
                if (entity instanceof Player) continue;
                if (!entity.hasMetadata("duplication")) continue;
                for (MetadataValue value : entity.getMetadata("duplication")) {
                    if (value.getOwningPlugin() != SurvivalChallengesPlugin.getInstance()) continue;
                    int duplication = value.asInt();
                    if (duplication >= 16) {
                        entity.remove();
                    }
                }
            }
            for(Player player : Bukkit.getOnlinePlayers())
                player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MobDuplicator" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Removed required mobs to prevent lag (" + count + " entities)");
            Bukkit.getScheduler().runTaskLater(SurvivalChallengesPlugin.getInstance(), () -> ClearMobs = false, 1L);
        }

        if(ClearMobs) return;
        if (!event.getEntity().hasMetadata("duplication")) {
            event.getEntity().setMetadata("duplication", new FixedMetadataValue(SurvivalChallengesPlugin.getInstance(), 1));
        }
        boolean playerInvolved = false;
        if (event.getEntity().getKiller() != null) playerInvolved = true;
        else if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player) playerInvolved = true;
        }
        if (!playerInvolved) return;
        List<MetadataValue> values = event.getEntity().getMetadata("duplication");
        for (MetadataValue value : values) {
            if (value.getOwningPlugin() == SurvivalChallengesPlugin.getInstance()) {
                int duplication = value.asInt() * 2;
                for (int i = 0; i < duplication; i++) {
                    Entity newMob = event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), event.getEntity().getType());
                    newMob.setMetadata("duplication", new FixedMetadataValue(SurvivalChallengesPlugin.getInstance(), duplication));
                }
            }
        }
    }
}