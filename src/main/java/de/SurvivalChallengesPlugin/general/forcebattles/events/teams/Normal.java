package de.SurvivalChallengesPlugin.general.forcebattles.events.teams;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.forcebattles.utils.TaskResult;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class Normal implements Listener {
    private static BukkitRunnable task;
    private static BukkitRunnable task1;
    private static final Random random = new Random();
    public static final Map<Team, Material> tasksTeams = new HashMap<>();
    private static final Map<UUID, ItemDisplay> displayPlayers = new HashMap<>();
    public static final Map<Team, List<TaskResult>> doneTasksTeams = new HashMap<>();
    private static final Map<Team, List<Inventory>> results = new HashMap<>();
    private static final List<Material> itemPool = new ArrayList<>();
    private static final Map<Integer, List<Team>> places = new LinkedHashMap<>();
    private static final Map<UUID, Integer> currentPage = new HashMap<>();
    private static final List<UUID> resultDisplayOrder = new ArrayList<>();
    private static int currentResultIndex = 0;



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
                if(!forceBattles.isForceBattlesEnabled() || !forceBattles.isForceBattlesTeams() || forceBattles.isForceBattlesCustomItems()) {
                    task.cancel();
                    task = null;
                    stop();
                    return;
                }
                else {
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    if (manager != null) {
                        Scoreboard scoreboard = manager.getNewScoreboard();
                        for(org.bukkit.scoreboard.Team team : scoreboard.getTeams()){
                            Material taskMaterial = tasksTeams.get(team);
                            if(taskMaterial == null && timer.isRunning()){
                                taskMaterial = getRandomItem();
                                tasksTeams.put(team, taskMaterial);
                                for (String playerName : team.getEntries()){
                                    Player player = Bukkit.getPlayer(playerName);
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
                                            if (display != null && !display.isDead())
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
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 3);
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

    private static String formattedString(String string){
        return Arrays.stream(string.split("_")).map(word -> word.charAt(0) + word.substring(1).toLowerCase()).reduce((a, b) -> a + " " + b).orElse(string);
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

}
