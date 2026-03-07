package de.SurvivalChallengesPlugin.general.backpack.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.datamanager.BackpackManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Backpack implements CommandExecutor {

    public final Map<UUID, Inventory> backpacks = new HashMap<>();
    private final BackpackManager backpackManager;
    private final Inventory globalBackpack;

    public Backpack(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
        this.globalBackpack = backpackManager.loadGlobal();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "SurvivalChallenges" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
            return false;
        }
        de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
        if(settings.getSettingBackpack() == 1){
            player.openInventory(globalBackpack);
            return true;
        }
        else if(settings.getSettingBackpack() == 2) {
            Inventory inventory = backpacks.get(player.getUniqueId());
            if (inventory == null) {
                inventory = backpackManager.loadPlayer(player.getUniqueId());
                backpacks.put(player.getUniqueId(), inventory);
            }
            player.openInventory(inventory);
            return true;
        }
        else {
            commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Backpack" + ChatColor.GRAY + "] " + ChatColor.RED + "Backpacks are disabled");
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return false;
        }
    }

    public void saveAll(){
        backpackManager.saveGlobal(globalBackpack);
        for(Map.Entry<UUID, Inventory> map : backpacks.entrySet()){
            backpackManager.savePlayer(map.getKey(), map.getValue());
        }
    }

    public void clearAll(){
        if (globalBackpack != null) {
            globalBackpack.clear();
            backpackManager.saveGlobal(globalBackpack);
        }
        for (Map.Entry<UUID, Inventory> entry : backpacks.entrySet()) {
            entry.getValue().clear();
            backpackManager.savePlayer(entry.getKey(), entry.getValue());
        }
    }
}
