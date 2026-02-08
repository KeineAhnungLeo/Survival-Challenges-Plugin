package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import de.SurvivalChallengesPlugin.general.challenges.utils.ChunkSynchronisation.BlockKey;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashMap;
import java.util.Map;

public class ChunkSynchronisation implements Listener {
    public static final Map<BlockKey, BlockData> blocksOverworld = new HashMap<>();
    public static final Map<BlockKey, BlockData> blocksNether = new HashMap<>();
    public static final Map<BlockKey, BlockData> blocksEnd = new HashMap<>();
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.CHUNK_SYNC)) {
            Player player = event.getPlayer();
            World.Environment environment = player.getWorld().getEnvironment();
            BlockKey key = new BlockKey(event.getBlock().getX() & 15, event.getBlock().getY(), event.getBlock().getZ() & 15);
            Chunk orignChunk = event.getBlock().getChunk();
            if (environment.equals(World.Environment.NORMAL)) {
                blocksOverworld.put(key, event.getBlock().getBlockData());
                for (Chunk chunk : player.getWorld().getLoadedChunks()) {
                    if (!chunk.isLoaded()) continue;
                    if (chunk.equals(orignChunk)) continue;
                    if (chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.AIR)
                        continue;
                    chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).setBlockData(blocksOverworld.get(key), true);
                }
            } else if (environment.equals(World.Environment.NETHER)) {
                blocksNether.put(key, event.getBlock().getBlockData());
                for (Chunk chunk : player.getWorld().getLoadedChunks()) {
                    if (!chunk.isLoaded()) continue;
                    if (chunk.equals(orignChunk)) continue;
                    if (chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.AIR)
                        continue;
                    chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).setBlockData(blocksNether.get(key), true);
                }
            } else {
                blocksEnd.put(key, event.getBlock().getBlockData());
                for (Chunk chunk : player.getWorld().getLoadedChunks()) {
                    if (!chunk.isLoaded()) continue;
                    if (chunk.equals(orignChunk)) continue;
                    if (chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.AIR)
                        continue;
                    chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).setBlockData(blocksEnd.get(key), true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.CHUNK_SYNC)) {
            Player player = event.getPlayer();
            World.Environment environment = player.getWorld().getEnvironment();
            BlockKey key = new BlockKey(event.getBlock().getX() & 15, event.getBlock().getY(), event.getBlock().getZ() & 15);
            if (environment.equals(World.Environment.NORMAL))
                blocksOverworld.put(key, Material.AIR.createBlockData());
            else if (environment.equals(World.Environment.NETHER))
                blocksNether.put(key, Material.AIR.createBlockData());
            else
                blocksEnd.put(key, Material.AIR.createBlockData());
            Chunk orignChunk = event.getBlock().getChunk();
            for (Chunk chunk : player.getWorld().getLoadedChunks()) {
                if (!chunk.isLoaded()) continue;
                if (chunk.equals(orignChunk)) continue;
                if (chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.END_PORTAL_FRAME && chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.SPAWNER)
                    chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).setBlockData(Material.AIR.createBlockData(), false);
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if (!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if (challenges.isActive(Challenges.Challenge.CHUNK_SYNC)) {
            Chunk chunk = event.getChunk();
            World.Environment environment = chunk.getWorld().getEnvironment();
            if (environment.equals(World.Environment.NORMAL))
                for (BlockKey key : blocksOverworld.keySet()) {
                    if (chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.END_PORTAL_FRAME && chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.SPAWNER)
                        chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).setBlockData(blocksOverworld.get(key), false);
                }
            else if (environment.equals(World.Environment.NETHER))
                for (BlockKey key : blocksNether.keySet()) {
                    if (chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.END_PORTAL_FRAME && chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).getType() != Material.SPAWNER)
                        chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).setBlockData(blocksNether.get(key), false);
                }
            else
                for (BlockKey key : blocksEnd.keySet())
                    chunk.getWorld().getBlockAt(chunk.getX() * 16 + key.x, key.y, chunk.getZ() * 16 + key.z).setBlockData(blocksEnd.get(key), false);
        }
    }
}
