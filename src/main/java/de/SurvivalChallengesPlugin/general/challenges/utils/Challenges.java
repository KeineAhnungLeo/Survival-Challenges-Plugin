package de.SurvivalChallengesPlugin.general.challenges.utils;


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
        NETHER_SPREADS,
        CHUNK_RANDOM_MOB,
        LOL
    }
    public Set<Challenge> activeChallenges = EnumSet.noneOf(Challenge.class);

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
}
