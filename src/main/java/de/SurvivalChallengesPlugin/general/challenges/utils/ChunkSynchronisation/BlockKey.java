package de.SurvivalChallengesPlugin.general.challenges.utils.ChunkSynchronisation;

import java.util.Objects;

public class BlockKey {
    public final int x;
    public final int y;
    public final int z;
    public BlockKey(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockKey key)) return false;
        return x == key.x && y == key.y && z == key.z;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
