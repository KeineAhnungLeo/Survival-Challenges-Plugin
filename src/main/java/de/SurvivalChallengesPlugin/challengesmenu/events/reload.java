package de.SurvivalChallengesPlugin.challengesmenu.events;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class reload implements Listener {

    public Plugin plugin;

    public reload(){
        plugin = de.SurvivalChallengesPlugin.SurvivalChallengesPlugin.getInstance();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        if(event.getMessage().toLowerCase().startsWith("/reload")){
            event.setCancelled(true);

            String[] message = event.getMessage().split(" ");
            int seconds = -1;
            if(message.length>1){
                try {
                    seconds = Integer.parseInt(message[1]);
                } catch (NumberFormatException ignored){

                }
            }

            if(seconds<0){
                seconds=5;
            }

            for(Player player1:Bukkit.getOnlinePlayers()){
                player1.sendMessage("§c§lDer Server wird in "+seconds+" Sekunden neugeladen.");
            }
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    for(Player player1:Bukkit.getOnlinePlayers()){
                        player1.sendMessage("§c§lDer Server wird neugeladen...");
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().reload();
                            for(Player player1:Bukkit.getOnlinePlayers()){
                                player1.sendMessage("§a§lFertig");
                            }
                        }
                    }, 5);

                }
            }, 20L *seconds);
        }
    }
}
