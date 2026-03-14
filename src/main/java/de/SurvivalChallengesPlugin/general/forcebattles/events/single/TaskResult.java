package de.SurvivalChallengesPlugin.general.forcebattles.events.single;

import org.bukkit.Material;

public class TaskResult {

    private final Material material;
    private final String time;
    private final boolean joker;

    public TaskResult(Material material, String time, boolean joker) {
        this.material = material;
        this.time = time;
        this.joker = joker;
    }

    //Getter

    public Material getMaterial() {
        return material;
    }

    public String getTime() {
        return time;
    }

    public boolean isJoker() {
        return joker;
    }
}