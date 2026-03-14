package de.SurvivalChallengesPlugin.general.forcebattles.events.single;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Normal implements Listener {
    private static BukkitRunnable task;
    private static BukkitRunnable task1;
    private static final Random random = new Random();
    private static final Map<UUID, Material> tasksPlayers = new HashMap<>();
    private static final Map<UUID, ItemDisplay> displayPlayers = new HashMap<>();
    private static final Map<UUID, List<TaskResult>> doneTasksPlayers = new HashMap<>();
    private static final Map<UUID, List<Inventory>> results = new HashMap<>();
    private static final List<Material> itemPool = new ArrayList<>();
    private static final Map<Integer, List<UUID>> places = new LinkedHashMap<>();
    private static final Map<UUID, Integer> currentPage = new HashMap<>();
    private static final List<UUID> resultDisplayOrder = new ArrayList<>();
    private static int currentResultIndex = 0;

    @EventHandler
    public void onPlayerItemPickup(EntityPickupItemEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(timer.isRunning() && forceBattles.isForceBattlesEnabled() && !forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems()){
            checkItemPlayer(player, event.getItem().getItemStack().getType());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player player)) return;
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(timer.isRunning() && forceBattles.isForceBattlesEnabled() && !forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems()){
            ItemStack itemStack = event.getCurrentItem();
            if(itemStack != null)
                checkItemPlayer(player, itemStack.getType());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(timer.isRunning() && forceBattles.isForceBattlesEnabled() && !forceBattles.isForceBattlesTeams() && !forceBattles.isForceBattlesCustomItems()){
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemDisplay display = displayPlayers.get(uuid);
            if (!display.isDead()){
                display.remove();
            }
            displayPlayers.remove(uuid);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            ItemStack itemStack = event.getItem();
            if (itemStack == null) return;
            ItemMeta meta = itemStack.getItemMeta();
            if(meta == null) return;
            if(!meta.hasDisplayName()) return;
            if(meta.getDisplayName().equals(ChatColor.RED + "Joker [ForceBattle]")){
                de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!forceBattles.isForceBattlesEnabled()){
                    event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "Force Battles are not active");
                    event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    event.setCancelled(true);
                    return;
                }
                if(!timer.isRunning()){
                    event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "The timer is not running");
                    event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    event.setCancelled(true);
                    return;
                }
                itemStack.setAmount(itemStack.getAmount() - 1);
                event.setCancelled(true);
                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();

                Material material = tasksPlayers.get(uuid);
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack(material));
                if (!leftover.isEmpty()) {
                    for (ItemStack remaining : leftover.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), remaining);
                    }
                }
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Task " + ChatColor.GOLD + formattedString(material.toString()) + ChatColor.RED + " skipped");
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                //Save done
                List<TaskResult> list = doneTasksPlayers.computeIfAbsent(uuid, k -> new ArrayList<>());
                String string = ((timer.getTimeD() >= 1) ? timer.getTimeD() : "") + ":" + ((timer.getTimeH() <= 9) ? "0" + timer.getTimeH() : timer.getTimeH() + "") + ":" + ((timer.getTimeM() <= 9) ? "0" + timer.getTimeM() : timer.getTimeM() + "") + ":" + ((timer.getTimeS() <= 9) ? "0" + timer.getTimeS() : timer.getTimeS());
                list.add(new TaskResult(material, string, true));
                //New Task
                Material newTask = getRandomItem();
                tasksPlayers.put(uuid, newTask);
                ItemDisplay display = displayPlayers.get(uuid);
                if(display != null)
                    display.setItemStack(new ItemStack(newTask));
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Next task: " + ChatColor.GOLD + formattedString(newTask.toString()));
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().startsWith(ChatColor.GOLD + "Results"))
            return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        UUID uuid = player.getUniqueId();
        List<Inventory> invs = results.get(uuid);
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

    private void checkItemPlayer(Player player, Material material) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        UUID uuid = player.getUniqueId();
        Material targetMaterial = tasksPlayers.get(uuid);
        if (targetMaterial == null) targetMaterial = getRandomItem();
        if (targetMaterial != null && material == targetMaterial) {
            Material newTask = getRandomItem();
            tasksPlayers.put(uuid, newTask);
            ItemDisplay display = displayPlayers.get(uuid);
            if (display != null && !display.isDead())
                display.setItemStack(new ItemStack(newTask));
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Task " + ChatColor.GOLD + formattedString(material.toString()) + ChatColor.GREEN + " done");
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Next task: " + ChatColor.GOLD + formattedString(newTask.toString()));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            //Save done
            List<TaskResult> list = doneTasksPlayers.computeIfAbsent(uuid, k -> new ArrayList<>());
            String string = ((timer.getTimeD() >= 1) ? timer.getTimeD() : "") + ":" + ((timer.getTimeH() <= 9) ? "0" + timer.getTimeH() : timer.getTimeH() + "") + ":" + ((timer.getTimeM() <= 9) ? "0" + timer.getTimeM() : timer.getTimeM() + "") + ":" + ((timer.getTimeS() <= 9) ? "0" + timer.getTimeS() : timer.getTimeS());
            list.add(new TaskResult(material, string, false));
        }
    }

    public static void start(JavaPlugin plugin) {
        for(Material type : Material.values()){
            if(!type.isItem()) continue;
            if(type.isAir()) continue;
            if(type == Material.BUBBLE_COLUMN || type == Material.BEDROCK) continue;
            if(type.name().contains("WALL_")) continue;
            if(type.name().contains("POTTED")) continue;
            if(type.name().contains("CANDLE_CAKE")) continue;
            itemPool.add(type);
        }
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!forceBattles.isForceBattlesEnabled() || forceBattles.isForceBattlesTeams() || forceBattles.isForceBattlesCustomItems()) {
                    task.cancel();
                    task = null;
                    stop();
                    return;
                }
                else {
                    if (timer.isRunning()) {
                        UUID target = null;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            target = player.getUniqueId();
                            Material taskMaterial;
                            taskMaterial = tasksPlayers.get(target);
                            if (taskMaterial == null) {
                                taskMaterial = getRandomItem();
                                tasksPlayers.put(target, taskMaterial);
                                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Next task: " + ChatColor.GOLD + formattedString(taskMaterial.toString()));
                            }
                            if (!displayPlayers.containsKey(target)) {
                                ItemDisplay display = createNewDisplay(player, taskMaterial);
                                display.setRotation(0f, 0f);
                                displayPlayers.put(target, display);
                            } else {
                                ItemDisplay display = displayPlayers.get(target);
                                if (display != null && !display.isDead())
                                    display.setItemStack(new ItemStack(taskMaterial));
                            }
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 5);
    }

    public static void showResults(Player player){
        calcPlayerPlaces();
        openNextPlayerResult();
        player.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Use /nextresult to show the next result");
    }


    public static void showResultsStepByStep(JavaPlugin plugin, Player player, List<Inventory> invs) {
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
                if (!forceBattles.isForceBattlesEnabled() || forceBattles.isForceBattlesTeams() || forceBattles.isForceBattlesCustomItems() || !forceBattles.isForceBattlesResults()) {
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
                                player1.playSound(player1, Sound.BLOCK_SNIFFER_EGG_PLOP, 1, 1);
                            return;
                        }
                    }

                    currentInventoryIndex[0]++;
                    slot[0] = 0;

                    if (currentInventoryIndex[0] < invs.size()) {
                        targetInv[0].clear();
                        for (int i = 5 * 9; i < 6 * 9; i++)
                            targetInv[0].setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
                    }
                    return;
                }
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.playSound(player1, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    if (player1.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Results"))
                        player1.closeInventory();
                    UUID currentUUID = resultDisplayOrder.get(currentResultIndex - 1);
                    Player currentPlayer = Bukkit.getPlayer(currentUUID);
                    int place = -1;
                    int score = doneTasksPlayers.getOrDefault(currentUUID, Collections.emptyList()).size();
                    for (Map.Entry<Integer, List<UUID>> entry : places.entrySet()) {
                        if (entry.getValue().contains(currentUUID)) {
                            place = entry.getKey();
                            break;
                        }
                    }
                    String formattedPlace;
                    if(place == 1)
                        formattedPlace = ChatColor.GOLD + "" + place ;
                    else if(place == 2)
                        formattedPlace = ChatColor.GRAY + "" + place ;
                    else if(place == 3)
                        formattedPlace = ChatColor.of("#ce8946") + "" + place ;
                    else
                        formattedPlace = ChatColor.WHITE + "" + place ;
                    if(currentPlayer != null)
                        player1.sendTitle(formattedPlace + ChatColor.WHITE + ". " + currentPlayer.getName(), ChatColor.GOLD + "Completed " + score + " tasks", 10, 100, 20);
                    else
                        player1.sendTitle(formattedPlace + ChatColor.WHITE + ". " + "Offline Player", ChatColor.GOLD + "Completed " + score + " tasks", 10, 100, 20);
                }
                task1.cancel();
                task1 = null;
            }
        };
        task1.runTaskTimer(plugin, 0L, 15L);
    }

    public static void openNextPlayerResult() {
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
        if (resultDisplayOrder.isEmpty()) return;
        boolean finished = false;
        if (currentResultIndex >= resultDisplayOrder.size()) {
            finished = true;
            currentResultIndex = 0; // Reset für nächsten Aufruf
        }
        if (!finished) {
            UUID nextUUID = resultDisplayOrder.get(currentResultIndex);
            Player currentPlayer = Bukkit.getPlayer(nextUUID);
            if (currentPlayer != null) {
                List<Inventory> invs = results.get(nextUUID);
                showResultsStepByStep(SurvivalChallengesPlugin.getInstance(), currentPlayer, invs);
            }

            currentResultIndex++;
        }

        if (finished) {
            forceBattles.setForceBattlesResults(false);
            for(Player player1 : Bukkit.getOnlinePlayers())
                player1.sendMessage(org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] " + ChatColor.RED + "All results have been showed");
        }
    }

    /*
    if (invs != null && !invs.isEmpty()) {
        currentPage.put(nextUUID, 0); // immer auf erste Seite
        player.openInventory(invs.get(0));
    }
    */
    private static void calcPlayerPlaces(){
        results.clear();
        for(Map.Entry<UUID, List<TaskResult>> entry : doneTasksPlayers.entrySet()){
            UUID uuid = entry.getKey();
            List<TaskResult> tasks = entry.getValue();
            String playerName = "Offline Player";
            Player player = Bukkit.getPlayer(uuid);
            if(player != null)
                playerName = player.getName();
            Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.GOLD + "Results - " + playerName);
            for (int i = 5*9; i < 6*9; i++)
                inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
            int slot = 0;
            for(TaskResult task : tasks){
                if (slot >= 5*9) {
                    List<Inventory> invs = results.computeIfAbsent(uuid, k -> new ArrayList<>());
                    inv.setItem(9*6-1, createGuiItem(Material.ARROW, ChatColor.GREEN + "Next Page", ChatColor.GRAY + "Takes you to the next page"));
                    invs.add(inv);
                    slot = 0;

                    inv = Bukkit.createInventory(null, 9*6, ChatColor.GOLD + "Results - " + playerName);
                    for (int i = 5*9; i < 6*9; i++)
                        inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " "));
                    inv.setItem(5*9, createGuiItem(Material.ARROW, ChatColor.GREEN + "Previous Page", ChatColor.GRAY + "Takes you to the previous page"));
                }
                inv.setItem(slot, createGuiResultItem(task.getMaterial(), ChatColor.GOLD + task.getTime(), task.isJoker() ? ChatColor.RED + "[Joker]" : ""));
                slot++;
            }
            List<Inventory> invs = results.computeIfAbsent(uuid, k -> new ArrayList<>());
            invs.add(inv);
        }

        // Places
        Map<UUID, Integer> scores = new HashMap<>();
        for (Map.Entry<UUID, List<TaskResult>> entry : doneTasksPlayers.entrySet())
            scores.put(entry.getKey(), entry.getValue().size());

        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        places.clear();
        int place = 1;
        int lastScore = -1;
        for (Map.Entry<UUID, Integer> entry : sorted) {
            int score = entry.getValue();
            if (lastScore != -1 && score < lastScore) {
                place++;
            }
            places.computeIfAbsent(place, k -> new ArrayList<>()).add(entry.getKey());
            lastScore = score;
        }

        resultDisplayOrder.clear();
        List<Integer> sortedPlaceNumbers = new ArrayList<>(places.keySet());
        sortedPlaceNumbers.sort(Collections.reverseOrder()); // schlechtester Platz zuerst
        for (int p : sortedPlaceNumbers) {
            List<UUID> playersAtPlace = places.get(p);
            if (playersAtPlace != null) {
                resultDisplayOrder.addAll(playersAtPlace); // alle Spieler dieses Platzes nacheinander
            }
        }
        currentResultIndex = 0;
    }

    private static Material getRandomItem(){
        return itemPool.get(random.nextInt(itemPool.size()));
    }

    private static ItemDisplay createNewDisplay(Player player, Material material){
        ItemDisplay display = (ItemDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.ITEM_DISPLAY);
        display.setItemStack(new ItemStack(material));
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

    public static void stop(){
        for(Map.Entry<UUID, ItemDisplay> map : displayPlayers.entrySet()){
            ItemDisplay itemDisplay = map.getValue();
            if (!itemDisplay.isDead()){
                itemDisplay.remove();
            }
        }
        displayPlayers.clear();
    }

    private static String formattedString(String string){
        return Arrays.stream(string.split("_")).map(word -> word.charAt(0) + word.substring(1).toLowerCase()).reduce((a, b) -> a + " " + b).orElse(string);
    }

    public static String getTaskName(Player player){
        return formattedString(String.valueOf(tasksPlayers.get(player.getUniqueId())));
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
            String name = Arrays.stream(material.name().split("_")).map(word -> word.charAt(0) + word.substring(1).toLowerCase()).reduce((a, b) -> a + " " + b).orElse(material.name());
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
