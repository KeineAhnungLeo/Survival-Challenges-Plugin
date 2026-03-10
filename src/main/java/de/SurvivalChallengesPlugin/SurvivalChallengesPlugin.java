package de.SurvivalChallengesPlugin;

import de.SurvivalChallengesPlugin.challengesmenu.events.reload;
import de.SurvivalChallengesPlugin.datamanager.*;
import de.SurvivalChallengesPlugin.general.backpack.commands.Backpack;
import de.SurvivalChallengesPlugin.general.challenges.events.*;
import de.SurvivalChallengesPlugin.general.challenges.events.OnlyOneBlockUse;
import de.SurvivalChallengesPlugin.general.invsee.commands.Invsee;
import de.SurvivalChallengesPlugin.general.joker.commands.Joker;
import de.SurvivalChallengesPlugin.general.position.commands.Position;
import de.SurvivalChallengesPlugin.general.reset.commands.Reset;
import de.SurvivalChallengesPlugin.timer.commands.Timer;
import de.SurvivalChallengesPlugin.challengesmenu.commands.Challengemenu;
import de.SurvivalChallengesPlugin.challengesmenu.events.invClick;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SurvivalChallengesPlugin extends JavaPlugin {

    private static SurvivalChallengesPlugin instance;

    private de.SurvivalChallengesPlugin.timer.utils.Timer timer;

    private de.SurvivalChallengesPlugin.general.settings.utils.Settings settings;

    private de.SurvivalChallengesPlugin.general.challenges.utils.Challenges challenges;

    private de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles forceBattles;

    private SettingsManager settingsManager;

    private TimerManager timerManager;

    private ChallengesManager challengesManager;

    private Backpack backpackCommand;

    private ForceBattlesManager forceBattlesManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable(){
        getLogger().info("Loading SurvivalChallengesPlugin...");
        instance=this;
        //Ini Timer
        timerManager = new TimerManager(this);
        timer = timerManager.load();
        //Ini Settings
        settingsManager = new SettingsManager(this);
        settings = settingsManager.load();
        //Ini Challenges
        challengesManager = new ChallengesManager(this);
        challenges = challengesManager.load();
        //Ini ForceBattles
        forceBattlesManager = new ForceBattlesManager(this);
        forceBattles = forceBattlesManager.load();
        //Ini Backpack
        BackpackManager backpackManager = new BackpackManager(this);
        //Commands
        Objects.requireNonNull(getCommand("challengemenu")).setExecutor(new Challengemenu());
        Objects.requireNonNull(getCommand("timer")).setExecutor(new Timer());
        Objects.requireNonNull(getCommand("position")).setExecutor(new Position());
        Objects.requireNonNull(getCommand("invsee")).setExecutor(new Invsee());
        Objects.requireNonNull(getCommand("reset")).setExecutor(new Reset());
        Objects.requireNonNull(getCommand("joker")).setExecutor(new Joker());
        backpackCommand = new Backpack(backpackManager);
        Objects.requireNonNull(getCommand("backpack")).setExecutor(backpackCommand);
        //Tab Completer
        Objects.requireNonNull(getCommand("timer")).setTabCompleter(new Timer());
        Objects.requireNonNull(getCommand("position")).setTabCompleter(new Position());
        Objects.requireNonNull(getCommand("reset")).setTabCompleter(new Reset());
        Objects.requireNonNull(getCommand("joker")).setTabCompleter(new Joker());
        //Events
        Bukkit.getPluginManager().registerEvents(new invClick(),this);
        Bukkit.getPluginManager().registerEvents(new de.SurvivalChallengesPlugin.general.settings.events.Settings(this),this);
        Bukkit.getPluginManager().registerEvents(new DelayedDamage(), this);
        Bukkit.getPluginManager().registerEvents(new DamageJump(), this);
        Bukkit.getPluginManager().registerEvents(new MobJump(), this);
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
        Bukkit.getPluginManager().registerEvents(new LavaFloor(), this);
        Bukkit.getPluginManager().registerEvents(new BedrockWall(), this);
        Bukkit.getPluginManager().registerEvents(new de.SurvivalChallengesPlugin.general.backpack.events.Backpack(backpackManager), this);
        Bukkit.getPluginManager().registerEvents(new reload(), this);
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
        de.SurvivalChallengesPlugin.general.challenges.events.BedrockWall.stop();
        settingsManager.save(settings);
        timerManager.save(timer);
        challengesManager.save(challenges);
        backpackCommand.saveAll();
        forceBattlesManager.save(forceBattles);
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

    public de.SurvivalChallengesPlugin.general.forcebattles.utils.ForceBattles getForceBattles() {return forceBattles;}

    public Backpack getBackpackCommand() {return backpackCommand;}
}