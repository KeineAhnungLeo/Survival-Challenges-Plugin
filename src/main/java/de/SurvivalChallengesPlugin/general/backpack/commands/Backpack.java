package de.SurvivalChallengesPlugin.general.backpack.commands;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.datamanager.BackpackManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Backpack implements CommandExecutor {

    public final Map<UUID, Inventory> backpacks = new HashMap<>();
    public static final Map<String, Inventory> teamBackpacks = new HashMap<>();
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
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if(settings.getSettingBackpack() == 1){
            if(forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams()){
                Team team = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByPlayer(player);
                if(team==null){
                    commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Backpack" + ChatColor.GRAY + "] " + ChatColor.RED + "You're not in any team");
                    return false;
                }
                String name = team.getName();
                Inventory inventory = teamBackpacks.get(name);
                if(inventory == null){
                    inventory = backpackManager.loadTeam(name);
                    teamBackpacks.put(name, inventory);
                }
                player.openInventory(inventory);
            }
            else {
                player.openInventory(globalBackpack);
                return true;
            }
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
        return false;
    }

    public void saveAll(){
        backpackManager.saveGlobal(globalBackpack);
        for(Map.Entry<UUID, Inventory> map : backpacks.entrySet()){
            backpackManager.savePlayer(map.getKey(), map.getValue());
        }
        for(Map.Entry<String, Inventory> map : teamBackpacks.entrySet()){
            backpackManager.saveTeam(map.getKey(), map.getValue());
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
        for (Map.Entry<String, Inventory> entry : teamBackpacks.entrySet()) {
            entry.getValue().clear();
            backpackManager.saveTeam(entry.getKey(), entry.getValue());
        }
    }

    public void clearTeams(){
        for (Map.Entry<String, Inventory> entry : teamBackpacks.entrySet()) {
            entry.getValue().clear();
            backpackManager.saveTeam(entry.getKey(), entry.getValue());
        }
    }
}
