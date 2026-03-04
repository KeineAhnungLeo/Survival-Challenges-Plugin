package de.SurvivalChallengesPlugin.general.backpack.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.datamanager.BackpackManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class Backpack implements Listener {
    private final BackpackManager manager;
    public Backpack(BackpackManager BackpackManager) {
        this.manager = BackpackManager;
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (player.getOpenInventory().getTitle().equals(ChatColor.BLUE + "Backpack"))
            manager.savePlayer(uuid, inventory);
        if (player.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Backpack"))
            manager.saveGlobal(inventory);
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!player.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Backpack")) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        for(Player player1 : Bukkit.getOnlinePlayers()){
            if(player1 == player) continue;
            if(player1.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Backpack")){
                player1.getOpenInventory().getTopInventory().setContents(player.getOpenInventory().getTopInventory().getContents());
            }
        }
    }
}
