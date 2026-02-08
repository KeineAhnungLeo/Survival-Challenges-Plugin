package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemPickupDamage implements Listener {
    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.ITEM_PICKUP_DAMAGE)) {
            ItemStack stack = event.getItem().getItemStack();
            double damage = stack.getAmount();
            player.damage(damage);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.ITEM_PICKUP_DAMAGE)) {
            Player player = (Player) event.getWhoClicked();
            if(player.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "challenge menu") || player.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "challenges menu") || player.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "options menu")) return;
            ItemStack stack = event.getCurrentItem();
            if(stack == null) return;
            double damage = stack.getAmount();
            player.damage(damage);
        }
    }
}
