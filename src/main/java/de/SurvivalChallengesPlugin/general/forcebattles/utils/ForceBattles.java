package de.SurvivalChallengesPlugin.general.forcebattles.utils;

public class ForceBattles {

    private boolean forceBattlesEnabled;
    private boolean forceBattlesTeams;
    private boolean forceBattlesEasierMode;
    private boolean forceBattlesCustomItems;
    private boolean forceBattlesTeamSwitch;
    private boolean forceBattlesTimerBackward;
    private boolean forceBattlesResults;
    private boolean forceBattlesItems;
    private boolean forceBattlesMobs;
    private boolean forceBattlesAdvancements;

    public ForceBattles(boolean forceBattlesEnabled, boolean  forceBattlesTeams, boolean  forceBattlesEasierMode, boolean  forceBattlesCustomItems, boolean  forceBattlesTeamSwitch, boolean  forceBattlesTimerBackward, boolean  forceBattlesResults, boolean  forceBattlesItems, boolean  forceBattlesMobs, boolean  forceBattlesAdvancements) {
        this.forceBattlesEnabled = forceBattlesEnabled;
        this.forceBattlesTeams = forceBattlesTeams;
        this.forceBattlesEasierMode = forceBattlesEasierMode;
        this.forceBattlesCustomItems = forceBattlesCustomItems;
        this.forceBattlesTeamSwitch = forceBattlesTeamSwitch;
        this.forceBattlesTimerBackward = forceBattlesTimerBackward;
        this.forceBattlesResults = forceBattlesResults;
        this.forceBattlesItems = forceBattlesItems;
        this.forceBattlesMobs = forceBattlesMobs;
        this.forceBattlesAdvancements = forceBattlesAdvancements;
    }

    //Getter

    public boolean isForceBattlesEnabled() {
        return forceBattlesEnabled;
    }

    public boolean isForceBattlesTeams() {
        return forceBattlesTeams;
    }

    public boolean isForceBattlesEasierMode() {
        return forceBattlesEasierMode;
    }

    public boolean isForceBattlesCustomItems() {
        return forceBattlesCustomItems;
    }

    public boolean isForceBattlesTeamSwitch() {
        return forceBattlesTeamSwitch;
    }

    public boolean isForceBattlesTimerBackward() {
        return forceBattlesTimerBackward;
    }

    public boolean isForceBattlesResults() {
        return forceBattlesResults;
    }

    public boolean isForceBattlesItems() {
        return forceBattlesItems;
    }

    public boolean isForceBattlesMobs() {
        return forceBattlesMobs;
    }

    public boolean isForceBattlesAdvancements() {
        return forceBattlesAdvancements;
    }


//Setter

    public void setForceBattlesEnabled(boolean forceBattlesEnabled) {
        this.forceBattlesEnabled = forceBattlesEnabled;
    }

    public void setForceBattlesTeams(boolean forceBattlesTeams) {
        this.forceBattlesTeams = forceBattlesTeams;
    }

    public void setForceBattlesEasierMode(boolean forceBattlesEasierMode) {
        this.forceBattlesEasierMode = forceBattlesEasierMode;
    }

    public void setForceBattlesCustomItems(boolean forceBattlesCustomItems) {
        this.forceBattlesCustomItems = forceBattlesCustomItems;
    }

    public void setForceBattlesTeamSwitch(boolean forceBattlesTeamSwitch) {
        this.forceBattlesTeamSwitch = forceBattlesTeamSwitch;
    }

    public void setForceBattlesTimerBackward(boolean forceBattlesTimerBackward) {
        this.forceBattlesTimerBackward = forceBattlesTimerBackward;
    }

    public void setForceBattlesResults(boolean forceBattlesResults) {
        this.forceBattlesResults = forceBattlesResults;
    }

    public void setForceBattlesItems(boolean forceBattlesItems) {
        this.forceBattlesItems = forceBattlesItems;
    }

    public void setForceBattlesMobs(boolean forceBattlesMobs) {
        this.forceBattlesMobs = forceBattlesMobs;
    }

    public void setForceBattlesAdvancements(boolean forceBattlesAdvancements) {
        this.forceBattlesAdvancements = forceBattlesAdvancements;
    }

    public void resetDefault() {
        forceBattlesEnabled = false;
        forceBattlesTeams = false;
        forceBattlesEasierMode = true;
        forceBattlesCustomItems = false;
        forceBattlesTeamSwitch = true;
        forceBattlesTimerBackward = false;
        forceBattlesResults = false;
        forceBattlesItems = true;
        forceBattlesMobs = false;
        forceBattlesAdvancements = false;
    }
}
