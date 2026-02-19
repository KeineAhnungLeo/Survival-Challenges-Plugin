package de.SurvivalChallengesPlugin.general.settings.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import static de.SurvivalChallengesPlugin.general.challenges.events.DelayedDamage.getSetDamage;

public class Settings implements Listener {
    private final Plugin plugin;
    public Settings(Plugin plugin) {
        this.plugin = plugin;
    }
    private static boolean killAllPlayers = false;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) return;
        if (settings.isSettingLimitedPlayer())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) return;
        if (settings.isSettingLimitedPlayer())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) return;
        if (settings.isSettingLimitedPlayer())
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) return;
        if (settings.isSettingLimitedPlayer())
            event.setCancelled(true);
    }

    @EventHandler
    public void onSwapOffhand(PlayerSwapHandItemsEvent event) {
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) return;
        if (settings.isSettingLimitedPlayer())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event){
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) return;
        if (settings.isSettingLimitedPlayer())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHitEntity(EntityDamageByEntityEvent event){
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) return;
        if (settings.isSettingLimitedPlayer())
            event.setCancelled(true);
    }


    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && settings.getSettingRegeneration() == 2) {
            event.setCancelled(true);
            return;
        }
        if (settings.getSettingRegeneration() == 0) {
            event.setCancelled(true);
            return;
        }

        if (settings.isSettingSplitHearts()) {
            if (!(event.getEntity() instanceof Player player)) return;
            Bukkit.getScheduler().runTaskLater(SurvivalChallengesPlugin.getInstance(), () -> {
                double newHealth = player.getHealth();
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (player1 == player) continue;
                    if (player1.isDead()) continue;
                    AttributeInstance attribute = player1.getAttribute(Attribute.MAX_HEALTH);
                    if (attribute == null) continue;
                    double max = attribute.getValue();
                    player1.setHealth(Math.max(0.0, Math.min(newHealth, max)));
                }
                double oldAbs = player.getAbsorptionAmount();
                double newAbs = oldAbs + event.getAmount();
                double addedAbs = newAbs - oldAbs;
                if (addedAbs <= 0) return;
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (player1 == player) continue;
                    player1.setAbsorptionAmount(player1.getAbsorptionAmount() + addedAbs);
                }
            },2);
        }
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning()) {
            if (settings.getSettingBossRequired() >= 1) {
                switch (settings.getSettingBossRequired()) {
                    case 1:
                        if (event.getEntityType() == EntityType.ENDER_DRAGON)
                            endChallenge(EntityType.ENDER_DRAGON);
                        break;
                    case 2:
                        if (event.getEntityType() == EntityType.WITHER)
                            endChallenge(EntityType.WITHER);
                        break;
                    case 3:
                        if (event.getEntityType() == EntityType.ELDER_GUARDIAN)
                            endChallenge(EntityType.ELDER_GUARDIAN);
                        break;
                    case 4:
                        if (event.getEntityType() == EntityType.WARDEN)
                            endChallenge(EntityType.WARDEN);
                        break;
                }
            }
        }
    }

    private void endChallenge(EntityType entityType){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        String boss = null;
        if(entityType == EntityType.ENDER_DRAGON)
            boss = "Ender dragon";
        else if(entityType == EntityType.WITHER)
            boss = "Wither";
        else if(entityType == EntityType.ELDER_GUARDIAN)
            boss = "Elder guardian";
        else if(entityType == EntityType.WARDEN)
            boss = "Warden";
        timer.setRunning(false);
        for(Player player1 : Bukkit.getOnlinePlayers()){
            player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD +  boss + ChatColor.GRAY + "] " + ChatColor.GREEN + "Timer paused");
            player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD +  boss + ChatColor.GRAY + "] " + "The " + ChatColor.GOLD + ChatColor.BOLD + boss + ChatColor.RESET + ChatColor.GRAY + " has been killed");
            player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + boss + ChatColor.GRAY + "] " + "Time needed: " + ChatColor.GOLD + ChatColor.BOLD + timer.getTimeD() + "d " + timer.getTimeH() + "h " + timer.getTimeM() + "m " + timer.getTimeS() + "s");
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCancelled()) return;
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (!timer.isRunning()){
            if(settings.isSettingLimitedPlayer())
                event.setCancelled(true);
            return;
        }
        if(challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE) && !getSetDamage()) return;
        if(settings.isSettingDamageInvClear()){
            for(Player player1 : Bukkit.getOnlinePlayers()){
                player1.getInventory().clear();
                player1.getInventory().setArmorContents(null);
                player1.getInventory().setExtraContents(null);
            }
        }
        if (settings.getSettingHardcore() >= 1) {
            double damage = event.getFinalDamage();
            if(killAllPlayers) return;
            if (player.getHealth() - damage <= 0) {
                //Totem
                ItemStack itemOffhand = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
                ItemStack itemHand = player.getInventory().getItem(EquipmentSlot.HAND);
                if(itemHand != null)
                    if(itemHand.getType() == Material.TOTEM_OF_UNDYING) return;
                if(itemOffhand != null)
                    if(itemOffhand.getType() == Material.TOTEM_OF_UNDYING) return;
                if(settings.isSettingTimerPause() && timer.isRunning()) {
                    timer.setRunning(false);
                    for(Player player1 : Bukkit.getOnlinePlayers()){
                        player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + ChatColor.RED + "Timer paused");
                        player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + "Time wasted: " + ChatColor.GOLD + ChatColor.BOLD + timer.getTimeD() + "d " + timer.getTimeH() + "h " + timer.getTimeM() + "m " + timer.getTimeS() + "s");
                    }
                }
                if(!settings.isSettingDeathScreen()) {
                    event.setCancelled(true);
                    player.setHealth(0.1);
                    player.setGameMode(GameMode.SPECTATOR);
                    if(settings.isSettingSplitHearts()){
                        for(Player player1 : Bukkit.getOnlinePlayers()){
                            AttributeInstance attr = player1.getAttribute(Attribute.MAX_HEALTH);
                            if (attr != null) {
                                player1.setHealth(attr.getValue());
                            }
                        }
                    }
                    else {
                        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
                        if (attr != null) {
                            player.setHealth(attr.getValue());
                        }
                    }
                }
                else
                    player.setGameMode(GameMode.SPECTATOR);
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    String cause = event.getCause().name();
                    String message = ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died to " + ChatColor.GOLD + cause.toLowerCase();
                    if (event instanceof EntityDamageByEntityEvent entityEvent) {
                        cause = entityEvent.getDamager().getType().name();
                        message = ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died to " + ChatColor.GOLD + cause.toLowerCase();
                    } else if (event instanceof EntityDamageByBlockEvent blockEvent) {
                        if(blockEvent.getDamager() == null) break;
                        cause = blockEvent.getDamager().getType().name();
                        message = ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died to " + ChatColor.GOLD + cause.toLowerCase();
                    }
                    if(event.getCause().name().equals("CUSTOM"))
                        player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died");
                    else
                        player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + message);
                }
                if (settings.getSettingHardcore() == 2) {
                    killAllPlayers = true;
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        if(settings.isSettingDeathScreen()){
                            player1.setHealth(0);
                        }
                        else
                            player1.setGameMode(GameMode.SPECTATOR);
                    }
                }
                if(killAllPlayers)
                    Bukkit.getScheduler().runTaskLater(SurvivalChallengesPlugin.getInstance(), () -> killAllPlayers = false, 1L);
                return;
            }
        }
        else {
            if(killAllPlayers) return;
            double damage = event.getFinalDamage();
            if (player.getHealth() - damage <= 0) {
                //Totem
                ItemStack itemOffhand = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
                ItemStack itemHand = player.getInventory().getItem(EquipmentSlot.HAND);
                if(itemHand != null)
                    if(itemHand.getType() == Material.TOTEM_OF_UNDYING) return;
                if(itemOffhand != null)
                    if(itemOffhand.getType() == Material.TOTEM_OF_UNDYING) return;
                if(settings.isSettingTimerPause())
                    timer.setRunning(false);
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    String cause = event.getCause().name();
                    String message = ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died to " + ChatColor.GOLD + cause.toLowerCase();
                    if (event instanceof EntityDamageByEntityEvent entityEvent) {
                        cause = entityEvent.getDamager().getType().name();
                        message = ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died to " + ChatColor.GOLD + cause.toLowerCase();
                    } else if (event instanceof EntityDamageByBlockEvent blockEvent) {
                        if(blockEvent.getDamager() == null) break;
                        cause = blockEvent.getDamager().getType().name();
                        message = ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died to " + ChatColor.GOLD + cause.toLowerCase();
                    }
                    if(event.getCause().name().equals("CUSTOM"))
                        player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died");
                    else
                        player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + message);
                }
            }
        }
        if (settings.isSettingSplitHearts()) {
            if(killAllPlayers) return;
            Bukkit.getScheduler().runTask(SurvivalChallengesPlugin.getInstance(), () -> {
                double newHealth = player.getHealth();
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    if (player1 == player) continue;
                    if (player1.isDead()) continue;
                    AttributeInstance attribute = player1.getAttribute(Attribute.MAX_HEALTH);
                    if (attribute == null) continue;
                    double max = attribute.getValue();
                    player1.setHealth(Math.max(0.0, Math.min(newHealth, max)));
                }
            });
        }
        if (settings.isSettingDamageLogger()) {
            double damage = event.getFinalDamage();
            if (player.getHealth() - damage <= 0) return;
            damage = Math.floor(damage * 10) / 10;
            String cause = event.getCause().name();
            String message = ChatColor.BLUE + player.getName() + ChatColor.GRAY + " took " + ChatColor.BLUE + damage + ChatColor.GRAY + " hearts damage from " + ChatColor.BLUE + cause.toLowerCase();
            if (event instanceof EntityDamageByEntityEvent entityEvent) {
                cause = entityEvent.getDamager().getType().name();
                message = ChatColor.BLUE + player.getName() + ChatColor.GRAY + " took " + ChatColor.BLUE + damage + ChatColor.GRAY + " hearts damage from " + ChatColor.BLUE + cause.toLowerCase();
            } else if (event instanceof EntityDamageByBlockEvent blockEvent) {
                if(blockEvent.getDamager() == null) return;
                cause = blockEvent.getDamager().getType().name();
                message = ChatColor.BLUE + player.getName() + ChatColor.GRAY + " took " + ChatColor.BLUE + damage + ChatColor.GRAY + " hearts damage from " + ChatColor.BLUE + cause.toLowerCase();
            }
            if (cause.equalsIgnoreCase("custom")) return;
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + message);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        event.setDeathMessage(null);
    }

    public static void iniSplitPlayerHearts() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isDead()) {
                for(Player player1 : Bukkit.getOnlinePlayers()){
                    if(player1 == player) continue;
                    player1.setHealth(player.getHealth());
                }
                return;
            }
        }
    }

    public static void killPlayerCustom(Player player){
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(settings.isSettingTimerPause() && timer.isRunning()) {
            timer.setRunning(false);
            for(Player player1 : Bukkit.getOnlinePlayers()){
                player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + ChatColor.RED + "Timer paused");
                player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + "Time wasted: " + ChatColor.GOLD + ChatColor.BOLD + timer.getTimeD() + "d " + timer.getTimeH() + "h " + timer.getTimeM() + "m " + timer.getTimeS() + "s");
                player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " died");
            }
        }
        if(!settings.isSettingDeathScreen()) {
            player.setHealth(0.1);
            player.setGameMode(GameMode.SPECTATOR);
            if(settings.isSettingSplitHearts()){
                for(Player player1 : Bukkit.getOnlinePlayers()){
                    AttributeInstance attr = player1.getAttribute(Attribute.MAX_HEALTH);
                    if (attr != null) {
                        player1.setHealth(attr.getValue());
                    }
                }
            }
            else {
                AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
                if (attr != null) {
                    player.setHealth(attr.getValue());
                }
            }
        }
        else {
            player.setHealth(0);
            player.setGameMode(GameMode.SPECTATOR);
        }

        if (settings.getSettingHardcore() == 2) {
            killAllPlayers = true;
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if(settings.isSettingDeathScreen()){
                    player1.setHealth(0);
                }
                else
                    player1.setGameMode(GameMode.SPECTATOR);
            }
        }
        if(killAllPlayers)
            Bukkit.getScheduler().runTaskLater(SurvivalChallengesPlugin.getInstance(), () -> killAllPlayers = false, 1L);
    }
}
