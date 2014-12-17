package me.aerovulpe.ninjarunner;

public class Duration {

    public static long[] get(int duration, int tiles) {
        final long[] durationArray = new long[tiles];
        for (int i = 0; i < tiles; i++) {
            durationArray[i] = duration;
        }
        return durationArray;
    }
}