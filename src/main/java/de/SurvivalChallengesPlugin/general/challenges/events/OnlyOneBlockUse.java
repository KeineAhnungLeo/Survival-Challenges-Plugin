package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnlyOneBlockUse implements Listener {
    public static Map<UUID, Material> lastBlock = new HashMap<>();
    public static Map<UUID, Map<Material, Boolean>> map = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (!challenges.isActive(Challenges.Challenge.ONLY_ONE_BLOCK_USE)) return;
        Player player = event.getPlayer();
        Location loc = player.getLocation().clone().subtract(0, 0.25, 0);
        Material currentBlock = loc.getBlock().getType();
        if (currentBlock == Material.AIR || currentBlock == Material.WATER || currentBlock == Material.LAVA || !currentBlock.isSolid() || currentBlock == Material.OBSIDIAN) return;
        UUID uuid = player.getUniqueId();
        if (lastBlock.get(uuid) == currentBlock) return;
        lastBlock.put(uuid, currentBlock);
        Map<Material, Boolean> playerMap = map.computeIfAbsent(uuid, k -> new HashMap<>());
        if (playerMap.getOrDefault(currentBlock, false)) {
            for(Player player1 : Bukkit.getOnlinePlayers())
                player1.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "OnlyOneBlockUse" + ChatColor.GRAY + "] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " stand on " + ChatColor.BLUE + currentBlock);
            de.SurvivalChallengesPlugin.general.settings.events.Settings.killPlayerCustom(player);
        } else {
            playerMap.put(currentBlock, true);
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "OnlyOneBlockUse" + ChatColor.GRAY + "] " + ChatColor.GRAY + "+ " + ChatColor.BLUE + currentBlock);
        }
    }
}