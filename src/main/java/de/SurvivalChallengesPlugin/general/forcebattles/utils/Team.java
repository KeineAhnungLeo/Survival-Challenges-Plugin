package de.SurvivalChallengesPlugin.general.forcebattles.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;

public class Team implements Listener {

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
        team.setPrefix(color.toString());
    }

    public static void removePlayerFromTeam(Player player){
        org.bukkit.scoreboard.Team team = getTeamByPlayer(player);
        if(team != null){
            team.removeEntry(player.getName());
        }
    }

    public static void addPlayerToTeam(String name, Player player){
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(name);
        if(team != null) {
            team.addEntry(player.getName());
            player.setScoreboard(scoreboard);
        }
    }

    public static org.bukkit.scoreboard.Team getTeamByName(String name){
        return scoreboard.getTeam(name);
    }

    public static Scoreboard getScoreboard(){
        return scoreboard;
    }

    public static ArrayList<org.bukkit.scoreboard.Team> getAllTeams(){
        return new ArrayList<>(scoreboard.getTeams());
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

    public static void deleteAllTeams(){
        for(org.bukkit.scoreboard.Team team : scoreboard.getTeams()){
            team.unregister();
        }
    }

    public static String getTeamFormattedName(org.bukkit.scoreboard.Team team){
        String teamName = team.getName().replace("_", " ");
        String[] words = teamName.split(" ");
        StringBuilder uppercasedBuilder = new StringBuilder();
        for (String word : words) {
            uppercasedBuilder.append(word.substring(0,1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
        }
        String uppercased = uppercasedBuilder.toString().trim();
        return team.getColor() + uppercased;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().setScoreboard(scoreboard);
    }
}
