package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class MobDuplicator implements Listener {

    private static boolean ClearMobs = false;

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
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
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.MOB_DUPLICATOR)) {
            int count = event.getEntity().getWorld().getLivingEntities().size();
            if (count >= 1500){
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
                    player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MobDuplicator" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Removed necessary mobs to prevent lags (" + count + " Entities)");
                Bukkit.getScheduler().runTaskLater(SurvivalChallengesPlugin.getInstance(), () -> ClearMobs = false,1L);
            }
            if (event.getEntity().getKiller() == null) return;
            if(ClearMobs) return;
            if(event.getEntity().getType() == EntityType.ENDER_DRAGON) return;
            if (event.getEntity().hasMetadata("duplication")) {
                List<MetadataValue> values = event.getEntity().getMetadata("duplication");
                for (MetadataValue value : values) {
                    if (value.getOwningPlugin() == SurvivalChallengesPlugin.getInstance()) {
                        int duplication = value.asInt();
                        duplication = duplication * 2;
                        for (int i = 1; i <= duplication; i++) {
                            Entity newMob = event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), event.getEntity().getType());
                            newMob.setMetadata("duplication", new FixedMetadataValue(SurvivalChallengesPlugin.getInstance(), duplication));
                        }
                    }
                }
            }
        }
    }
}
