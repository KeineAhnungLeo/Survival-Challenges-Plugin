package de.SurvivalChallengesPlugin.datamanager;

import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ChallengesManager {
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration configuration;

    public ChallengesManager(JavaPlugin plugin){
        this.plugin = plugin;
        createFile();
    }

    private void createFile(){
        file = new File(plugin.getDataFolder(), "dataChallenges.yml");
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

    public void save(Challenges challenges){
        configuration.set("activeChallenges", challenges.getActiveChallenges().stream().map(Enum::name).toList());
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Challenges load(){
        Challenges challenges = new Challenges();
        for (String name : configuration.getStringList("activeChallenges")){
            try{
                Challenges.Challenge challenge = Challenges.Challenge.valueOf(name);
                challenges.addChallenge(challenge);
            } catch (IllegalArgumentException ignored){
            }
        }
        return challenges;
    }
}
