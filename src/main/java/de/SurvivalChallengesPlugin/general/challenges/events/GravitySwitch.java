package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class GravitySwitch implements Listener {
    private static BukkitRunnable task;
    private static BukkitRunnable task1;
    private static BukkitRunnable task2;
    private static final Random random = new Random();
    private static int randomMin;
    private static int randomSec;
    public static BossBar bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
    private static int timeSwitch = 10;
    private static int gravityType = -1;
    private static int activeGravityTime;

    @EventHandler
    public void onProjectileShoot(ProjectileLaunchEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH)) {
            if (gravityType == 1) {
                Entity entity = event.getEntity();
                entity.setGravity(false);
                Vector velocity = entity.getVelocity();
                entity.setVelocity(velocity);
            }
            else if(gravityType == 3){
                Entity entity = event.getEntity();
                entity.setGravity(false);
                Vector velocity = entity.getVelocity();
                velocity.setY(velocity.getY()+0.5);
                entity.setVelocity(velocity);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH)) {
            if (gravityType == 1) {
                Item item = event.getItemDrop();
                item.setGravity(false);
                Vector velocity = item.getVelocity();
                item.setVelocity(velocity);
            } else if (gravityType == 3) {
                Item item = event.getItemDrop();
                item.setGravity(false);
                Vector velocity = item.getVelocity();
                velocity.setY(velocity.getY() + 0.5);
                item.setVelocity(velocity);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH) && (activeGravityTime >= 1 || timeSwitch >= 1))
            bossBar.addPlayer(event.getPlayer());
        else bossBar.removePlayer(event.getPlayer());
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        randomSec = 0;
        randomMin = 0;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH)) {
                    task.cancel();
                    task = null;
                    for(Player player : Bukkit.getOnlinePlayers())
                        bossBar.removePlayer(player);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Entity entity : player.getWorld().getEntities()) {
                            if (!(entity instanceof LivingEntity)) continue;
                            AttributeInstance attribute = ((LivingEntity) entity).getAttribute(Attribute.GRAVITY);
                            if (attribute == null) continue;
                            attribute.setBaseValue(0.08);
                        }
                    }
                    return;
                }
                if(randomSec == 0 && randomMin == 0){
                    randomSec = random.nextInt(59)+1;
                    randomMin = random.nextInt(5)+1 + timer.getTimeM();
                    if(randomMin >= 60)
                        randomMin = randomMin - 60;
                }
                if(timer.getTimeS() == randomSec && timer.getTimeM() == randomMin){
                    randomSec = -1;
                    randomMin = -1;
                    switchGravity(SurvivalChallengesPlugin.getInstance());
                }
                if(gravityType == -1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Entity entity : player.getWorld().getEntities()) {
                            if (!(entity instanceof LivingEntity)) continue;
                            AttributeInstance attribute = ((LivingEntity) entity).getAttribute(Attribute.GRAVITY);
                            if (attribute == null) continue;
                            attribute.setBaseValue(0.08);
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 10);
    }

    public static void setRandomTime(int randomMin, int randomSec) {
        GravitySwitch.randomMin = randomMin;
        GravitySwitch.randomSec = randomSec;
    }

    private static void switchGravity(JavaPlugin plugin){
        timeSwitch = 10;
        bossBar.setColor(BarColor.RED);
        bossBar.setTitle(ChatColor.RED + "Gravity switch in " + ChatColor.BOLD + timeSwitch);
        for(Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        bossBar.setProgress(1);
        if (task1 != null) return;
        task1 = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH)) {
                    task1.cancel();
                    task1 = null;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        bossBar.removePlayer(player);
                    }
                    return;
                }
                if (timer.isRunning()) {
                    if (timeSwitch >= 0) {
                        timeSwitch--;
                        double progress = Math.max(0, timeSwitch / 10.0);
                        bossBar.setProgress(progress);
                        if(timeSwitch >= 0)
                            bossBar.setTitle(ChatColor.RED + "Gravity switch in " + ChatColor.BOLD + timeSwitch);
                    }
                    if(timeSwitch == -1){
                        gravityType = random.nextInt(4);
                        bossBar.setColor(BarColor.YELLOW);
                        bossBar.setProgress(1);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                        }
                        switch (gravityType){
                            case 0:{
                                bossBar.setTitle(ChatColor.YELLOW + "High Gravity");
                                break;
                            }
                            case 1:{
                                bossBar.setTitle(ChatColor.YELLOW + "No Gravity");
                                break;
                            }
                            case 2:{
                                bossBar.setTitle(ChatColor.YELLOW + "Low Gravity");
                                break;
                            }
                            case 3:{
                                bossBar.setTitle(ChatColor.YELLOW + "Anti Gravity");
                                break;
                            }
                        }
                        activeGravity(SurvivalChallengesPlugin.getInstance());
                        task1.cancel();
                        task1 = null;
                    }
                }
            }
        };
        task1.runTaskTimer(plugin, 0L, 20);
    }

    private static void activeGravity(JavaPlugin plugin){
        activeGravityTime = 1200;
        if (task2 != null) return;
        task2 = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if (!challenges.isActive(Challenges.Challenge.GRAVITY_SWITCH)) {
                    task2.cancel();
                    task2 = null;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        bossBar.removePlayer(player);
                    }
                    return;
                }
                if (timer.isRunning()) {
                    if(gravityType == 0){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            for (Entity entity : player.getWorld().getEntities()){
                                if (!(entity instanceof LivingEntity)) continue;
                                AttributeInstance attribute = ((LivingEntity) entity).getAttribute(Attribute.GRAVITY);
                                if(attribute == null) continue;
                                attribute.setBaseValue(1);
                            }
                            for (Entity entity : player.getWorld().getEntities()){
                                if (entity instanceof Projectile || entity instanceof Item || entity instanceof FallingBlock) {
                                    Vector vector = entity.getVelocity();
                                    vector.setY(Math.min(vector.getY(), -0.35));
                                    entity.setVelocity(vector);
                                }
                            }
                        }
                    }
                    if(gravityType == 1){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            for (Entity entity : player.getWorld().getEntities()){
                                if (!(entity instanceof LivingEntity) || entity instanceof Player) continue;
                                AttributeInstance attribute = ((LivingEntity) entity).getAttribute(Attribute.GRAVITY);
                                if(attribute == null) continue;
                                attribute.setBaseValue(0);
                                if(entity.isOnGround()){
                                    Vector vector = entity.getVelocity();
                                    vector.setY(vector.getY()+0.1);
                                    entity.setVelocity(vector);
                                }
                            if(player.isSneaking())
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10, 0));
                            else
                                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 0));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 10, 3));
                            }
                        }
                    }
                    if(gravityType == 2){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            for (Entity entity : player.getWorld().getEntities()){
                                if (!(entity instanceof LivingEntity)) continue;
                                AttributeInstance attribute = ((LivingEntity) entity).getAttribute(Attribute.GRAVITY);
                                if(attribute == null) continue;
                                attribute.setBaseValue(0.01);
                                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10, 0));
                                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 0));
                            }
                        }
                    }
                    if(gravityType == 3){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            for (Entity entity : player.getWorld().getEntities()){
                                if (!(entity instanceof LivingEntity)) continue;
                                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 2));
                            }
                        }
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Entity entity : player.getWorld().getEntities()) {
                            if (entity.getLocation().getY() >= 250 && entity instanceof LivingEntity living) {
                                living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 45, 20));
                            }
                        }
                    }
                    if(activeGravityTime == 0){
                        gravityType = -1;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            bossBar.removePlayer(player);
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1,1);
                        }
                        for (World world : Bukkit.getWorlds())
                            for (Item item : world.getEntitiesByClass(Item.class))
                                item.setGravity(true);
                        randomSec = 0;
                        randomMin = 0;
                        task2.cancel();
                        task2 = null;
                    }
                    if(activeGravityTime >= 1)
                        activeGravityTime--;
                }
            }
        };
        task2.runTaskTimer(plugin, 0L, 5);
    }

    public static void stop(){
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }

}