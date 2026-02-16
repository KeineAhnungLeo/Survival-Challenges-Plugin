package de.SurvivalChallengesPlugin.general.settings.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
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
    public void onEntitySpawn(EntitySpawnEvent event) {
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
    public void onHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player healedPlayer)) return;
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

        if (settings.isSettingSameHealth()) {
            if (healedPlayer.hasMetadata("shared_heal")) return;

            double healAmount = event.getAmount();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.equals(healedPlayer)) continue;
                if (player.isDead()) continue;

                AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
                if (attribute == null) continue;
                double maxHealth = attribute.getValue();

                double newHealth = Math.min(
                        player.getHealth() + healAmount,
                        maxHealth
                );

                player.setMetadata("shared_heal", new FixedMetadataValue(plugin, true));
                player.setHealth(newHealth);
                player.removeMetadata("shared_heal", plugin);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player newPlayer = event.getPlayer();

        double healthToUse = -1;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(newPlayer)) continue;
            AttributeInstance attr = p.getAttribute(Attribute.MAX_HEALTH);
            if (attr == null) continue;

            healthToUse = p.getHealth();
            break;
        }

        if (healthToUse < 0) return;

        AttributeInstance attr = newPlayer.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            newPlayer.setHealth(Math.min(healthToUse, attr.getValue()));
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
                if(settings.isSettingTimerPause())
                    timer.setRunning(false);
                if(!settings.isSettingDeathScreen()) {
                    event.setCancelled(true);
                    player.setHealth(0.1);
                    player.setGameMode(GameMode.SPECTATOR);
                    AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
                    if (attr != null) {
                        player.setHealth(attr.getValue());
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
                if(killAllPlayers){
                    Bukkit.getScheduler().runTaskLater(SurvivalChallengesPlugin.getInstance(), () -> killAllPlayers = false, 1L);
                }
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
                    player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + message);
                }
            }
        }
        if (settings.isSettingSameHealth()) {
            if (player.hasMetadata("shared_damage")) return;
            double damage = Math.min(event.getFinalDamage(), player.getHealth());
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                if (player1.equals(player)) continue;
                if (player1.isDead()) continue;
                player1.setMetadata("shared_damage", new FixedMetadataValue(plugin, true));
                player1.damage(damage);
                player1.removeMetadata("shared_damage", plugin);
            }
        }
        if (settings.isSettingDamageLogger()) {
            double damage = event.getFinalDamage();
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
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Death" + ChatColor.GRAY + "] " + message);
            }
        }
    }
}
