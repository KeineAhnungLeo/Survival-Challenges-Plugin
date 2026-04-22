package de.SurvivalChallengesPlugin.general.forcebattles.events.teams;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.forcebattles.utils.TaskResult;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.List;

public class Normal implements Listener {
    private static BukkitRunnable task;
    private static BukkitRunnable task1;
    private static final Random random = new Random();
    public static final Map<Team, Material> tasksTeams = new HashMap<>();
    private static final Map<UUID, ItemDisplay> displayPlayers = new HashMap<>();
    public static final Map<Team, List<TaskResult>> doneTasksTeams = new HashMap<>();
    private static final List<Material> itemPool = new ArrayList<>();
    private static final List<Material> mobPool = new ArrayList<>();
    private static final Map<UUID, Integer> currentPage = new HashMap<>();
    private static final Map<Team, List<Inventory>> results = new HashMap<>();
    private static final Map<Integer, List<Team>> places = new LinkedHashMap<>();
    private static final List<Team> resultDisplayOrder = new ArrayList<>();
    private static int currentResultIndex = 0;
    static Scoreboard scoreboard = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getScoreboard();

    @EventHandler
    public void onPlayerKillsMob(EntityDeathEvent event) {
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (timer.isRunning() && forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems() && forceBattles.isForceBattlesMobs()) {
            Player killer = event.getEntity().getKiller();
            if (killer == null) return;
            Material task = tasksTeams.get(de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByPlayer(killer));
            if (task == null) return;
            if (!task.name().endsWith("_SPAWN_EGG")) return;
            String mobName = task.name().replace("_SPAWN_EGG", "");
            EntityType targetType = EntityType.valueOf(mobName);
            if (event.getEntityType() == targetType)
                checkItemTeam(killer, task);
        }
    }

    @EventHandler
    public void onPlayerItemPickup(EntityPickupItemEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(timer.isRunning() && forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems() && forceBattles.isForceBattlesItems()){
            Material material = event.getItem().getItemStack().getType();
            if(!material.name().contains("SPAWN_EGG"))
                checkItemTeam(player, material);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player player)) return;
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(timer.isRunning() && forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems() && forceBattles.isForceBattlesItems()){
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack != null) {
                Material material = itemStack.getType();
                if(!material.name().contains("SPAWN_EGG"))
                    checkItemTeam(player, material);
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if(forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems()){
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemDisplay display = displayPlayers.get(uuid);
            if (display != null && !display.isDead()){
                display.remove();
            }
            displayPlayers.remove(uuid);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if (forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems()) {
            Player player = event.getEntity();
            UUID target = player.getUniqueId();
            ItemDisplay display = displayPlayers.get(target);
            if (display != null) {
                if (!display.isDead())
                    display.remove();
                displayPlayers.remove(target);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(forceBattles.isForceBattlesCustomItems() || !forceBattles.isForceBattlesTeams()) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemStack = event.getItem();
            if (itemStack == null) return;
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) return;
            if (!meta.hasDisplayName()) return;
            if (meta.getDisplayName().equals(ChatColor.RED + "Joker [ForceBattle]")) {

                if (!forceBattles.isForceBattlesEnabled()) {
                    event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "Force Battles are not active");
                    event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    event.setCancelled(true);
                    return;
                }
                if (!timer.isRunning()) {
                    event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "The timer is not running");
                    event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    event.setCancelled(true);
                    return;
                }
                Player player = event.getPlayer();
                Team team = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByPlayer(player);
                if(team ==null){
                    event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "You're not in any team");
                    event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    event.setCancelled(true);
                    return;
                }
                itemStack.setAmount(itemStack.getAmount() - 1);
                Material material = tasksTeams.get(team);
                event.setCancelled(true);
                if(!material.name().contains("SPAWN_EGG")) {
                    HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack(material));
                    if (!leftover.isEmpty()) {
                        for (ItemStack remaining : leftover.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), remaining);
                        }
                    }
                }
                String teamName = team.getName();
                for (Player player1 : de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getPlayers(teamName)) {
                    player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Task " + ChatColor.GOLD + formattedString(material.toString()) + ChatColor.RED + " skipped");
                    player1.playSound(player1, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                }
                //Save done
                List<TaskResult> list = doneTasksTeams.computeIfAbsent(team, k -> new ArrayList<>());
                String string = ((timer.getTimeD() >= 1) ? timer.getTimeD() + ":" : "") + ((timer.getTimeH() <= 9) ? "0" + timer.getTimeH() : timer.getTimeH() + "") + ":" + ((timer.getTimeM() <= 9) ? "0" + timer.getTimeM() : timer.getTimeM() + "") + ":" + ((timer.getTimeS() <= 9) ? "0" + timer.getTimeS() : timer.getTimeS());
                list.add(new TaskResult(material, string, true));

                //New Task
                Material newTask = getRandomTask();
                tasksTeams.put(team, newTask);

                for (Player player1 : de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getPlayers(teamName)) {
                    UUID uuid = player1.getUniqueId();
                    ItemDisplay display = displayPlayers.get(uuid);
                    if (display != null)
                        display.setItemStack(new ItemStack(newTask));
                    player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Next task: " + ChatColor.GOLD + formattedString(newTask.toString()));
                }
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if (forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems()) {
            if (!(event.getWhoClicked() instanceof Player player)) return;
            if (!event.getView().getTitle().startsWith(ChatColor.GOLD + "Results"))
                return;
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) return;

            UUID uuid = player.getUniqueId();
            Team team = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByPlayer(player);
            List<Inventory> invs = results.get(team);
            if (invs == null) return;
            int page = currentPage.getOrDefault(uuid, 0);

            if (meta.getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
                if (page + 1 < invs.size()) {
                    page++;
                    currentPage.put(uuid, page);
                    player.openInventory(invs.get(page));
                }
            } else if (meta.getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
                if (page - 1 >= 0) {
                    page--;
                    currentPage.put(uuid, page);
                    player.openInventory(invs.get(page));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if (forceBattles.isForceBattlesEnabled() && forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems()) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            org.bukkit.Location location = event.getTo();
            if (location == null) return;
            if (event.getFrom().getWorld() == event.getTo().getWorld()) return;
            ItemDisplay display = displayPlayers.get(uuid);
            if (display != null && !display.isDead()) {
                display.remove();
            }
            displayPlayers.remove(uuid);
        }
    }

    private void checkItemTeam(Player player, Material material) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        Team team = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByPlayer(player);
        Material targetMaterial = tasksTeams.get(team);

        if (targetMaterial != null && material == targetMaterial) {
            Material newTask = getRandomTask();
            tasksTeams.put(team, newTask);
            String teamName = team.getName();
            for(Player player1 : de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getPlayers(teamName)){
                UUID uuid = player1.getUniqueId();
                ItemDisplay display = displayPlayers.get(uuid);
                if (display != null && !display.isDead())
                    display.setItemStack(new ItemStack(newTask));
                player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Task " + ChatColor.GOLD + formattedString(material.toString()) + ChatColor.GREEN + " done");
                player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Next task: " + ChatColor.GOLD + formattedString(newTask.toString()));
                player1.playSound(player1, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            }
            //Save done
            List<TaskResult> list = doneTasksTeams.computeIfAbsent(team, k -> new ArrayList<>());
            String string = ((timer.getTimeD() >= 1) ? timer.getTimeD() + ":" : "") + ((timer.getTimeH() <= 9) ? "0" + timer.getTimeH() : timer.getTimeH() + "") + ":" + ((timer.getTimeM() <= 9) ? "0" + timer.getTimeM() : timer.getTimeM() + "") + ":" + ((timer.getTimeS() <= 9) ? "0" + timer.getTimeS() : timer.getTimeS());
            list.add(new TaskResult(material, string, false));
        }
    }

    public static void start(JavaPlugin plugin) {
        itemPool.clear();
        mobPool.clear();
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        List<Material> filter = List.of(Material.DRAGON_EGG, Material.ZOMBIE_HORSE_SPAWN_EGG, Material.SUSPICIOUS_GRAVEL, Material.SUSPICIOUS_SAND, Material.VAULT, Material.DIRT_PATH, Material.PLAYER_HEAD, Material.TEST_BLOCK, Material.TEST_INSTANCE_BLOCK, Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.JIGSAW, Material.LIGHT, Material.DEBUG_STICK, Material.KNOWLEDGE_BOOK, Material.END_PORTAL_FRAME, Material.SPAWNER, Material.REINFORCED_DEEPSLATE, Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.MOVING_PISTON, Material.PISTON_HEAD, Material.FIRE, Material.SOUL_FIRE, Material.NETHER_PORTAL, Material.END_PORTAL, Material.END_GATEWAY, Material.BUBBLE_COLUMN, Material.FROSTED_ICE, Material.KELP_PLANT, Material.TALL_SEAGRASS, Material.WATER, Material.LAVA, Material.POWDER_SNOW, Material.BAMBOO_SAPLING, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.SWEET_BERRY_BUSH, Material.COCOA, Material.MELON_STEM, Material.ATTACHED_MELON_STEM, Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM, Material.TORCHFLOWER_CROP, Material.PITCHER_CROP, Material.REDSTONE_WIRE, Material.TRIPWIRE, Material.TORCHFLOWER, Material.TORCHFLOWER_SEEDS, Material.PITCHER_PLANT, Material.PITCHER_POD);
        List<Material> filterHard = List.of(Material.SNIFFER_SPAWN_EGG, Material.SHULKER_SPAWN_EGG, Material.WARDEN_SPAWN_EGG, Material.ENDER_DRAGON_SPAWN_EGG, Material.WITHER_SPAWN_EGG, Material.WANDERING_TRADER_SPAWN_EGG, Material.SKELETON_HORSE_SPAWN_EGG, Material.PHANTOM_MEMBRANE, Material.PHANTOM_SPAWN_EGG, Material.MULE_SPAWN_EGG, Material.MOOSHROOM_SPAWN_EGG, Material.CREAKING_SPAWN_EGG, Material.END_ROD, Material.SHULKER_SHELL, Material.AMETHYST_CLUSTER, Material.BEE_NEST, Material.BLUE_ICE, Material.CREAKING_HEART, Material.ICE, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.MUSHROOM_STEM, Material.MYCELIUM, Material.GRASS_BLOCK, Material.PODZOL, Material.TURTLE_EGG, Material.TURTLE_HELMET);
        for(Material type : Material.values()){
            if (filter.contains(type)) continue;
            if(forceBattles.isForceBattlesEasierMode()) {
                if (filterHard.contains(type)) continue;
                if(type.name().contains("WEATHERED")) continue;
                if(type.name().contains("OXIDIZED")) continue;
                if(type.name().contains("_ORE")) continue;
                if(type.name().contains("TEMPLATE")) continue;
                if(type.name().contains("DISC")) continue;
                if(type.name().contains("SHULKER_BOX")) continue;
                if(type.name().contains("RESIN")) continue;
                if(type.name().contains("_SKULL")) continue;
                if(type.name().contains("_HEAD")) continue;
                if(type.name().contains("BUD")) continue;
                if(type.name().contains("POTTERY_SHERD")) continue;
                if(type.name().contains("NYLIUM")) continue;
                if(type.name().contains("SCULK")) continue;
                if(type.name().contains("PURPUR")) continue;
                if(type.name().contains("CORAL"))
                    if(!type.name().contains("DEAD")) continue;
            }
            if(!type.isItem()) continue;
            if(type.isAir()) continue;
            if(type.name().contains("WALL_")) continue;
            if(type.name().contains("POTTED")) continue;
            if(type.name().contains("CANDLE_CAKE")) continue;
            if(type.name().contains("INFESTED")) continue;
            if(type.name().contains("SPAWN_EGG")){
                mobPool.add(type);
                continue;
            }
            itemPool.add(type);
        }
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!forceBattles.isForceBattlesEnabled() || !forceBattles.isForceBattlesTeams() || forceBattles.isForceBattlesCustomItems()) {
                    task.cancel();
                    task = null;
                    stop();
                    return;
                } else {
                    for (Team team : scoreboard.getTeams()) {
                        Material taskMaterial = tasksTeams.get(team);
                        if (taskMaterial == null && timer.isRunning()) {
                            taskMaterial = getRandomTask();
                            tasksTeams.put(team, taskMaterial);
                            for (String playerName : team.getEntries()) {
                                Player player = Bukkit.getPlayer(playerName);
                                if(player == null) continue;
                                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Next task: " + ChatColor.GOLD + formattedString(taskMaterial.toString()));
                            }
                        }
                        for (String playerName : team.getEntries()) {
                            Player player = Bukkit.getPlayer(playerName);
                            if (player != null) {
                                UUID target = player.getUniqueId();
                                if (player.getGameMode() != GameMode.SPECTATOR && !player.isDead()) {
                                    if (!displayPlayers.containsKey(target)) {
                                        ItemDisplay display = createNewDisplay(player, taskMaterial);
                                        display.setRotation(0f, 0f);
                                        displayPlayers.put(target, display);
                                    } else {
                                        ItemDisplay display = displayPlayers.get(target);
                                        if (display != null && !display.isDead() && taskMaterial != null)
                                            display.setItemStack(new ItemStack(taskMaterial));
                                    }
                                } else {
                                    ItemDisplay display = displayPlayers.get(target);
                                    if (display != null) {
                                        if (!display.isDead())
                                            display.remove();
                                        displayPlayers.remove(target);
                                    }
                                }
                            }
                        }
                    }
                    for(Player player : Bukkit.getOnlinePlayers()){
                        Team team = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByPlayer(player);
                        if(team == null){
                            UUID target = player.getUniqueId();
                            ItemDisplay display = displayPlayers.get(target);
                            if (display != null) {
                                if (!display.isDead())
                                    display.remove();
                                displayPlayers.remove(target);
                            }
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 10);
    }

    public static void calcTeamPlaces(){
        results.clear();
        for(Map.Entry<Team, List<TaskResult>> entry : doneTasksTeams.entrySet()){
            Team team = entry.getKey();
            List<TaskResult> tasks = entry.getValue();
            String teamName = team.getName();
            Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.GOLD + "Results - " + teamName);
            for (int i = 5*9; i < 6*9; i++)
                inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
            int slot = 0;
            for(TaskResult task : tasks){
                if (slot >= 5*9) {
                    List<Inventory> invs = results.computeIfAbsent(team, k -> new ArrayList<>());
                    inv.setItem(9*6-1, createGuiItem(Material.ARROW, ChatColor.GREEN + "Next Page", ChatColor.GRAY + "Takes you to the next page"));
                    invs.add(inv);
                    slot = 0;

                    inv = Bukkit.createInventory(null, 9*6, ChatColor.GOLD + "Results - " + teamName);
                    for (int i = 5*9; i < 6*9; i++)
                        inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
                    inv.setItem(5*9, createGuiItem(Material.ARROW, ChatColor.GREEN + "Previous Page", ChatColor.GRAY + "Takes you to the previous page"));
                }
                inv.setItem(slot, createGuiResultItem(task.getMaterial(), ChatColor.GOLD + task.getTime(), task.isJoker() ? ChatColor.RED + "[Joker]" : ""));
                slot++;
            }
            List<Inventory> invs = results.computeIfAbsent(team, k -> new ArrayList<>());
            invs.add(inv);
        }

        Map<Team, Integer> scores = new HashMap<>();
        for (Map.Entry<Team, List<TaskResult>> entry : doneTasksTeams.entrySet())
            scores.put(entry.getKey(), entry.getValue().size());

        List<Map.Entry<Team, Integer>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        places.clear();
        int place = 1;
        int lastScore = -1;
        for (Map.Entry<Team, Integer> entry : sorted) {
            int score = entry.getValue();
            if (lastScore != -1 && score < lastScore) {
                place++;
            }
            places.computeIfAbsent(place, k -> new ArrayList<>()).add(entry.getKey());
            lastScore = score;
        }

        resultDisplayOrder.clear();
        List<Integer> sortedPlaceNumbers = new ArrayList<>(places.keySet());
        sortedPlaceNumbers.sort(Collections.reverseOrder());
        for (int p : sortedPlaceNumbers) {
            List<Team> teamsAtPlace = places.get(p);
            if (teamsAtPlace != null) {
                resultDisplayOrder.addAll(teamsAtPlace);
            }
        }
        currentResultIndex = 0;
    }

    public static void showResults(Player player){
        calcTeamPlaces();
        openNextTeamResult();
        player.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Use /nextresult to show the next result");
    }

    public static void showResultsStepByStep(JavaPlugin plugin, List<Inventory> invs) {
        if (invs == null || invs.isEmpty()) return;

        final Inventory[] targetInv = {Bukkit.createInventory(null, 6 * 9, ChatColor.GOLD + "Results")};
        for (int i = 5 * 9; i < 6 * 9; i++)
            targetInv[0].setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        for(Player player1 : Bukkit.getOnlinePlayers())
            player1.openInventory(targetInv[0]);
        final int[] slot = {0};
        final int[] currentInventoryIndex = {0};

        if (task1 != null) return;

        task1 = new BukkitRunnable() {
            @Override
            public void run() {
                de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                if (!forceBattles.isForceBattlesEnabled() || !forceBattles.isForceBattlesTeams() || !forceBattles.isForceBattlesResults() || forceBattles.isForceBattlesCustomItems()) {
                    task1.cancel();
                    task1 = null;
                    forceBattles.setForceBattlesResults(false);
                    return;
                }
                while (currentInventoryIndex[0] < invs.size()) {
                    Inventory currentInv = invs.get(currentInventoryIndex[0]);

                    int maxSlot = 5 * 9;

                    while (slot[0] < maxSlot) {
                        ItemStack itemStack = currentInv.getItem(slot[0]);
                        int currentSlot = slot[0];
                        slot[0]++;

                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                            targetInv[0].setItem(currentSlot, itemStack);
                            for(Player player1 : Bukkit.getOnlinePlayers())
                                if(player1.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Results"))
                                    player1.playSound(player1, Sound.ENTITY_CHICKEN_EGG, 1, 1);
                            return;
                        }
                    }

                    currentInventoryIndex[0]++;
                    slot[0] = 0;

                    if (currentInventoryIndex[0] < invs.size()) {
                        targetInv[0].clear();
                        for (int i = 5 * 9; i < 6 * 9; i++)
                            targetInv[0].setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
                        for(Player player1 : Bukkit.getOnlinePlayers())
                            if(player1.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Results"))
                                player1.playSound(player1, Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
                    }
                    return;
                }
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.playSound(player1, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    if (player1.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Results"))
                        player1.closeInventory();

                    Team currentTeam = resultDisplayOrder.get(currentResultIndex - 1);
                    int score = doneTasksTeams.getOrDefault(currentTeam, Collections.emptyList()).size();

                    int place = -1;
                    for (Map.Entry<Integer, List<Team>> entry : places.entrySet()) {
                        if (entry.getValue().contains(currentTeam)) {
                            place = entry.getKey();
                            break;
                        }
                    }

                    net.md_5.bungee.api.ChatColor placeColor;
                    if (place == 1)
                        placeColor = net.md_5.bungee.api.ChatColor.GOLD;
                    else if (place == 2)
                        placeColor = net.md_5.bungee.api.ChatColor.GRAY;
                    else if (place == 3)
                        placeColor = net.md_5.bungee.api.ChatColor.of("#ce8946");
                    else
                        placeColor = net.md_5.bungee.api.ChatColor.WHITE;

                    String formattedTeamName = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamFormattedName(currentTeam);

                    player1.sendTitle(placeColor + "" + place + "." + formattedTeamName, ChatColor.GOLD + "Completed " + score + " tasks", 10, 100, 20);

                    TextComponent message = new TextComponent("");

                    TextComponent prefix = new TextComponent("[ForceBattle] ");
                    prefix.setColor(net.md_5.bungee.api.ChatColor.GOLD);

                    TextComponent placeComponent = new TextComponent(place + ".");
                    placeComponent.setColor(placeColor);

                    TextComponent teamComponent = new TextComponent(formattedTeamName + " ");
                    teamComponent.setColor(net.md_5.bungee.api.ChatColor.GRAY);

                    TextComponent resultComponent = new TextComponent(score + " results ");
                    resultComponent.setColor(net.md_5.bungee.api.ChatColor.GRAY);

                    TextComponent clickComponent = new TextComponent("[Click]");
                    clickComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/openresult " + currentTeam.getName()));
                    clickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "/openresult " + currentTeam.getName()).create()
                    ));

                    message.addExtra(prefix);
                    message.addExtra(placeComponent);
                    message.addExtra(teamComponent);
                    message.addExtra(resultComponent);
                    message.addExtra(clickComponent);

                    player1.spigot().sendMessage(message);
                }
                task1.cancel();
                task1 = null;
            }
        };
        task1.runTaskTimer(plugin, 0L, 15L);
    }

    public static void openNextTeamResult() {
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if (resultDisplayOrder.isEmpty()) return;
        boolean finished = false;
        if (currentResultIndex >= resultDisplayOrder.size()) {
            finished = true;
            currentResultIndex = 0;
        }
        if (!finished) {
            Team nextTeam = resultDisplayOrder.get(currentResultIndex);
            List<Inventory> invs = results.get(nextTeam);
            showResultsStepByStep(SurvivalChallengesPlugin.getInstance(), invs);
            currentResultIndex++;
        }

        if (finished) {
            forceBattles.setForceBattlesResults(false);
            for(Player player1 : Bukkit.getOnlinePlayers())
                player1.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "All results have been showed");
        }
    }

    private static Material getRandomTask(){
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if(forceBattles.isForceBattlesItems() && forceBattles.isForceBattlesMobs()){
            //Mobs und Items
            boolean returnMob = random.nextBoolean();
            if(returnMob)
                return mobPool.get(random.nextInt(mobPool.size()));
            else
                return itemPool.get(random.nextInt(itemPool.size()));
        } else if(forceBattles.isForceBattlesItems()){
            //nur Items
            return itemPool.get(random.nextInt(itemPool.size()));
        } else{
            //nur mobs
            return mobPool.get(random.nextInt(mobPool.size()));
        }
    }

    public static void setAllTeamsTasksRandom(){
        for(Map.Entry<Team, Material> map : tasksTeams.entrySet()){
            org.bukkit.scoreboard.Team team = map.getKey();
            tasksTeams.put(team, getRandomTask());
        }
    }

    public static String getTaskName(Player player){
        Team team = de.SurvivalChallengesPlugin.general.forcebattles.utils.Team.getTeamByPlayer(player);
        if(team != null)
            return formattedString(String.valueOf(tasksTeams.get(team)));
        return ChatColor.RED + "No team selected";
    }

    private static ItemDisplay createNewDisplay(Player player, Material material){
        ItemDisplay display = (ItemDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.ITEM_DISPLAY);
        display.setItemStack(new ItemStack((material == null) ? Material.AIR : material));
        display.setBillboard(Display.Billboard.FIXED);
        display.setTransformation(display.getTransformation());
        display.setGravity(false);
        display.setPersistent(false);
        display.setViewRange(64);
        display.setShadowRadius(0);
        display.setShadowStrength(0);
        display.setTransformation(new Transformation(new Vector3f(0f, 2.5f, 0f), new Quaternionf(), new Vector3f(1f, 1f, 1f), new Quaternionf()));
        player.addPassenger(display);
        return display;
    }

    public static boolean openSpecificTeamResult(Player targetPlayer, Team team){
        List<Inventory> invs = results.get(team);
        if (invs != null && !invs.isEmpty()) {
            currentPage.put(targetPlayer.getUniqueId(), 0);
            targetPlayer.openInventory(invs.get(0));
            return true;
        }
        return false;
    }

    private static String formattedString(String string){
        return Arrays.stream(string.replace("_SPAWN_EGG", "").split("_")).filter(word -> !word.isEmpty()).map(word -> word.substring(0,1) + word.substring(1).toLowerCase()).reduce((a, b) -> a + " " + b).orElse(string);
    }

    public static void stop(){
        for(Map.Entry<UUID, ItemDisplay> map : displayPlayers.entrySet()){
            ItemDisplay itemDisplay = map.getValue();
            if (!itemDisplay.isDead()){
                itemDisplay.remove();
            }
        }
        displayPlayers.clear();
    }

    private static ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (meta.hasLore())
                meta.setLore(null);
            if (lore.length > 0)
                meta.setLore(Arrays.asList(lore));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createGuiResultItem(Material material, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String raw = material.name();
            if(raw.endsWith("_SPAWN_EGG"))
                raw = raw.replace("_SPAWN_EGG", "");
            String name = Arrays.stream(raw.split("_")).filter(word -> !word.isEmpty()).map(word -> word.substring(0,1) + word.substring(1).toLowerCase()).reduce((a, b) -> a + " " + b).orElse(raw);
            meta.setDisplayName(ChatColor.GREEN + name);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
            if (meta.hasLore())
                meta.setLore(null);
            if (lore.length > 0)
                meta.setLore(Arrays.asList(lore));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }
}
