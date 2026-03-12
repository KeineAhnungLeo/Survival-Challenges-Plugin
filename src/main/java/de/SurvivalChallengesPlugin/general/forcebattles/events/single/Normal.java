package de.SurvivalChallengesPlugin.general.forcebattles.events.single;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.*;

public class Normal implements Listener {
    private static BukkitRunnable task;
    private static BukkitRunnable task1;
    private static final Random random = new Random();
    private static final Map<UUID, Material> tasksPlayers = new HashMap<>();
    private static final Map<UUID, ItemDisplay> displayPlayers = new HashMap<>();
    private static final Map<UUID, Map<Material, Map<String, Boolean>>> doneTasksPlayers = new HashMap<>();
    private static final Map<UUID, Map<Integer, Inventory>> results = new HashMap<>();

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
                Map<Material, Map<String, Boolean>> innerMap = doneTasksPlayers.computeIfAbsent(uuid, k -> new HashMap<>());
                Map<String, Boolean> innerestMap = innerMap.computeIfAbsent(material, k -> new HashMap<>());
                String string = ((timer.getTimeD() >= 1) ? timer.getTimeD() + " " : "") + ((timer.getTimeH() <= 9) ? "0" + timer.getTimeH() + " " : timer.getTimeH() + " ") + ((timer.getTimeM() <= 9) ? "0" + timer.getTimeM() + " " : timer.getTimeM() + " ") + ((timer.getTimeS() <= 9) ? "0" + timer.getTimeS() : timer.getTimeS());
                innerestMap.put(string, true);
                innerMap.put(material, innerestMap);
                doneTasksPlayers.put(uuid, innerMap);

                Material newTask = getRandomItem();
                tasksPlayers.put(uuid, newTask);
                ItemDisplay display = displayPlayers.get(uuid);
                if(display != null)
                    display.setItemStack(new ItemStack(newTask));
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "ForceBattle" + ChatColor.GRAY + "] Next task: " + ChatColor.GOLD + formattedString(newTask.toString()));
            }
        }
    }

    private void checkItemPlayer(Player player, Material material) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
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
        }
        //Save done
        Map<Material, Map<String, Boolean>> innerMap = doneTasksPlayers.computeIfAbsent(uuid, k -> new HashMap<>());
        Map<String, Boolean> innerestMap = innerMap.computeIfAbsent(material, k -> new HashMap<>());
        String string = ((timer.getTimeD() >= 1) ? timer.getTimeD() + " " : "") + ((timer.getTimeH() <= 9) ? "0" + timer.getTimeH() + " " : timer.getTimeH() + " ") + ((timer.getTimeM() <= 9) ? "0" + timer.getTimeM() + " " : timer.getTimeM() + " ") + ((timer.getTimeS() <= 9) ? "0" + timer.getTimeS() : timer.getTimeS());
        innerestMap.put(string, false);
        innerMap.put(material, innerestMap);
        doneTasksPlayers.put(uuid, innerMap);
    }

    public static void start(JavaPlugin plugin) {
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

    public static void showResults(JavaPlugin plugin) {
        if (task1 != null) return;
        task1 = new BukkitRunnable() {
            @Override
            public void run() {
                de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                if(!forceBattles.isForceBattlesEnabled() || forceBattles.isForceBattlesTeams() || forceBattles.isForceBattlesCustomItems()) {
                    task1.cancel();
                    task1 = null;
                    return;
                }
                else {
                    //Totes zeug jetzt
                }
            }
        };
        task1.runTaskTimer(plugin, 0L, 10);
    }

    private static Material getRandomItem(){
        Material[] types = Material.values();
        Material type = Material.BARRIER;
        for (int i = 0; i < 50; i++) {
            type = types[random.nextInt(types.length)];
            if(!type.isItem()) continue;
            if(type.isAir()) continue;
            if(List.of(Material.BEDROCK).contains(type)) continue;
            if(type.name().contains("WALL_HANGING")) continue;
            if(type.name().contains("CANDLE_CAKE")) continue;
        }
        return type;
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

    private static ItemStack createGuiItem(Material material, String name, boolean enchanted, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (enchanted)
                meta.setEnchantmentGlintOverride(true);
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
