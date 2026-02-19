package de.SurvivalChallengesPlugin.datamanager;

import de.SurvivalChallengesPlugin.general.settings.utils.Settings;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SettingsManager {
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration configuration;

    public SettingsManager(JavaPlugin plugin){
        this.plugin = plugin;
        createFile();
    }

    private void createFile(){
        file = new File(plugin.getDataFolder(), "dataSettings.yml");
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

    public void save(Settings settings){
        configuration.set("limitedPlayer", settings.isSettingLimitedPlayer());
        configuration.set("backpack", settings.getSettingBackpack());
        configuration.set("splitHearts", settings.isSettingSplitHearts());
        configuration.set("damageLogger", settings.isSettingDamageLogger());
        configuration.set("hardcore", settings.getSettingHardcore());
        configuration.set("regeneration", settings.getSettingRegeneration());
        configuration.set("deathScreen", settings.isSettingDeathScreen());
        configuration.set("timerPause", settings.isSettingTimerPause());
        configuration.set("bossRequired", settings.getSettingBossRequired());
        configuration.set("damageInvClear", settings.isSettingDamageInvClear());

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings load(){
        return new Settings(
            configuration.getBoolean("limitedPlayer"),
            configuration.getBoolean("splitHearts"),
            configuration.getInt("backpack"),
            configuration.getBoolean("damageLogger"),
            configuration.getBoolean("deathScreen"),
            configuration.getBoolean("timerPause"),
            configuration.getBoolean("damageInvClear"),
            configuration.getInt("hardcore"),
            configuration.getInt("regeneration"),
            configuration.getInt("bossRequired")
        );
    }
}
