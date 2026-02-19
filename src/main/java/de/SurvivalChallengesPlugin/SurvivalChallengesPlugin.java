package de.SurvivalChallengesPlugin;

import de.SurvivalChallengesPlugin.challengesmenu.events.reload;
import de.SurvivalChallengesPlugin.datamanager.ChallengesManager;
import de.SurvivalChallengesPlugin.datamanager.SettingsManager;
import de.SurvivalChallengesPlugin.datamanager.TimerManager;
import de.SurvivalChallengesPlugin.general.challenges.events.*;
import de.SurvivalChallengesPlugin.general.challenges.events.OnlyOneBlockUse;
import de.SurvivalChallengesPlugin.general.invsee.commands.Invsee;
import de.SurvivalChallengesPlugin.general.position.commands.Position;
import de.SurvivalChallengesPlugin.timer.commands.Timer;
import de.SurvivalChallengesPlugin.challengesmenu.commands.Challengemenu;
import de.SurvivalChallengesPlugin.challengesmenu.events.invClick;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurvivalChallengesPlugin extends JavaPlugin {

    private static SurvivalChallengesPlugin instance;

    private de.SurvivalChallengesPlugin.timer.utils.Timer timer;

    private de.SurvivalChallengesPlugin.general.settings.utils.Settings settings;

    private de.SurvivalChallengesPlugin.general.challenges.utils.Challenges challenges;

    private SettingsManager settingsManager;

    private TimerManager timerManager;

    private ChallengesManager challengesManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable(){
        getLogger().info("Loading SurvivalChallengesPlugin...");
        instance=this;
        //Commands
        getCommand("challengemenu").setExecutor(new Challengemenu());
        getCommand("timer").setExecutor(new Timer());
        getCommand("position").setExecutor(new Position());
        getCommand("invsee").setExecutor(new Invsee());
        //Tab Completer
        getCommand("timer").setTabCompleter(new Timer());
        getCommand("position").setTabCompleter(new Position());
        //Events
        Bukkit.getPluginManager().registerEvents(new invClick(),this);
        Bukkit.getPluginManager().registerEvents(new reload(),this);
        Bukkit.getPluginManager().registerEvents(new de.SurvivalChallengesPlugin.general.settings.events.Settings(this),this);
        Bukkit.getPluginManager().registerEvents(new DelayedDamage(), this);
        Bukkit.getPluginManager().registerEvents(new DamageJump(), this);
        Bukkit.getPluginManager().registerEvents(new MobJump(), this);
        Bukkit.getPluginManager().registerEvents(new MobSwap(), this);
        Bukkit.getPluginManager().registerEvents(new MobDuplicator(), this);
        Bukkit.getPluginManager().registerEvents(new DamageRandomEffect(), this);
        Bukkit.getPluginManager().registerEvents(new IceFloor(), this);
        Bukkit.getPluginManager().registerEvents(new DamageRandomTeleport(), this);
        Bukkit.getPluginManager().registerEvents(new ItemPickupDamage(), this);
        Bukkit.getPluginManager().registerEvents(new OnlyOneBlockUse(), this);
        Bukkit.getPluginManager().registerEvents(new GravitySwitch(), this);
        Bukkit.getPluginManager().registerEvents(new JumpStrength(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkRandomBlock(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkSynchronisation(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkRandomMob(), this);
        Bukkit.getPluginManager().registerEvents(new de.SurvivalChallengesPlugin.general.invsee.events.invClick(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkDisappear(), this);
        Bukkit.getPluginManager().registerEvents(new TrafficLight(), this);
        //Ini Timer
        timerManager = new TimerManager(this);
        timer = timerManager.load();
        //Ini Settings
        settingsManager = new SettingsManager(this);
        settings = settingsManager.load();
        //Ini Challenges
        challengesManager = new ChallengesManager(this);
        challenges = challengesManager.load();
        //Ini ResetToDefault
        de.SurvivalChallengesPlugin.general.ResetToDefault.run(SurvivalChallengesPlugin.getInstance());
        getLogger().info("Successfully loaded SurvivalChallengesPlugin");
        //Enable Schedulers
        de.SurvivalChallengesPlugin.general.challenges.utils.Challenges.enableChallengeSchedulers();
    }

    @Override
    public void onDisable(){
        de.SurvivalChallengesPlugin.general.challenges.events.GravitySwitch.stop();
        de.SurvivalChallengesPlugin.general.challenges.events.ChunkDisappear.stop();
        de.SurvivalChallengesPlugin.general.challenges.events.TrafficLight.stop();
        settingsManager.save(settings);
        timerManager.save(timer);
        challengesManager.save(challenges);
    }

    public static SurvivalChallengesPlugin getInstance(){
        return instance;
    }

    public de.SurvivalChallengesPlugin.timer.utils.Timer getTimer() {
        return timer;
    }

    public de.SurvivalChallengesPlugin.general.settings.utils.Settings getSettings() {
        return settings;
    }

    public de.SurvivalChallengesPlugin.general.challenges.utils.Challenges getChallenges() {return challenges;}
}