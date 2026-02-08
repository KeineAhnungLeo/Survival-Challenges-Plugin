package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class NetherSpreads implements Listener {
    public static boolean spreads = false;
    private static BukkitRunnable task;
    @EventHandler
    public void onPlayerActivatesNetherPortal(PortalCreateEvent event){
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        if(!challenges.isActive(Challenges.Challenge.NETHER_SPREADS)) {
            if (event.getReason() != PortalCreateEvent.CreateReason.FIRE) return;
            spreads = true;
        }
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.NETHER_SPREADS)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if(!spreads) return;
                if(!timer.isRunning()) return;
                for (Player player : Bukkit.getOnlinePlayers()){
                    Location playerLoc = player.getLocation();
                    int radius = 15;
                    int heightAbove = 5;
                    int heightBelow = 5;
                    for (int x = -radius; x <= radius; x++) {
                        for (int z = -radius; z <= radius; z++) {
                            double horizontalDistance = Math.sqrt(x*x + z*z);
                            if(horizontalDistance > radius) continue;
                            for (int y = -heightBelow; y <= heightAbove; y++) {
                                Location loc = playerLoc.clone().add(x, y, z);
                                if(horizontalDistance >= radius - 1) {
                                    onReplaceWater(loc);
                                }
                                else if(horizontalDistance <= radius - 2) {
                                    onReplaceAll(loc);
                                    onReplaceObsidian(loc);
                                }
                            }
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1);
    }

    private static void onReplaceAll(Location location){
        Material material = location.getBlock().getType();
        Block block = location.getBlock();
        switch (material){
            case BIG_DRIPLEAF, MOSS_CARPET, BIG_DRIPLEAF_STEM, SMALL_DRIPLEAF, CAVE_VINES_PLANT, CAVE_VINES, SPORE_BLOSSOM -> replaceWithBlockData(block, Material.AIR, true);
            case SAND, SUSPICIOUS_SAND -> replaceWithBlockData(block, Material.SOUL_SOIL, false);
            case CACTUS, SUGAR_CANE, CACTUS_FLOWER -> replaceWithBlockData(block, Material.TWISTING_VINES, true);
            case DEAD_BUSH -> replaceWithBlockData(block, Material.WARPED_FUNGUS, false);
            case CUT_SANDSTONE, CHISELED_SANDSTONE, SMOOTH_SANDSTONE -> replaceWithBlockData(block, Material.NETHER_BRICKS, false);
            case SANDSTONE_STAIRS -> replaceWithBlockData(block, Material.NETHER_BRICK_STAIRS, false);
            case TALL_DRY_GRASS, SHORT_DRY_GRASS ->  replaceWithBlockData(block, Material.CRIMSON_FUNGUS, false);
            case SUSPICIOUS_GRAVEL ->  replaceWithBlockData(block, Material.GRAVEL, false);
            case SANDSTONE, STONE, COBBLESTONE, DEEPSLATE, DIRT -> replaceWithBlockData(block, Material.NETHERRACK, false);
            case COAL_ORE, IRON_ORE, GOLD_ORE, DIAMOND_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_DIAMOND_ORE -> replaceWithBlockData(block, Material.NETHER_GOLD_ORE, false);
            case COPPER_ORE, REDSTONE_ORE, EMERALD_ORE, LAPIS_ORE, DEEPSLATE_COPPER_ORE, DEEPSLATE_REDSTONE_ORE, DEEPSLATE_EMERALD_ORE, DEEPSLATE_LAPIS_ORE -> replaceWithBlockData(block, Material.NETHER_QUARTZ_ORE, false);
            case DIORITE, ANDESITE, GRANITE -> replaceWithBlockData(block, Material.BLACKSTONE, false);
        }
    }
    private static void onReplaceWater(Location location){
        Material material = location.getBlock().getType();
        Block block = location.getBlock();
        if(material == Material.WATER || material == Material.SEAGRASS || material== Material.TALL_SEAGRASS|| material == Material.KELP){
            replaceWithBlockData(block, Material.LAVA, true);
        }
    }
    private static void onReplaceObsidian(Location location){
        Material material = location.getBlock().getType();
        Block block = location.getBlock();
        if(material == Material.OBSIDIAN){
            replaceWithBlockData(block, Material.LAVA, true);
        }
    }
    private static void replaceWithBlockData(Block block, Material material, Boolean physics) {
        BlockData data = block.getBlockData();
        block.setType(material, physics);
        BlockData newData = block.getBlockData();
        if (data.getMaterial().name().contains("STAIRS") && newData instanceof Stairs newStairs) {
            Stairs oldStairs = (Stairs) data;
            newStairs.setFacing(oldStairs.getFacing());
            newStairs.setHalf(oldStairs.getHalf());
            newStairs.setShape(oldStairs.getShape());
            block.setBlockData(newStairs);
        } else if (data.getMaterial().name().contains("SLAB") && newData instanceof Slab newSlab) {
            Slab oldSlab = (Slab) data;
            newSlab.setType(oldSlab.getType());
            block.setBlockData(newSlab);
        } else if (data.getMaterial().name().contains("FENCE") && newData instanceof Fence newFence) {
            Fence oldFence = (Fence) data;
            newFence.setFace(BlockFace.NORTH, oldFence.hasFace(BlockFace.NORTH));
            newFence.setFace(BlockFace.SOUTH, oldFence.hasFace(BlockFace.SOUTH));
            newFence.setFace(BlockFace.WEST, oldFence.hasFace(BlockFace.WEST));
            newFence.setFace(BlockFace.EAST, oldFence.hasFace(BlockFace.EAST));
            block.setBlockData(newFence);
        } else if (data.getMaterial().name().contains("FENCE_GATE") && newData instanceof Gate newGate) {
            Gate oldGate = (Gate) data;
            newGate.setFacing(oldGate.getFacing());
            newGate.setInWall(oldGate.isInWall());
            newGate.setOpen(oldGate.isOpen());
            newGate.setPowered(oldGate.isPowered());
            block.setBlockData(newGate);
        } else if (data.getMaterial().name().contains("DOOR") && newData instanceof Door newDoor) {
            Door oldDoor = (Door) data;
            newDoor.setFacing(oldDoor.getFacing());
            newDoor.setHinge(oldDoor.getHinge());
            newDoor.setHalf(oldDoor.getHalf());
            newDoor.setPowered(oldDoor.isPowered());
            newDoor.setOpen(oldDoor.isOpen());
            block.setBlockData(newDoor);
        } else if (data.getMaterial().name().contains("TRAPDOOR") && newData instanceof TrapDoor newTrapDoor) {
            TrapDoor oldTrapdoor = (TrapDoor) data;
            newTrapDoor.setFacing(oldTrapdoor.getFacing());
            newTrapDoor.setHalf(oldTrapdoor.getHalf());
            newTrapDoor.setPowered(oldTrapdoor.isPowered());
            newTrapDoor.setOpen(oldTrapdoor.isOpen());
            block.setBlockData(newTrapDoor);
        }
    }
}
