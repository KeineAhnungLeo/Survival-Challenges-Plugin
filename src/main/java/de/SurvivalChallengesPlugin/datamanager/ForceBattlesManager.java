package de.SurvivalChallengesPlugin.datamanager;

import de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems;
import de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal;
import de.SurvivalChallengesPlugin.general.forcebattles.utils.TaskResult;
import de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ForceBattlesManager {
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration configuration;

    public ForceBattlesManager(JavaPlugin plugin){
        this.plugin = plugin;
        createFile();
    }

    private void createFile(){
        file = new File(plugin.getDataFolder(), "dataForceBattles.yml");
        if(!file.exists()){
            plugin.getDataFolder().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void saveSettings(ForceBattles forceBattles){
        configuration.set("enabled", forceBattles.isForceBattlesEnabled());
        configuration.set("teams", forceBattles.isForceBattlesTeams());
        configuration.set("easierMode", forceBattles.isForceBattlesEasierMode());
        configuration.set("customItems", forceBattles.isForceBattlesCustomItems());
        configuration.set("teamSwitch", forceBattles.isForceBattlesTeamSwitch());
        configuration.set("timerBackward", forceBattles.isForceBattlesTimerBackward());
        configuration.set("results", forceBattles.isForceBattlesResults());
        configuration.set("items", forceBattles.isForceBattlesItems());
        configuration.set("mobs", forceBattles.isForceBattlesMobs());
        configuration.set("advancements", forceBattles.isForceBattlesAdvancements());

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomItemOrder(Integer integer, Inventory inventory){
        configuration.set("customOrderInv." + integer, inventory.getContents());
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveNormalTasks(){
        Map<String, String> map = new HashMap<>();
        for(Map.Entry<UUID, Material> entry : Normal.tasksPlayers.entrySet()){
            map.put(entry.getKey().toString(), entry.getValue().name());
        }
        configuration.set("normalTasks",map);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveNormalDoneTasks() {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        for (Map.Entry<UUID, List<TaskResult>> entry : Normal.doneTasksPlayers.entrySet()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (TaskResult result : entry.getValue()) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("material", result.getMaterial().name());
                resultMap.put("time", result.getTime());
                resultMap.put("joker", result.isJoker());
                list.add(resultMap);
            }
            map.put(entry.getKey().toString(), list);
        }
        configuration.set("normalDoneTasks", map);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomTasks(){
        Map<String, Integer> map = new HashMap<>();
        for(Map.Entry<UUID, Integer> entry : CustomItems.taskIdPlayers.entrySet()){
            map.put(entry.getKey().toString(), entry.getValue());
        }
        configuration.set("customTasks",map);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomDoneTasks() {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        for (Map.Entry<UUID, List<TaskResult>> entry : CustomItems.doneTasksPlayers.entrySet()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (TaskResult result : entry.getValue()) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("material", result.getMaterial().name());
                resultMap.put("time", result.getTime());
                resultMap.put("joker", result.isJoker());
                list.add(resultMap);
            }
            map.put(entry.getKey().toString(), list);
        }
        configuration.set("customDoneTasks", map);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCustomDoneTasks() {
        Map<UUID, List<TaskResult>> map = new HashMap<>();
        ConfigurationSection section = configuration.getConfigurationSection("customDoneTasks");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            List<Map<?, ?>> rawList = section.getMapList(key);
            List<TaskResult> resultList = new ArrayList<>();
            for (Map<?, ?> raw : rawList) {
                Material material = Material.valueOf((String) raw.get("material"));
                String time = (String) raw.get("time");
                boolean joker = (boolean) raw.get("joker");

                resultList.add(new TaskResult(material, time, joker));
            }
            map.put(uuid, resultList);
        }
        de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.doneTasksPlayers.clear();
        de.SurvivalChallengesPlugin.general.forcebattles.events.single.CustomItems.doneTasksPlayers.putAll(map);
    }

    public void loadCustomTasks() {
        Map<UUID, Integer> map = new HashMap<>();
        if (configuration.contains("customTasks")) {
            ConfigurationSection section = configuration.getConfigurationSection("customTasks");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    UUID uuid = UUID.fromString(key);
                    int id = section.getInt(key);
                    map.put(uuid, id);
                }
            }
        }
        CustomItems.taskIdPlayers.clear();
        CustomItems.taskIdPlayers.putAll(map);
    }

    public void loadNormalDoneTasks() {
        Map<UUID, List<TaskResult>> map = new HashMap<>();
        ConfigurationSection section = configuration.getConfigurationSection("normalDoneTasks");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            List<Map<?, ?>> rawList = section.getMapList(key);
            List<TaskResult> resultList = new ArrayList<>();
            for (Map<?, ?> raw : rawList) {
                Material material = Material.valueOf((String) raw.get("material"));
                String time = (String) raw.get("time");
                boolean joker = (boolean) raw.get("joker");

                resultList.add(new TaskResult(material, time, joker));
            }
            map.put(uuid, resultList);
        }
        de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.doneTasksPlayers.clear();
        de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.doneTasksPlayers.putAll(map);
    }

    public void loadNormalTasks() {
        Map<UUID, Material> map = new HashMap<>();
        if (configuration.contains("normalTasks")) {
            ConfigurationSection section = configuration.getConfigurationSection("normalTasks");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    UUID uuid = UUID.fromString(key);
                    Material material = Material.valueOf(section.getString(key));
                    map.put(uuid, material);
                }
            }
        }
        de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.tasksPlayers.clear();
        de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.tasksPlayers.putAll(map);
    }

    public ForceBattles loadSettings(){
        return new ForceBattles(
            configuration.getBoolean("enabled"),
            configuration.getBoolean("teams"),
            configuration.getBoolean("easierMode"),
            configuration.getBoolean("customItems"),
            configuration.getBoolean("teamSwitch"),
            configuration.getBoolean("timerBackward"),
            configuration.getBoolean("results"),
            configuration.getBoolean("items"),
            configuration.getBoolean("mobs"),
            configuration.getBoolean("advancements")
        );
    }

    public Inventory loadCustomItemOrder(Integer integer){
        org.bukkit.inventory.Inventory customOrderForceBattleInv = Bukkit.createInventory(null, 9*6, ChatColor.GOLD + "Force Battles Menu - CO - " + integer);
        if(configuration.contains("customOrderInv." + integer)){
            List<ItemStack> itemStackList = (List<ItemStack>) configuration.get("customOrderInv." + integer);
            if(itemStackList != null)
                customOrderForceBattleInv.setContents(itemStackList.toArray(new ItemStack[0]));
        }
        return customOrderForceBattleInv;
    }
}
