package de.SurvivalChallengesPlugin.general.settings.utils;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class Settings {

    private boolean settingLimitedPlayer;
    private boolean settingSameHealth;
    private boolean settingDamageLogger;
    private boolean settingDeathScreen;
    private boolean settingTimerPause;
    private int settingHardcore;
    private int settingBackpack;
    private int settingRegeneration;
    private int settingBossRequired;

    public Settings(boolean settingLimitedPlayer, boolean settingSameHealth, int settingBackpack, boolean settingDamageLogger, boolean settingDeathScreen, boolean settingTimerPause, int settingHardcore, int settingRegeneration, int settingBossRequired) {
        this.settingLimitedPlayer = settingLimitedPlayer;
        this.settingSameHealth = settingSameHealth;
        this.settingDamageLogger = settingDamageLogger;
        this.settingHardcore = settingHardcore;
        this.settingBackpack = settingBackpack;
        this.settingRegeneration = settingRegeneration;
        this.settingDeathScreen = settingDeathScreen;
        this.settingTimerPause = settingTimerPause;
        this.settingBossRequired = settingBossRequired;
    }

    //Getter

    public boolean isSettingLimitedPlayer() {
        return settingLimitedPlayer;
    }

    public boolean isSettingSameHealth() {
        return settingSameHealth;
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

    //Setter

    public void setSettingLimitedPlayer(boolean settingLimitedPlayer) { this.settingLimitedPlayer = settingLimitedPlayer;}

    public void setSettingSameHealth(boolean settingSameHealth) {
        this.settingSameHealth = settingSameHealth;
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

    public void syncAllPlayersHealth() {
        double healthToUse = -1;

        for (Player player : Bukkit.getOnlinePlayers()) {
            AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
            if (attr == null) continue;

            healthToUse = player.getHealth();
            break;
        }

        if (healthToUse < 0) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            AttributeInstance attr = p.getAttribute(Attribute.MAX_HEALTH);
            if (attr == null) continue;

            p.setHealth(Math.min(healthToUse, attr.getValue()));
        }
    }

}
