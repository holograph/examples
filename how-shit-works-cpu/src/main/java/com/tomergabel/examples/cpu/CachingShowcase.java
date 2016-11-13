package com.tomergabel.examples.cpu;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple cache performance benchmark. Creates an "image" (2-dimensional array of ARGB8888 pixels) and
 * transposes it using various block sizes (the naive version does it sequentially). This clearly shows that
 * even suboptimal blocking is tremendously beneficial.
 *
 * Sample results:
 * <pre>
 *     Benchmark                            Mode  Cnt   Score   Error  Units
 *     CachingShowcase.transposeNaive       avgt   10  43.851 ± 6.000  ms/op
 *     CachingShowcase.transposeTiled8x8    avgt   10  20.641 ± 1.646  ms/op
 *     CachingShowcase.transposeTiled16x16  avgt   10  18.515 ± 1.833  ms/op
 *     CachingShowcase.transposeTiled48x48  avgt   10  21.941 ± 1.954  ms/op
 * </pre>
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class CachingShowcase {
    private final static int WIDTH = 3840;
    private final static int HEIGHT = 2160;
    private final static int IMAGE_SIZE = WIDTH * HEIGHT;
    private final static long RANDOM_SEED = 0xdeadbeef;

    private int[] pixels;

    @Setup(Level.Trial)
    public void setup() {
        pixels = new Random(RANDOM_SEED).ints(IMAGE_SIZE).toArray();
    }

    @Benchmark
    public int[] transposeNaive() {
        int[] target = new int[IMAGE_SIZE];

        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                target[x * HEIGHT + y] = pixels[y * WIDTH + x];

        return target;
    }

    private int[] transposeTiled(int blockSize) {
        assert((WIDTH % blockSize) == 0 &&
                (HEIGHT % blockSize) == 0);
        int[] target = new int[IMAGE_SIZE];

        for (int by = 0; by < HEIGHT; by += blockSize)
            for (int bx = 0; bx < WIDTH; bx += blockSize)
                for (int y = 0; y < blockSize; y++)
                    for (int x = 0; x < blockSize; x++)
                        target[(x + bx) * HEIGHT + (y + by)] = pixels[(y + by) * WIDTH + (x + bx)];

        return target;
    }

    @Benchmark
    public int[] transposeTiled8x8() {
        return transposeTiled(8);
    }

    @Benchmark
    public int[] transposeTiled16x16() {
        return transposeTiled(16);
    }

    @Benchmark
    public int[] transposeTiled48x48() {
        return transposeTiled(48);
    }

    static public void main(String[] args) {
        // Quick & dirty validation
        CachingShowcase s = new CachingShowcase();
        s.setup();
        int[] naive = s.transposeNaive();
        int[] b8 = s.transposeTiled8x8();
        int[] b16 = s.transposeTiled16x16();
        int[] b48 = s.transposeTiled48x48();
        System.out.println("b8\t" + Arrays.equals(naive, b8));
        System.out.println("b16\t" + Arrays.equals(naive, b16));
        System.out.println("b48\t" + Arrays.equals(naive, b48));
    }
}
