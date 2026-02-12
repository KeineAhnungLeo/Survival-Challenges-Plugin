package de.SurvivalChallengesPlugin.general.invsee.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class invClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getView().getTitle().contains("'s Inventory"))
            event.setCancelled(true);
    }
}
