package de.SurvivalChallengesPlugin.general.settings.utils;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Settings {

    private boolean settingLimitedPlayer;
    private boolean settingSplitHearts;
    private boolean settingDamageLogger;
    private boolean settingDeathScreen;
    private boolean settingTimerPause;
    private boolean settingDamageInvClear;
    private int settingHardcore;
    private int settingBackpack;
    private int settingRegeneration;
    private int settingBossRequired;

    public Settings(boolean settingLimitedPlayer, boolean settingSplitHearts, int settingBackpack, boolean settingDamageLogger, boolean settingDeathScreen, boolean settingTimerPause, boolean settingDamageInvClear, int settingHardcore, int settingRegeneration, int settingBossRequired) {
        this.settingLimitedPlayer = settingLimitedPlayer;
        this.settingSplitHearts = settingSplitHearts;
        this.settingDamageLogger = settingDamageLogger;
        this.settingHardcore = settingHardcore;
        this.settingBackpack = settingBackpack;
        this.settingRegeneration = settingRegeneration;
        this.settingDeathScreen = settingDeathScreen;
        this.settingTimerPause = settingTimerPause;
        this.settingBossRequired = settingBossRequired;
        this.settingDamageInvClear = settingDamageInvClear;
    }

    //Getter

    public boolean isSettingLimitedPlayer() {
        return settingLimitedPlayer;
    }

    public boolean isSettingSplitHearts() {
        return settingSplitHearts;
    }

    public int getSettingBackpack() { return settingBackpack; }

    public boolean isSettingDamageLogger() {
        return settingDamageLogger;
    }

    public int getSettingHardcore() { return settingHardcore; }

    public int getSettingRegeneration() { return settingRegeneration; }

    public boolean isSettingDeathScreen() {
        return settingDeathScreen;
    }

    public boolean isSettingTimerPause() {
        return settingTimerPause;
    }

    public int getSettingBossRequired() {
        return settingBossRequired;
    }

    public boolean isSettingDamageInvClear() {
        return settingDamageInvClear;
    }

//Setter

    public void setSettingLimitedPlayer(boolean settingLimitedPlayer) { this.settingLimitedPlayer = settingLimitedPlayer;}

    public void setSettingSplitHearts(boolean settingSameHealth) {
        this.settingSplitHearts = settingSameHealth;
    }

    public void setSettingBackpack(int settingBackpack) {
        this.settingBackpack = settingBackpack;
    }

    public void setSettingDamageLogger(boolean settingDamageLogger) {
        this.settingDamageLogger = settingDamageLogger;
    }

    public void setSettingHardcore(int settingHardcore) {
        this.settingHardcore = settingHardcore;
    }

    public void setSettingRegeneration(int settingRegeneration) { this.settingRegeneration = settingRegeneration; }

    public void setSettingDeathScreen(boolean settingDeathScreen) {
        this.settingDeathScreen = settingDeathScreen;
    }

    public void setSettingTimerPause(boolean settingTimerPause) {
        this.settingTimerPause = settingTimerPause;
    }

    public void setSettingBossRequired(int settingBossRequired) {
        this.settingBossRequired = settingBossRequired;
    }

    public void setSettingDamageInvClear(boolean settingDamageInvClear) {
        this.settingDamageInvClear = settingDamageInvClear;
    }

    public void resetDefault() {
        settingLimitedPlayer = true;
        settingSplitHearts = false;
        settingBackpack = 0;
        settingDamageLogger = false;
        settingDeathScreen = false;
        settingTimerPause = true;
        settingDamageInvClear = false;
        settingHardcore = 2;
        settingRegeneration = 1;
        settingBossRequired = 1;
        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.NORMAL);
            world.setGameRule(GameRule.PVP, true);
            world.setGameRule(GameRule.DO_FIRE_TICK, true);
            world.setGameRule(GameRule.KEEP_INVENTORY, false);
        }
    }
}
