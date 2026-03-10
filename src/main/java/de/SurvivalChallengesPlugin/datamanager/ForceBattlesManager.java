package de.SurvivalChallengesPlugin.datamanager;

import de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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

    public void save(ForceBattles forceBattles){
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

    public ForceBattles load(){
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
}
