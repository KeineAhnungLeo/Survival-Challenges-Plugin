package de.SurvivalChallengesPlugin.timer.utils;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private boolean running;
    private int timeS;
    private int timeM;
    private int timeH;
    private int timeD;
    private boolean Invisible;
    private ChatColor color;

    public Timer(boolean running, int timeS, int timeM, int timeH, int timeD, boolean Invisible, ChatColor color) {
        this.running = running;
        this.timeS = timeS;
        this.timeM = timeM;
        this.timeH = timeH;
        this.timeD = timeD;
        this.Invisible = Invisible;
        this.color = color;
        run();
    }

    public boolean isRunning() {
        return running;
    }

    public int getTimeS() {
        return timeS;
    }

    public int getTimeM() {
        return timeM;
    }

    public int getTimeH() {
        return timeH;
    }

    public int getTimeD() {
        return timeD;
    }

    public boolean isInvisible() {
        return Invisible;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setTimeS(int timeS) {
        this.timeS = timeS;
    }

    public void setTimeM(int timeM) {
        this.timeM = timeM;
    }

    public void setTimeH(int timeH) {
        this.timeH = timeH;
    }

    public void setTimeD(int timeD) {
        this.timeD = timeD;
    }

    public void setInvisible(boolean invisible) {
        this.Invisible = invisible;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void sendActionBar(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isRunning()) {
                if(!isInvisible())
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Timer paused"));
                continue;
            }
            if (getTimeS() >= 60) {
                timeS = 0;
                timeM = timeM + 1;
            }
            if (getTimeM() >= 60) {
                timeM = 0;
                timeH = timeH + 1;
            }
            if (getTimeH() >= 24) {
                timeH = 0;
                timeD = timeD + 1;
            }
            if (!isInvisible()) {
                if (getTimeM() < 1 && getTimeH() < 1 && getTimeD() < 1)
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getColor().toString() + ChatColor.BOLD + getTimeS() + "s"));
                else if (getTimeH() < 1 && getTimeD() < 1)
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getColor().toString() + ChatColor.BOLD + getTimeM() + "m " + getTimeS() + "s"));
                else if (getTimeD() < 1)
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getColor().toString() + ChatColor.BOLD + getTimeH() + "h " + getTimeM() + "m " + getTimeS() + "s"));
                else
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getColor().toString() + ChatColor.BOLD + getTimeD() + "d " + getTimeH() + "h " + getTimeM() + "m " + getTimeS() + "s"));
            }
        }
    }

    private void run(){
        new BukkitRunnable(){
            @Override
            public void run(){
                sendActionBar();
                if(!isRunning()){
                    for (Player player : Bukkit.getOnlinePlayers()){
                        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0,1,0), 5, 0.1, 0, 0.1, 0.9);
                    }
                    return;
                }
                setTimeS(getTimeS()+1);
            }
        }.runTaskTimer(SurvivalChallengesPlugin.getInstance(), 0, 20);
    }
}
