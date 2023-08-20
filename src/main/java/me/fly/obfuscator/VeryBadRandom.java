package me.fly.obfuscator;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.Random;

import static java.lang.Math.cos;

/**
 * A very bad biased random generator created by me putting random operations together.
 */
public class VeryBadRandom {


    private Random underlying;
    private long seed;
    private long current;

    public VeryBadRandom() {
        this(System.currentTimeMillis(), 1);
    }

    public VeryBadRandom(long seed, long seed2) {
        this.seed = seed;
        this.underlying = new Random(seed2);
    }

    public long nextLong() {
        return underlying.nextLong();
    }

    public int nextInt() {
        return (int) (((double) nextLong())/Integer.MAX_VALUE);
    }

    public int nextInt(int max) {
        return Math.abs(nextInt()%max);
    }

    public static void main(String[] args) {
        VeryBadRandom random = new VeryBadRandom();

        Int2IntMap map = new Int2IntOpenHashMap();

        for(int i = 0; i < 40000; i++) {
            int result = random.nextInt(32);

            map.put(result, map.getOrDefault(result, 0)+1);
        }

        for(int key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }
    }
}
