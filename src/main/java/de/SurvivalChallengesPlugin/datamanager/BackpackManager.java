package de.SurvivalChallengesPlugin.datamanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class BackpackManager {
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration configuration;

    public BackpackManager(JavaPlugin plugin){
        this.plugin = plugin;
        createFile();
    }

    private void createFile(){
        file = new File(plugin.getDataFolder(), "dataBackpacks.yml");
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

    public void savePlayer(UUID uuid, Inventory inventory){
        configuration.set("backpacks." + uuid, inventory.getContents());
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Inventory loadPlayer(UUID uuid){
        org.bukkit.inventory.Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Backpack");
        if(configuration.contains("backpacks." + uuid)){
            List<ItemStack> itemStackList = (List<ItemStack>) configuration.get("backpacks." + uuid);
            if(itemStackList != null)
                inventory.setContents(itemStackList.toArray(new ItemStack[0]));
        }
        return inventory;
    }

    public void saveGlobal(Inventory inventory){
        configuration.set("backpacks.global", inventory.getContents());
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Inventory loadGlobal(){
        org.bukkit.inventory.Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Backpack");
        if(configuration.contains("backpacks.global")){
            List<ItemStack> itemStackList = (List<ItemStack>) configuration.get("backpacks.global");
            if(itemStackList != null)
                inventory.setContents(itemStackList.toArray(new ItemStack[0]));
        }
        return inventory;
    }
}
