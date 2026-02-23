package de.SurvivalChallengesPlugin.general.challenges.events;

import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;
import de.SurvivalChallengesPlugin.general.challenges.utils.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

import static de.SurvivalChallengesPlugin.general.challenges.events.DelayedDamage.getSetDamage;

public class DamageRandomEffect implements Listener {
    private static final Random random = new Random();
    private static final Map<PotionEffectType, Integer> usedEffects = new HashMap<>();
    private static final List<PotionEffectType> effectPool = new ArrayList<>();
    private static BukkitRunnable task;

    public static void ChallengeStart() throws IllegalAccessException {
        effectPool.clear();
        usedEffects.clear();
        for (Field field : PotionEffectType.class.getFields()) {
            if(field.getType() != PotionEffectType.class) continue;
            PotionEffectType type = (PotionEffectType) field.get(null);
            if (type == null) continue;
            if (type == PotionEffectType.INSTANT_DAMAGE) continue;
            if (type == PotionEffectType.INSTANT_HEALTH) continue;
            if (type == PotionEffectType.WITHER) continue;
            if (type == PotionEffectType.POISON) continue;
            effectPool.add(type);
        }
        Collections.shuffle(effectPool);
        de.SurvivalChallengesPlugin.general.challenges.events.DamageRandomEffect.start(SurvivalChallengesPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
        if(!timer.isRunning()) return;
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenges.Challenge.DELAYED_DAMAGE) && !getSetDamage()) return;
        if(challenges.isActive(Challenges.Challenge.DAMAGE_RANDOM_EFFECT)) {
            PotionEffectType chosen = effectPool.get(random.nextInt(effectPool.size()));
            if (usedEffects.containsKey(chosen)){
                int amplifier = usedEffects.get(chosen);
                if(amplifier < 255)
                    amplifier++;
                usedEffects.put(chosen, amplifier);
                for(Player player1 : Bukkit.getOnlinePlayers()){
                    player1.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "DamageRandomEffect" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "+ " + ChatColor.BLUE + chosen.getKey() + " (" + (amplifier+1) + ")");
                }
            }
            else {
                usedEffects.put(chosen, 0);
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "DamageRandomEffect" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "+ " + ChatColor.BLUE + chosen.getKey());
                }
            }
        }
    }

    public static void start(JavaPlugin plugin) {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
                de.SurvivalChallengesPlugin.timer.utils.Timer timer = SurvivalChallengesPlugin.getInstance().getTimer();
                if(!challenges.isActive(Challenges.Challenge.DAMAGE_RANDOM_EFFECT)) {
                    task.cancel();
                    task = null;
                    return;
                }
                if (!timer.isRunning()) return;
                for(Player player : Bukkit.getOnlinePlayers()){
                    for(Map.Entry<PotionEffectType, Integer> entry : usedEffects.entrySet()){
                        PotionEffectType effectType = entry.getKey();
                        Integer amplifier = entry.getValue();
                        player.addPotionEffect(new PotionEffect(effectType, 20*12, amplifier));
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20);
    }
}
