package de.SurvivalChallengesPlugin.timer.utils;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
            de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
            if(forceBattles.isForceBattlesTimerBackward()){
                if (timeS < 0) {
                    timeS = 59;
                    timeM--;
                    if (timeM < 0) {
                        timeM = 59;
                        timeH--;
                        if (timeH < 0) {
                            timeH = 23;
                            timeD--;
                            if (timeD < 0) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                                    player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Time's up!", "", 5, 50, 20);
                                    setTimeS(0);
                                    setTimeM(0);
                                    setTimeH(0);
                                    setTimeD(0);
                                }
                                setRunning(false);
                            }
                        }
                    }
                }
            }
            else {
                if (getTimeS() >= 60) {
                    timeS = 0;
                    timeM++;
                }
                if (getTimeM() >= 60) {
                    timeM = 0;
                    timeH++;
                }
                if (getTimeH() >= 24) {
                    timeH = 0;
                    if (timeD < Integer.MAX_VALUE)
                        timeD++;
                }
            }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isRunning()) {
                if (!isInvisible())
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Timer paused"));
                continue;
            }
            if (!isInvisible()) {
                String time = "";
                if (getTimeD() >= 1)
                    time = time + " " + getColor().toString() + ChatColor.BOLD + getTimeD() + "d";
                if (getTimeD() >= 1 || getTimeH() >= 1)
                    time = time + " " + getColor().toString() + ChatColor.BOLD + getTimeH() + "h";
                if (getTimeH() >= 1 || getTimeD() >= 1 || getTimeM() >= 1)
                    time = time + " " + getColor().toString() + ChatColor.BOLD + getTimeM() + "m";

                time = time + " " + getColor().toString() + ChatColor.BOLD + getTimeS() + "s";

                if (forceBattles.isForceBattlesEnabled()){
                    if(forceBattles.isForceBattlesTeams()){
                        if(forceBattles.isForceBattlesCustomItems())
                            //Teams, Custom
                            time = time + ChatColor.GRAY + " - " + ChatColor.GOLD + de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.getTaskName(player);
                        else
                            //Teams, NotCustom
                            time = time + ChatColor.GRAY + " - " + ChatColor.GOLD + de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.getTaskName(player);
                    }
                    else{
                        if(forceBattles.isForceBattlesCustomItems())
                            //Single, Custom
                            time = time + ChatColor.GRAY + " - " + ChatColor.GOLD + de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.getTaskName(player);
                        else
                            //Single, NotCustom
                            time = time + ChatColor.GRAY + " - " + ChatColor.GOLD + de.SurvivalChallengesPlugin.general.forcebattles.events.single.Normal.getTaskName(player);
                    }
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(time));
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
                de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles = SurvivalChallengesPlugin.getInstance().getForceBattles();
                if(forceBattles.isForceBattlesTimerBackward())
                    setTimeS(getTimeS()-1);
                else
                    setTimeS(getTimeS()+1);
            }
        }.runTaskTimer(SurvivalChallengesPlugin.getInstance(), 0, 20);
    }
}
