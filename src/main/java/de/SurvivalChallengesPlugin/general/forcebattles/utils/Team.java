package de.SurvivalChallengesPlugin.general.forcebattles.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;

public class Team {

    private static Scoreboard scoreboard;

    public static void init() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            scoreboard = manager.getNewScoreboard();
        }
    }

    public static void createTeam(String name, ChatColor color) {
        org.bukkit.scoreboard.Team team = scoreboard.registerNewTeam(name.toLowerCase());
        team.setColor(color);
        team.setPrefix(color + "");
    }

    public static void deleteAllTeams() {
        for (org.bukkit.scoreboard.Team team : scoreboard.getTeams()) {
            team.unregister();
        }
    }

    public static void addPlayerToTeam(String name, Player player){
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(name);
        if(team != null) {
            team.addEntry(player.getName());
        }
    }

    public static org.bukkit.scoreboard.Team getTeamByName(String name){
        return scoreboard.getTeam(name);
    }

    public static org.bukkit.scoreboard.Team getTeamByPlayer(Player player){
        return scoreboard.getEntryTeam(player.getName());
    }

    public static ArrayList<Player> getPlayers(String name){
        ArrayList<Player> playerArrayList = new ArrayList<>();
            org.bukkit.scoreboard.Team team = scoreboard.getTeam(name);
            if(team!=null) {
                for (String s : team.getEntries()) {
                    Player player = Bukkit.getPlayer(s);
                    playerArrayList.add(player);
                }
            }
        return playerArrayList;
    }
}
