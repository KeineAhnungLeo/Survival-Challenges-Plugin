    package de.SurvivalChallengesPlugin.general.forcebattles.commands;

    import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
    import de.SurvivalChallengesPlugin.general.forcebattles.utils.Team;
    import org.bukkit.Bukkit;
    import org.bukkit.ChatColor;
    import org.bukkit.Material;
    import org.bukkit.Sound;
    import org.bukkit.command.Command;
    import org.bukkit.command.CommandExecutor;
    import org.bukkit.command.CommandSender;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.inventory.InventoryClickEvent;
    import org.bukkit.inventory.Inventory;
    import org.bukkit.inventory.ItemFlag;
    import org.bukkit.inventory.ItemStack;
    import org.bukkit.inventory.meta.ItemMeta;

    import java.util.ArrayList;

    public class Teams implements CommandExecutor, Listener {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "This command can only be used by players");
                return false;
            }
            de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
            if(forceBattles.isForceBattlesEnabled()) {
                if (forceBattles.isForceBattlesTeams()) {
                    Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Teams");
                    inventory.setItem(0, createGuiItem(Material.RED_WOOL, ChatColor.RED + "Team Red", "red", player, ChatColor.RED));
                    inventory.setItem(1, createGuiItem(Material.ORANGE_WOOL, ChatColor.GOLD + "Team Orange", "orange", player, ChatColor.GOLD));
                    inventory.setItem(2, createGuiItem(Material.YELLOW_WOOL, ChatColor.YELLOW + "Team Yellow", "yellow", player, ChatColor.YELLOW));
                    inventory.setItem(3, createGuiItem(Material.LIME_WOOL, ChatColor.GREEN + "Team Green", "green", player, ChatColor.GREEN));
                    inventory.setItem(4, createGuiItem(Material.LIGHT_BLUE_WOOL, ChatColor.AQUA + "Team Light Blue", "light_blue", player, ChatColor.AQUA));
                    inventory.setItem(5, createGuiItem(Material.BLUE_WOOL, ChatColor.BLUE + "Team Blue", "blue", player, ChatColor.BLUE));
                    inventory.setItem(6, createGuiItem(Material.PURPLE_WOOL, ChatColor.DARK_PURPLE + "Team Purple", "purple", player, ChatColor.DARK_PURPLE));
                    inventory.setItem(7, createGuiItem(Material.MAGENTA_WOOL, ChatColor.LIGHT_PURPLE + "Team Magenta", "magenta", player, ChatColor.LIGHT_PURPLE));
                    player.openInventory(inventory);
                } else {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "Team mode is not active");
                    return false;
                }
            }
            else{
                player.sendMessage(net.md_5.bungee.api.ChatColor.GRAY + "[" + net.md_5.bungee.api.ChatColor.GOLD + "ForceBattle" + net.md_5.bungee.api.ChatColor.GRAY + "] " + net.md_5.bungee.api.ChatColor.RED + "No Force Battles are active");
                return false;
            }
            return false;
        }

        private static void updateTeams(){
            for (Player player : Bukkit.getOnlinePlayers()){
                if(player.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Teams")){
                    Inventory inventory = player.getOpenInventory().getTopInventory();
                    inventory.setItem(0, createGuiItem(Material.RED_WOOL, ChatColor.RED + "Team Red", "red", player, ChatColor.RED));
                    inventory.setItem(1, createGuiItem(Material.ORANGE_WOOL, ChatColor.GOLD + "Team Orange", "orange", player, ChatColor.GOLD));
                    inventory.setItem(2, createGuiItem(Material.YELLOW_WOOL, ChatColor.YELLOW + "Team Yellow", "yellow", player, ChatColor.YELLOW));
                    inventory.setItem(3, createGuiItem(Material.LIME_WOOL, ChatColor.GREEN + "Team Green", "green", player, ChatColor.GREEN));
                    inventory.setItem(4, createGuiItem(Material.LIGHT_BLUE_WOOL, ChatColor.AQUA + "Team Light Blue", "light_blue", player, ChatColor.AQUA));
                    inventory.setItem(5, createGuiItem(Material.BLUE_WOOL, ChatColor.BLUE + "Team Blue", "blue", player, ChatColor.BLUE));
                    inventory.setItem(6, createGuiItem(Material.PURPLE_WOOL, ChatColor.DARK_PURPLE + "Team Purple", "purple", player, ChatColor.DARK_PURPLE));
                    inventory.setItem(7, createGuiItem(Material.MAGENTA_WOOL, ChatColor.LIGHT_PURPLE + "Team Magenta", "magenta", player, ChatColor.LIGHT_PURPLE));
                }
            }
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
                    playerNames.add(ChatColor.GRAY + "-");
                meta.setLore(playerNames);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            return item;
        }

        @EventHandler
        private void onPlayerClick(InventoryClickEvent event){
            Player player = (Player) event.getWhoClicked();
            if(player.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Teams")){
                event.setCancelled(true);
                de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                if(forceBattles.isForceBattlesTeamSwitch()){
                    ItemStack itemStack = event.getCurrentItem();
                    if(itemStack != null){
                        ItemMeta meta = itemStack.getItemMeta();
                        if(meta!=null) {
                            org.bukkit.scoreboard.Team currentTeam = Team.getTeamByPlayer(player);
                            String clickedTeam = getString(meta);
                            if (clickedTeam != null) {
                                if (currentTeam != null && currentTeam.getName().equals(clickedTeam)) {
                                    Team.removePlayerFromTeam(player);
                                } else {
                                    Team.removePlayerFromTeam(player);
                                    Team.addPlayerToTeam(clickedTeam, player);
                                }
                            }
                            updateTeams();
                        }
                    }
                }
                else{
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    player.sendMessage(net.md_5.bungee.api.ChatColor.GRAY + "[" + net.md_5.bungee.api.ChatColor.GOLD + "ForceBattle" + net.md_5.bungee.api.ChatColor.GRAY + "] " + ChatColor.RED + "Teams cannot be switched");
                }
            }
        }

        private static String getString(ItemMeta meta) {
            String clickedTeam = null;
            String name = meta.getDisplayName();
            if (name.equals(ChatColor.RED + "Team Red"))
                clickedTeam = "red";
            else if (name.equals(ChatColor.GOLD + "Team Orange"))
                clickedTeam = "orange";
            else if (name.equals(ChatColor.YELLOW + "Team Yellow"))
                clickedTeam = "yellow";
            else if (name.equals(ChatColor.GREEN + "Team Green"))
                clickedTeam = "green";
            else if (name.equals(ChatColor.AQUA + "Team Light Blue"))
                clickedTeam = "light_blue";
            else if (name.equals(ChatColor.BLUE + "Team Blue"))
                clickedTeam = "blue";
            else if (name.equals(ChatColor.DARK_PURPLE + "Team Purple"))
                clickedTeam = "purple";
            else if (name.equals(ChatColor.LIGHT_PURPLE + "Team Magenta"))
                clickedTeam = "magenta";
            return clickedTeam;
        }
    }
