package de.SurvivalChallengesPlugin.general.challenges.utils;


import de.SurvivalChallengesPlugin.SurvivalChallengesPlugin;

import java.util.EnumSet;
import java.util.Set;

public class Challenges {
    public enum Challenge{
        DELAYED_DAMAGE,
        DAMAGE_JUMP,
        MOB_JUMP,
        MOB_SWAP,
        MOB_DUPLICATOR,
        DAMAGE_RANDOM_EFFECT,
        ICE_FLOOR,
        ANVIL_RAIN,
        DAMAGE_RANDOM_TELEPORT,
        ITEM_PICKUP_DAMAGE,
        ONLY_ONE_BLOCK_USE,
        GRAVITY_SWITCH,
        JUMP_STRENGTH,
        CHUNK_RANDOM_BLOCK,
        CHUNK_SYNC,
        CHUNK_RANDOM_MOB,
        CHUNK_DISAPPEAR,
        TRAFFIC_LIGHT,
        SPEEDY
    }
    private final Set<Challenge> activeChallenges = EnumSet.noneOf(Challenge.class);

    //Getter

    public Set<Challenge> getActiveChallenges() {
        return activeChallenges;
    }

    //Setter

    public void addChallenge(Challenge challenge){
        activeChallenges.add(challenge);
    }

    public void removeChallenge(Challenge challenge){
        activeChallenges.remove(challenge);
    }

    //Check

    public boolean isActive(Challenge challenge){
        return activeChallenges.contains(challenge);
    }

    //Reload

    public static void enableChallengeSchedulers(){
        Challenges challenges = SurvivalChallengesPlugin.getInstance().getChallenges();
        if(challenges.isActive(Challenge.DELAYED_DAMAGE))
            de.SurvivalChallengesPlugin.general.challenges.events.DelayedDamage.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.DAMAGE_RANDOM_EFFECT))
            de.SurvivalChallengesPlugin.general.challenges.events.DamageRandomEffect.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.ICE_FLOOR))
            de.SurvivalChallengesPlugin.general.challenges.events.IceFloor.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.ANVIL_RAIN))
            de.SurvivalChallengesPlugin.general.challenges.events.AnvilRain.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.GRAVITY_SWITCH))
            de.SurvivalChallengesPlugin.general.challenges.events.GravitySwitch.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.CHUNK_RANDOM_MOB))
            de.SurvivalChallengesPlugin.general.challenges.events.ChunkRandomMob.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.CHUNK_DISAPPEAR))
            de.SurvivalChallengesPlugin.general.challenges.events.ChunkDisappear.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.TRAFFIC_LIGHT))
            de.SurvivalChallengesPlugin.general.challenges.events.TrafficLight.start(SurvivalChallengesPlugin.getInstance());
        if(challenges.isActive(Challenge.SPEEDY))
            de.SurvivalChallengesPlugin.general.challenges.events.Speedy.start(SurvivalChallengesPlugin.getInstance());
    }
}
