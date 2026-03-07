package de.SurvivalChallengesPlugin.challengesmenu.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.events.ChunkDisappear;
import de.SurvivalChallengesPlugin.general.challenges.events.ChunkRandomBlock;
import de.SurvivalChallengesPlugin.general.challenges.events.ChunkSynchronisation;
import de.SurvivalChallengesPlugin.general.challenges.events.OnlyOneBlockUse;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static de.SurvivalChallengesPlugin.general.challenges.events.IceFloor.ACTIVE_PLAYER;

public class invClick implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent event) throws IllegalAccessException {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Challenge Menu")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;
            else {
                if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Settings")) {
                    createOptionsMenu(player, 1);
                    player.playSound(player, Sound.BLOCK_VAULT_ACTIVATE, 1, 1);
                }
                else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Challenges")) {
                    createChallengesMenu(player);
                    player.playSound(player, Sound.BLOCK_GRASS_BREAK, 1, 1);
                }
            }
        }
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Settings Menu - 1")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;
            if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Back")) {
                createMainMenu(player);
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            }
            else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page")) {
                createOptionsMenu(player, 2);
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            }
            else if(clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "[Active]") || clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "[Inactive]")){
                ItemStack setting = player.getOpenInventory().getTopInventory().getItem(event.getSlot() - 9);
                if (setting == null || setting.getType() == Material.AIR) return;
                ItemMeta meta1 = setting.getItemMeta();
                if (meta1 == null) return;

                if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Limited Players")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingLimitedPlayer(!settings.isSettingLimitedPlayer());
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Backpack")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingBackpack(settings.getSettingBackpack() + 1);
                    if (settings.getSettingBackpack() >= 3) {
                        settings.setSettingBackpack(0);
                    }
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Split Hearts")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingSplitHearts(!settings.isSettingSplitHearts());
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Damage Logger")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingDamageLogger(!settings.isSettingDamageLogger());
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Hardcore")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingHardcore(settings.getSettingHardcore() + 1);
                    if (settings.getSettingHardcore() >= 3) {
                        settings.setSettingHardcore(0);
                    }
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Regeneration")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingRegeneration(settings.getSettingRegeneration() + 1);
                    if (settings.getSettingRegeneration() >= 3) {
                        settings.setSettingRegeneration(0);
                    }
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Show Death Screen")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingDeathScreen(!settings.isSettingDeathScreen());
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Timer pause")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingTimerPause(!settings.isSettingTimerPause());
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Fire Tick")) {
                    if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.DO_FIRE_TICK)))
                        player.getWorld().setGameRule(GameRule.DO_FIRE_TICK, false);
                    else
                        player.getWorld().setGameRule(GameRule.DO_FIRE_TICK, true);
                }
                syncOptionsSettings();
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            }
        }
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Settings Menu - 2")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;
            if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Back")) {
                createMainMenu(player);
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            }
            else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Previous Page")) {
                createOptionsMenu(player, 1);
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            }
            else if(meta.getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "[Active]") || meta.getDisplayName().equalsIgnoreCase(ChatColor.RED + "[Inactive]")){
                ItemStack setting = player.getOpenInventory().getTopInventory().getItem(event.getSlot() - 9);
                if (setting == null || setting.getType() == Material.AIR) return;
                ItemMeta meta1 = setting.getItemMeta();
                if (meta1 == null) return;
                if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Difficulty")) {
                    if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
                        player.getWorld().setDifficulty(Difficulty.EASY);
                    } else if (player.getWorld().getDifficulty() == Difficulty.EASY) {
                        player.getWorld().setDifficulty(Difficulty.NORMAL);
                    } else if (player.getWorld().getDifficulty() == Difficulty.NORMAL) {
                        player.getWorld().setDifficulty(Difficulty.HARD);
                    } else if (player.getWorld().getDifficulty() == Difficulty.HARD) {
                        player.getWorld().setDifficulty(Difficulty.PEACEFUL);
                    }
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Boss Required")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingBossRequired(settings.getSettingBossRequired() + 1);
                    if (settings.getSettingBossRequired() >= 5) {
                        settings.setSettingBossRequired(0);
                    }
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Damage = Inv clear")) {
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    settings.setSettingDamageInvClear(!settings.isSettingDamageInvClear());
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "PvP")) {
                    if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.PVP)))
                        player.getWorld().setGameRule(GameRule.PVP, false);
                    else
                        player.getWorld().setGameRule(GameRule.PVP, true);
                } else if (meta1.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Keep Inventory")) {
                    if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)))
                        player.getWorld().setGameRule(GameRule.KEEP_INVENTORY, false);
                    else
                        player.getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
                }
                syncOptionsSettings();
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            }
        }
        if(event.getView().getTitle().equals(ChatColor.GOLD + "Challenges Menu")){
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) {
                return;
            }
            if (event.getCurrentItem().getItemMeta() != null) {
                if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Back")) {
                    createMainMenu(player);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
                }
                else{
                    Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();

                    if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Delayed Damage")) {
                        if (challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE)) challenges.removeChallenge(Challenges.Challenge.DELAYED_DAMAGE);
                        else {
                            challenges.addChallenge(Challenges.Challenge.DELAYED_DAMAGE);
                            de.SurvivalChallengesPlugin.general.challenges.events.DelayedDamage.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Damage jump")) {
                        if (challenges.isActive(Challenges.Challenge.DAMAGE_JUMP)) challenges.removeChallenge(Challenges.Challenge.DAMAGE_JUMP);
                        else challenges.addChallenge(Challenges.Challenge.DAMAGE_JUMP);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Mob jump")) {
                        if (challenges.isActive(Challenges.Challenge.MOB_JUMP)) challenges.removeChallenge(Challenges.Challenge.MOB_JUMP);
                        else challenges.addChallenge(Challenges.Challenge.MOB_JUMP);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Mob Duplicator")) {
                        if (challenges.isActive(Challenges.Challenge.MOB_DUPLICATOR)) challenges.removeChallenge(Challenges.Challenge.MOB_DUPLICATOR);
                        else challenges.addChallenge(Challenges.Challenge.MOB_DUPLICATOR);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Damage = Random Effect")) {
                        if (challenges.isActive(Challenges.Challenge.DAMAGE_RANDOM_EFFECT)) challenges.removeChallenge(Challenges.Challenge.DAMAGE_RANDOM_EFFECT);
                        else {
                            challenges.addChallenge(Challenges.Challenge.DAMAGE_RANDOM_EFFECT);
                            de.SurvivalChallengesPlugin.general.challenges.events.DamageRandomEffect.ChallengeStart();
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Ice Floor")) {
                        if (challenges.isActive(Challenges.Challenge.ICE_FLOOR)) {
                            ACTIVE_PLAYER.clear();
                            challenges.removeChallenge(Challenges.Challenge.ICE_FLOOR);
                        } else {
                            de.SurvivalChallengesPlugin.general.challenges.events.IceFloor.start(SurvivalChallengesPlugin.getInstance());
                            challenges.addChallenge(Challenges.Challenge.ICE_FLOOR);
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Anvil Rain")) {
                        if (challenges.isActive(Challenges.Challenge.ANVIL_RAIN)) challenges.removeChallenge(Challenges.Challenge.ANVIL_RAIN);
                        else {
                            challenges.addChallenge(Challenges.Challenge.ANVIL_RAIN);
                            de.SurvivalChallengesPlugin.general.challenges.events.AnvilRain.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Damage = Random Teleport")) {
                        if (challenges.isActive(Challenges.Challenge.DAMAGE_RANDOM_TELEPORT)) challenges.removeChallenge(Challenges.Challenge.DAMAGE_RANDOM_TELEPORT);
                        else challenges.addChallenge(Challenges.Challenge.DAMAGE_RANDOM_TELEPORT);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Item Pickup Damage")) {
                        if (challenges.isActive(Challenges.Challenge.ITEM_PICKUP_DAMAGE)) challenges.removeChallenge(Challenges.Challenge.ITEM_PICKUP_DAMAGE);
                        else challenges.addChallenge(Challenges.Challenge.ITEM_PICKUP_DAMAGE);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Only One Block Use")) {
                        if (challenges.isActive(Challenges.Challenge.ONLY_ONE_BLOCK_USE)){
                            challenges.removeChallenge(Challenges.Challenge.ONLY_ONE_BLOCK_USE);
                            OnlyOneBlockUse.map.clear();
                            OnlyOneBlockUse.lastBlock.clear();
                        }
                        else challenges.addChallenge(Challenges.Challenge.ONLY_ONE_BLOCK_USE);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Gravity Switch")) {
                        if (challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH)) challenges.removeChallenge(Challenges.Challenge.GRAVITY_SWITCH);
                        else {
                            challenges.addChallenge(Challenges.Challenge.GRAVITY_SWITCH);
                            de.SurvivalChallengesPlugin.general.challenges.events.GravitySwitch.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Jump Strength")) {
                        if (challenges.isActive(Challenges.Challenge.JUMP_STRENGTH)){
                            challenges.removeChallenge(Challenges.Challenge.JUMP_STRENGTH);
                            de.SurvivalChallengesPlugin.general.challenges.events.JumpStrength.playerJumpStrength.clear();
                        }
                        else challenges.addChallenge(Challenges.Challenge.JUMP_STRENGTH);
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Jump Strength")) {
                        if (challenges.isActive(Challenges.Challenge.JUMP_STRENGTH)){
                            challenges.removeChallenge(Challenges.Challenge.JUMP_STRENGTH);
                            de.SurvivalChallengesPlugin.general.challenges.events.JumpStrength.playerJumpStrength.clear();
                        }
                        else challenges.addChallenge(Challenges.Challenge.JUMP_STRENGTH);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Chunk = Random Block")) {
                        if (challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_BLOCK)){
                            challenges.removeChallenge(Challenges.Challenge.CHUNK_RANDOM_BLOCK);
                            ChunkRandomBlock.doneChunks.clear();
                        }
                        else challenges.addChallenge(Challenges.Challenge.CHUNK_RANDOM_BLOCK);}

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Chunk Synchronisation")) {
                        if (challenges.isActive(Challenges.Challenge.CHUNK_SYNC)){
                            challenges.removeChallenge(Challenges.Challenge.CHUNK_SYNC);
                            ChunkSynchronisation.blocksOverworld.clear();
                            ChunkSynchronisation.blocksNether.clear();
                            ChunkSynchronisation.blocksEnd.clear();
                        }
                        else challenges.addChallenge(Challenges.Challenge.CHUNK_SYNC);
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Chunk = Random Mob")) {
                        if (challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_MOB)){
                            challenges.removeChallenge(Challenges.Challenge.CHUNK_RANDOM_MOB);
                            de.SurvivalChallengesPlugin.general.challenges.events.ChunkRandomMob.reset();
                        }
                        else{
                            challenges.addChallenge(Challenges.Challenge.CHUNK_RANDOM_MOB);
                            de.SurvivalChallengesPlugin.general.challenges.events.ChunkRandomMob.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Chunk = 60sec")) {
                        if (challenges.isActive(Challenges.Challenge.CHUNK_DISAPPEAR)){
                            challenges.removeChallenge(Challenges.Challenge.CHUNK_DISAPPEAR);
                            ChunkDisappear.doneChunks.clear();
                            ChunkDisappear.activeChunks.clear();
                        }
                        else{
                            challenges.addChallenge(Challenges.Challenge.CHUNK_DISAPPEAR);
                            de.SurvivalChallengesPlugin.general.challenges.events.ChunkDisappear.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Traffic light")) {
                        if (challenges.isActive(Challenges.Challenge.TRAFFIC_LIGHT)) challenges.removeChallenge(Challenges.Challenge.TRAFFIC_LIGHT);
                        else{
                            challenges.addChallenge(Challenges.Challenge.TRAFFIC_LIGHT);
                            de.SurvivalChallengesPlugin.general.challenges.events.TrafficLight.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Speedy")) {
                        if (challenges.isActive(Challenges.Challenge.SPEEDY)) challenges.removeChallenge(Challenges.Challenge.SPEEDY);
                        else{
                            challenges.addChallenge(Challenges.Challenge.SPEEDY);
                            de.SurvivalChallengesPlugin.general.challenges.events.Speedy.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Player Boost")) {
                        if (challenges.isActive(Challenges.Challenge.PLAYER_BOOST)) challenges.removeChallenge(Challenges.Challenge.PLAYER_BOOST);
                        else{
                            challenges.addChallenge(Challenges.Challenge.PLAYER_BOOST);
                            de.SurvivalChallengesPlugin.general.challenges.events.PlayerBoost.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Lava Floor")) {
                        if (challenges.isActive(Challenges.Challenge.LAVA_FLOOR)){
                            challenges.removeChallenge(Challenges.Challenge.LAVA_FLOOR);
                            de.SurvivalChallengesPlugin.general.challenges.events.LavaFloor.locations.clear();
                        }
                        else{
                            challenges.addChallenge(Challenges.Challenge.LAVA_FLOOR);
                            de.SurvivalChallengesPlugin.general.challenges.events.LavaFloor.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Flying Floor")) {
                        if (challenges.isActive(Challenges.Challenge.FLYING_FLOOR)){
                            challenges.removeChallenge(Challenges.Challenge.FLYING_FLOOR);
                            de.SurvivalChallengesPlugin.general.challenges.events.FlyingFloor.locations.clear();
                        }
                        else{
                            challenges.addChallenge(Challenges.Challenge.FLYING_FLOOR);
                            de.SurvivalChallengesPlugin.general.challenges.events.FlyingFloor.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }

                    else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Bedrock Wall")) {
                        if (challenges.isActive(Challenges.Challenge.BEDROCK_WALL)){
                            challenges.removeChallenge(Challenges.Challenge.BEDROCK_WALL);
                            de.SurvivalChallengesPlugin.general.challenges.events.BedrockWall.locations.clear();
                        }
                        else{
                            challenges.addChallenge(Challenges.Challenge.BEDROCK_WALL);
                            de.SurvivalChallengesPlugin.general.challenges.events.BedrockWall.start(SurvivalChallengesPlugin.getInstance());
                        }
                    }
                    syncChallengesActivity();
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
                }
            }
        }
    }

    public static void createMainMenu(Player player){
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Challenge Menu");
        inventory.setItem(11, createGuiItem(Material.GRASS_BLOCK, ChatColor.YELLOW + "Challenges", false));
        inventory.setItem(15, createGuiItem(Material.SPAWNER, ChatColor.YELLOW + "Settings", false));
        player.openInventory(inventory);
    }

    public static void createOptionsMenu(Player player, Integer page) {
        if (page == 1) {
            Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Settings Menu - 1");
            for (int i = 0; i < 9; i++) {
                inventory.setItem(i, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " ", false));
            }
            inventory.setItem(9, createGuiItem(Material.BONE, ChatColor.YELLOW + "Limited Players", false, ChatColor.GRAY + "Disables player actions while", ChatColor.GRAY + "the timer is paused"));
            inventory.setItem(10, createGuiItem(Material.CHEST, ChatColor.YELLOW + "Backpack", false, ChatColor.GRAY + "Players can open a backpack", ChatColor.GRAY + "with /backpack"));
            inventory.setItem(11, createGuiItem(Material.TOTEM_OF_UNDYING, ChatColor.YELLOW + "Split Hearts", false, ChatColor.GRAY + "All players share the same", ChatColor.GRAY + "health and take equal damage"));
            inventory.setItem(12, createGuiItem(Material.HEART_POTTERY_SHERD, ChatColor.YELLOW + "Damage Logger", false, ChatColor.GRAY + "Logs every damage a player", ChatColor.GRAY + "receives in chat"));
            inventory.setItem(13, createGuiItem(Material.HEAVY_CORE, ChatColor.YELLOW + "Hardcore", false, ChatColor.GRAY + "No respawn allowed after death"));
            inventory.setItem(14, createGuiItem(Material.GOLDEN_APPLE, ChatColor.YELLOW + "Regeneration", false, ChatColor.GRAY + "Players naturally regain health", ChatColor.GRAY + "over time"));
            inventory.setItem(15, createGuiItem(Material.SKELETON_SKULL, ChatColor.YELLOW + "Show Death Screen", false, ChatColor.GRAY + "Displays the death screen when", ChatColor.GRAY + "a player dies"));
            inventory.setItem(16, createGuiItem(Material.CLOCK, ChatColor.YELLOW + "Timer Pause", false, ChatColor.GRAY + "Pauses the timer when a player", ChatColor.GRAY + "dies"));
            inventory.setItem(17, createGuiItem(Material.FLINT_AND_STEEL, ChatColor.YELLOW + "Fire Tick", false, ChatColor.GRAY + "Fire can burn wooden blocks", ChatColor.GRAY + "and spread through them"));
            inventory.setItem(31, createGuiItem(Material.BARRIER, ChatColor.YELLOW + "Back", false, ChatColor.GRAY + "Takes you back to the main menu"));
            inventory.setItem(32, createGuiItem(Material.ARROW, ChatColor.GREEN + "Next Page", false, ChatColor.GRAY + "Takes you to the next page"));
            player.openInventory(inventory);
            syncOptionsSettings();
        }
        else if(page == 2){
            Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Settings Menu - 2");
            for (int i = 0; i < 9; i++) {
                inventory.setItem(i, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " ", false));
            }
            inventory.setItem(9, createGuiItem(Material.IRON_SWORD, ChatColor.YELLOW + "Difficulty", false, ChatColor.GRAY + "Sets how difficult the game is"));
            inventory.setItem(10, createGuiItem(Material.ENDER_DRAGON_SPAWN_EGG, ChatColor.YELLOW + "Boss Required", false, ChatColor.GRAY + "A boss must be killed", ChatColor.GRAY + "to stop the timer"));
            inventory.setItem(11, createGuiItem(Material.OAK_SHELF, ChatColor.YELLOW + "Damage = Inv Clear", false, ChatColor.GRAY + "When a player takes damage", ChatColor.GRAY + "all players' inventories", ChatColor.GRAY + "are cleared"));
            inventory.setItem(12, createGuiItem(Material.NETHERITE_SWORD, ChatColor.YELLOW + "PvP", false, ChatColor.GRAY + "Players can hit each-other"));
            inventory.setItem(13, createGuiItem(Material.COPPER_CHEST, ChatColor.YELLOW + "Keep Inventory", false, ChatColor.GRAY + "Players keep their inventory", ChatColor.GRAY + "after death"));
            inventory.setItem(31, createGuiItem(Material.BARRIER, ChatColor.YELLOW + "Back", false, ChatColor.GRAY + "Takes you back to the main menu"));
            inventory.setItem(30, createGuiItem(Material.ARROW, ChatColor.GREEN + "Previous Page", false, ChatColor.GRAY + "Takes you to the previous page"));
            player.openInventory(inventory);
            syncOptionsSettings();
        }
    }

    public static void createChallengesMenu(Player player){
            Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Challenges Menu");

        inventory.setItem(0, createGuiItem(Material.NETHER_WART, ChatColor.YELLOW + "Delayed Damage", false, ChatColor.GRAY + "Damage is applied only every", ChatColor.GRAY + "five minutes and summed"));
        inventory.setItem(1, createGuiItem(Material.FROG_SPAWN_EGG, ChatColor.YELLOW + "Damage Jump", false, ChatColor.GRAY + "Launches the player into the air", ChatColor.GRAY + "based on the amount of damage they", ChatColor.GRAY + "have taken"));
        inventory.setItem(2, createGuiItem(Material.LEATHER_BOOTS, ChatColor.YELLOW + "Mob Jump", false, ChatColor.GRAY + "Spawns a random mob whenever", ChatColor.GRAY + "a player jumps"));
        inventory.setItem(3, createGuiItem(Material.SILVERFISH_SPAWN_EGG, ChatColor.YELLOW + "Mob Duplicator", false, ChatColor.GRAY + "Each mob that dies multiplies", ChatColor.GRAY + "2x, 4x, or 8x based on", ChatColor.GRAY + "how many times it already died", ChatColor.RED + "[Performance heavy]"));
        inventory.setItem(4, createGuiItem(Material.BREWING_STAND, ChatColor.YELLOW + "Damage = Random Effect", false, ChatColor.GRAY + "Whenever a player takes damage,", ChatColor.GRAY + "all players get a random potion effect"));
        inventory.setItem(5, createGuiItem(Material.PACKED_ICE, ChatColor.YELLOW + "Ice Floor", false, ChatColor.GRAY + "When a player sneaks, a 3x3", ChatColor.GRAY + "ice floor is generated below them"));
        inventory.setItem(6, createGuiItem(Material.ANVIL, ChatColor.YELLOW + "Anvil Rain", false, ChatColor.GRAY + "Anvils rain wherever a player", ChatColor.GRAY + "walks"));
        inventory.setItem(7, createGuiItem(Material.ENDER_PEARL, ChatColor.YELLOW + "Damage = Random Teleport", false, ChatColor.GRAY + "Whenever a player takes damage,", ChatColor.GRAY + "all players are teleported to", ChatColor.GRAY + "random locations"));
        inventory.setItem(8, createGuiItem(Material.HOPPER, ChatColor.YELLOW + "Item Pickup Damage", false, ChatColor.GRAY + "Picking up or moving items in UI", ChatColor.GRAY + "deals damage based on amount"));
        inventory.setItem(9, createGuiItem(Material.COBBLESTONE, ChatColor.YELLOW + "Only One Block Use", false, ChatColor.GRAY + "Players can switch a block", ChatColor.GRAY + "below them only once"));
        inventory.setItem(10, createGuiItem(Material.WIND_CHARGE, ChatColor.YELLOW + "Gravity Switch", false, ChatColor.GRAY + "Gravity changes every few minutes", ChatColor.GRAY + "affecting all entities"));
        inventory.setItem(11, createGuiItem(Material.DIAMOND_BOOTS, ChatColor.YELLOW + "Jump Strength", false, ChatColor.GRAY + "When a player jumps, others", ChatColor.GRAY + "jump higher"));
        inventory.setItem(12, createGuiItem(Material.MAGENTA_GLAZED_TERRACOTTA, ChatColor.YELLOW + "Chunk = Random Block", false, ChatColor.GRAY + "All blocks in a chunk are", ChatColor.GRAY + "replaced with random ones", ChatColor.RED + "[Performance heavy]"));
        inventory.setItem(13, createGuiItem(Material.OBSERVER, ChatColor.YELLOW + "Chunk Synchronisation", false, ChatColor.GRAY + "Placed or destroyed blocks are", ChatColor.GRAY + "synchronized across all chunks", ChatColor.RED + "[Performance heavy]"));
        inventory.setItem(14, createGuiItem(Material.ELDER_GUARDIAN_SPAWN_EGG, ChatColor.YELLOW + "Chunk = Random Mob", false, ChatColor.GRAY + "Entering a chunk spawns a", ChatColor.GRAY + "random mob that must be killed", ChatColor.GRAY + "to progress to the next chunk"));
        inventory.setItem(15, createGuiItem(Material.BLAZE_POWDER, ChatColor.YELLOW + "Chunk = 60sec", false, ChatColor.GRAY + "Chunks are removed 60 seconds", ChatColor.GRAY + "after a player enters them", ChatColor.RED + "[Performance heavy]"));
        inventory.setItem(16, createGuiItem(Material.LIME_CONCRETE_POWDER, ChatColor.YELLOW + "Traffic Light", false, ChatColor.GRAY + "Traffic lights switch to red", ChatColor.GRAY + "every few minutes, forcing", ChatColor.GRAY + "players to stop moving"));
        inventory.setItem(17, createGuiItem(Material.RABBIT_FOOT, ChatColor.YELLOW + "Speedy", false, ChatColor.GRAY + "All entities move very fast"));
        inventory.setItem(18, createGuiItem(Material.FIREWORK_ROCKET, ChatColor.YELLOW + "Player Boost", false, ChatColor.GRAY + "Every few seconds or minutes,", ChatColor.GRAY + "the player is boosted in a", ChatColor.GRAY + "random direction"));
        inventory.setItem(19, createGuiItem(Material.MAGMA_BLOCK, ChatColor.YELLOW + "Lava Floor", false, ChatColor.GRAY + "Wherever a player walks,", ChatColor.GRAY + "the floor turns into lava"));
        inventory.setItem(20, createGuiItem(Material.LARGE_AMETHYST_BUD, ChatColor.YELLOW + "Flying Floor", false, ChatColor.GRAY + "Wherever a player walks,", ChatColor.GRAY + "the floor flies in the air"));
        inventory.setItem(21, createGuiItem(Material.BEDROCK, ChatColor.YELLOW + "Bedrock Wall", false, ChatColor.GRAY + "Wherever a player walks, a", ChatColor.GRAY + "large bedrock wall follows", ChatColor.AQUA + "[Joker available]"));
        inventory.setItem(31, createGuiItem(Material.BARRIER, ChatColor.YELLOW + "Back", false, ChatColor.GRAY + "Takes you back to the main menu"));
        player.openInventory(inventory);
        syncChallengesActivity();
    }

    private static void syncChallengesActivity(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Challenges Menu")) {
                for (int i = 0; i < 22; i++) {
                    ItemStack stack = player.getOpenInventory().getItem(i);
                    if (stack == null || stack.getType() == Material.AIR) continue;
                    ItemMeta meta = stack.getItemMeta();
                    if (meta == null)
                        continue;
                    String name = meta.getDisplayName();
                    Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                    boolean enchanted = false;
                    if(name.equalsIgnoreCase(ChatColor.YELLOW + "Delayed Damage") && challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Damage Jump") && challenges.isActive(Challenges.Challenge.DAMAGE_JUMP))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Mob Jump") && challenges.isActive(Challenges.Challenge.MOB_JUMP))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Mob Duplicator") && challenges.isActive(Challenges.Challenge.MOB_DUPLICATOR))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Damage = Random Effect") && challenges.isActive(Challenges.Challenge.DAMAGE_RANDOM_EFFECT))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Ice Floor") && challenges.isActive(Challenges.Challenge.ICE_FLOOR))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Anvil Rain") && challenges.isActive(Challenges.Challenge.ANVIL_RAIN))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Damage = Random Teleport") && challenges.isActive(Challenges.Challenge.DAMAGE_RANDOM_TELEPORT))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Item Pickup Damage") && challenges.isActive(Challenges.Challenge.ITEM_PICKUP_DAMAGE))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Only One BLock Use") && challenges.isActive(Challenges.Challenge.ONLY_ONE_BLOCK_USE))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Gravity Switch") && challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Jump Strength") && challenges.isActive(Challenges.Challenge.JUMP_STRENGTH))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Chunk = Random Block") && challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_BLOCK))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Chunk Synchronisation") && challenges.isActive(Challenges.Challenge.CHUNK_SYNC))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Chunk = Random Mob") && challenges.isActive(Challenges.Challenge.CHUNK_RANDOM_MOB))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Chunk = 60sec") && challenges.isActive(Challenges.Challenge.CHUNK_DISAPPEAR))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Traffic Light") && challenges.isActive(Challenges.Challenge.TRAFFIC_LIGHT))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Speedy") && challenges.isActive(Challenges.Challenge.SPEEDY))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Player Boost") && challenges.isActive(Challenges.Challenge.PLAYER_BOOST))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Lava Floor") && challenges.isActive(Challenges.Challenge.LAVA_FLOOR))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Flying Floor") && challenges.isActive(Challenges.Challenge.FLYING_FLOOR))
                        enchanted = true;
                    else if(name.equalsIgnoreCase(ChatColor.YELLOW + "Bedrock Wall") && challenges.isActive(Challenges.Challenge.BEDROCK_WALL))
                        enchanted = true;
                    player.getOpenInventory().getTopInventory().setItem(i, editEnchantedGuiItem(player.getOpenInventory().getTopInventory(), i, enchanted));
                }
            }
        }
    }


    private static void syncOptionsSettings(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Settings Menu - 1")) {
                for (int i = 9; i < 18; i++) {
                    ItemStack stack = player.getOpenInventory().getItem(i);
                    if (stack == null || stack.getType() == Material.AIR) continue;
                    ItemMeta meta = stack.getItemMeta();
                    if (meta == null) continue;
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    String name = meta.getDisplayName();
                    if (name.equalsIgnoreCase(ChatColor.YELLOW + "Limited Players")) {
                        if (settings.isSettingLimitedPlayer())
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Backpack")) {
                        if (settings.getSettingBackpack() == 0)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                        else if (settings.getSettingBackpack() == 1)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Every player share the same backpack"));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Every player has their own backpack"));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Split Hearts")) {
                        if (settings.isSettingSplitHearts()) {
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                            de.SurvivalChallengesPlugin.general.settings.events.Settings.iniSplitPlayerHearts();
                        } else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Damage Logger")) {
                        if (settings.isSettingDamageLogger())
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Hardcore")) {
                        if (settings.getSettingHardcore() == 0)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                        else if (settings.getSettingHardcore() == 1)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Every player dies on their own"));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Every player dies if one player", ChatColor.GOLD + "dies"));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Regeneration")) {
                        if (settings.getSettingRegeneration() == 0)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                        else if (settings.getSettingRegeneration() == 1)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Players can only regenerate", ChatColor.GOLD + "through potions, stews,", ChatColor.GOLD + "golden apples, beacons, etc."));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Show Death Screen")) {
                        if (settings.isSettingDeathScreen())
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Timer Pause")) {
                        if (settings.isSettingTimerPause())
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Fire Tick")) {
                        if(Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.DO_FIRE_TICK)))
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    }
                }
            }
            if (player.getOpenInventory().getTitle().equals(ChatColor.GOLD + "Settings Menu - 2")) {
                for (int i = 9; i < 18; i++) {
                    ItemStack stack = player.getOpenInventory().getItem(i);
                    if (stack == null || stack.getType() == Material.AIR) continue;
                    ItemMeta meta = stack.getItemMeta();
                    if (meta == null) continue;
                    de.SurvivalChallengesPlugin.general.settings.utils.Settings settings = SurvivalChallengesPlugin.getInstance().getSettings();
                    String name = meta.getDisplayName();
                    if (name.equalsIgnoreCase(ChatColor.YELLOW + "Difficulty")) {
                        if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Peaceful"));
                        else if (player.getWorld().getDifficulty() == Difficulty.EASY)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Easy"));
                        else if (player.getWorld().getDifficulty() == Difficulty.NORMAL)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Normal"));
                        else if (player.getWorld().getDifficulty() == Difficulty.HARD)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Hard"));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Boss Required")) {
                        if (settings.getSettingBossRequired() == 0)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                        else if (settings.getSettingBossRequired() == 1)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Ender dragon"));
                        else if (settings.getSettingBossRequired() == 2)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Wither"));
                        else if (settings.getSettingBossRequired() == 3)
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Elder guardian"));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.ORANGE_DYE, ChatColor.GREEN + "[Active]", false, ChatColor.GOLD + "Warden"));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Split Inventory")) {
                        if (settings.isSettingDamageInvClear())
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Damage = Inv clear")) {
                        if (settings.isSettingDamageInvClear())
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "PvP")) {
                        if(Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.PVP)))
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    } else if (name.equalsIgnoreCase(ChatColor.YELLOW + "Keep Inventory")) {
                        if(Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)))
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "[Active]", false));
                        else
                            player.getOpenInventory().getTopInventory().setItem((i + 9), createGuiItem(Material.RED_DYE, ChatColor.RED + "[Inactive]", false));
                    }
                }
            }
        }
    }

    private static ItemStack createGuiItem(Material material, String name, boolean enchanted, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (enchanted)
                meta.setEnchantmentGlintOverride(true);
            if (meta.hasLore())
                meta.setLore(null);
            if (lore.length > 0)
                meta.setLore(Arrays.asList(lore));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack editEnchantedGuiItem(Inventory inventory ,int slot, Boolean enchanted) {
        ItemStack item = inventory.getItem(slot);
        if (item == null || item.getType().isAir()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setEnchantmentGlintOverride(enchanted);
            item.setItemMeta(meta);
        }
        return item;
    }
}