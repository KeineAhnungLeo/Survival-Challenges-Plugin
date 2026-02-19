package de.SurvivalChallengesPlugin.datamanager;

import de.SurvivalChallengesPlugin.timer.utils.Timer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class TimerManager {
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration configuration;

    public TimerManager(JavaPlugin plugin){
        this.plugin = plugin;
        createFile();
    }

    private void createFile(){
        file = new File(plugin.getDataFolder(), "dataTimer.yml");
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

    public void save(Timer timer){
        configuration.set("timerS", timer.getTimeS());
        configuration.set("timerM", timer.getTimeM());
        configuration.set("timerH", timer.getTimeH());
        configuration.set("timerD", timer.getTimeD());
        configuration.set("timerColor", timer.getColor().name());
        configuration.set("timerVisible", timer.isInvisible());
        configuration.set("timerRunning", timer.isRunning());

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Timer load(){
        String colorName = configuration.getString("timerColor");

        ChatColor color;
        try {
            color = ChatColor.valueOf(colorName);
        } catch (Exception e) {
            color = ChatColor.GOLD;
        }
        return new Timer(
            configuration.getBoolean("timerRunning"),
            configuration.getInt("timerS"),
            configuration.getInt("timerM"),
            configuration.getInt("timerH"),
            configuration.getInt("timerD"),
            configuration.getBoolean("timerInvisible"),
            color
        );

    }


}
