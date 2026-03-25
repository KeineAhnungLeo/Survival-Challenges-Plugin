    package de.SurvivalChallengesPlugin.general.forcebattles.commands;

    import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
    import de.SurvivalChallengesPlugin.general.forcebattles.utils.Team;
    import org.bukkit.Bukkit;
    import org.bukkit.ChatColor;
    import org.bukkit.Material;
    import org.bukkit.command.Command;
    import org.bukkit.command.CommandExecutor;
    import org.bukkit.command.CommandSender;
    import org.bukkit.entity.Player;
    import org.bukkit.event.Listener;
    import org.bukkit.inventory.Inventory;
    import org.bukkit.inventory.ItemFlag;
    import org.bukkit.inventory.ItemStack;
    import org.bukkit.inventory.meta.ItemMeta;

    import java.util.ArrayList;
    import java.util.Arrays;

    public class Teams implements CommandExecutor, Listener {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "SurvivalChallenges" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
                return false;
            }
            de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
            if(forceBattles.isForceBattlesTeams()){
                Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Teams");
                inventory.setItem(0, createGuiItem(Material.RED_WOOL, ChatColor.RED + "Team Red", "red", player, ChatColor.RED));
                inventory.setItem(1, createGuiItem(Material.ORANGE_WOOL, ChatColor.GOLD + "Team Orange", "orange", player, ChatColor.GOLD));
                inventory.setItem(2, createGuiItem(Material.YELLOW_WOOL, ChatColor.YELLOW + "Team Yellow", "yellow", player, ChatColor.YELLOW));
                inventory.setItem(3, createGuiItem(Material.LIME_WOOL, ChatColor.GREEN + "Team Green", "green", player, ChatColor.GREEN));
                inventory.setItem(4, createGuiItem(Material.LIGHT_BLUE_WOOL, ChatColor.BLUE + "Team Blue", "light_blue", player, ChatColor.BLUE));
                inventory.setItem(5, createGuiItem(Material.BLUE_WOOL, ChatColor.DARK_BLUE + "Team Blue", "blue", player, ChatColor.DARK_BLUE));
                inventory.setItem(6, createGuiItem(Material.PURPLE_WOOL, ChatColor.DARK_PURPLE + "Team Purple", "purple", player, ChatColor.DARK_PURPLE));
                inventory.setItem(7, createGuiItem(Material.MAGENTA_WOOL, ChatColor.LIGHT_PURPLE + "Team Magenta", "magenta", player, ChatColor.LIGHT_PURPLE));
                player.openInventory(inventory);
            }
            else{
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "Team mode is not active");
                return false;
            }
            return false;
        }

        private static ItemStack createGuiItem(Material material, String name, String nameId, Player player, ChatColor color) {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                org.bukkit.scoreboard.Team team = Team.getTeamByPlayer(player);
                if (team != null) {
                    String teamName = team.getName();
                    if (teamName.equals(nameId)) {
                        meta.setEnchantmentGlintOverride(true);
                    }
                }
                if (meta.hasLore())
                    meta.setLore(null);
                ArrayList<String> playerNames = new ArrayList<>();
                for (Player player1 : Team.getPlayers(nameId))
                    playerNames.add(color + "- " + player1.getName());
                if(playerNames.isEmpty())
                    playerNames.add(color + "-");
                meta.setLore(playerNames);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            return item;
        }
    }
