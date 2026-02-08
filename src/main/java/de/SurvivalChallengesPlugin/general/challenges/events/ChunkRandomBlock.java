package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;


public class ChunkRandomBlock implements Listener {
    public static final Set<String> doneChunks = new HashSet<>();
    private static final Random random = new Random();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_BLOCK)) {
            if (event.getTo() == null) return;
            Player player = event.getPlayer();
            if(player.getGameMode() == GameMode.SPECTATOR) return;
            Chunk oldChunk = event.getFrom().getChunk();
            Chunk newChunk = event.getTo().getChunk();
            if (oldChunk.equals(newChunk)) return;
            String chunkId = newChunk.getX() + ";" + newChunk.getZ();
            if (doneChunks.contains(chunkId)) return;
            doneChunks.add(chunkId);
            int blockX = newChunk.getX() * 16;
            int blockZ = newChunk.getZ() * 16;
            List<Material> candidates = new ArrayList<>();
            for (Material material : Material.values()) {
                if (!material.isBlock()) continue;
                if (!material.isSolid()) continue;
                if (!material.isOccluding()) continue;
                if (material.name().contains("SHULKER") || material.name().contains("COMMAND") || material.name().contains("JIGSAW") || material.name().contains("TEST") || material.name().contains("SPAWNER"))
                    continue;
                candidates.add(material);
            }
            if (candidates.isEmpty()) return;
            Material material = candidates.get(random.nextInt(candidates.size()));
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = player.getWorld().getMinHeight(); y < player.getWorld().getMaxHeight(); y++) {
                        Block block = player.getWorld().getBlockAt(blockX + x, y, blockZ + z);
                        if (block.getType() != Material.AIR && block.getType() != Material.WATER && block.getType() != Material.END_PORTAL_FRAME && block.getType() != Material.SPAWNER && block.getType() != Material.BEDROCK && block.getType() != Material.NETHER_PORTAL && block.getType() != Material.END_PORTAL  && block.getType() != Material.CHEST) {
                            block.setType(material, false);
                        }
                    }
                }
            }
        }
    }
}