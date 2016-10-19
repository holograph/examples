// Based off of http://stackoverflow.com/questions/21432400/is-branch-prediction-not-working

package com.tomergabel.examples.cpu.caching;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple cache performance benchmark. Creates a bunch of random bytes, and sums up bytes from random locations
 * within the array. The locations are bound by successively larger spans while total amount of data is the same
 * between runs; this showcases the access penalty difference between L1-L3 caches and main memory.
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Thread)
@Fork(2)
public class CachingShowcase {

    /*
        # Run complete. Total time: 00:01:10

        Benchmark                            Mode  Cnt     Score    Error  Units
        CachingShowcase.accessingMainMemory  avgt   10  1252.513 ± 72.487  ms/op
        CachingShowcase.fitsWithinL1         avgt   10   261.145 ±  6.679  ms/op
        CachingShowcase.fitsWithinL2         avgt   10   310.477 ±  7.438  ms/op
        CachingShowcase.fitsWithinL3         avgt   10  1101.759 ± 58.822  ms/op
     */

    static int L1_SIZE = 32768;
    static int L2_SIZE = 262144;
    static int L3_SIZE = 4194304;
    static int CACHE_LINE_SIZE = 64;
    static int L2_ASSOCIATIVITY = 8;
    static int TOTAL_DATA_READ = 20 * 1024 * 1024;

    int[] data;
    private Random random;

    @Setup
    public void setup() {
        random = new Random(0xdeadbeef);
        data = random.ints(L3_SIZE * 2).toArray();
    }

    private long massiveRead(int span) {
        long sum = 0;
        int offset = 0;
        for (int i = 0; i < TOTAL_DATA_READ; i++) {
            sum += data[offset];
            offset = random.nextInt(span);
        }
        return sum;
    }

    @Benchmark
    public long fitsWithinL1() {
        return massiveRead(L1_SIZE / 2);
    }

    @Benchmark
    public long fitsWithinL2() {
        return massiveRead(L2_SIZE / 2);
    }

    @Benchmark
    public long fitsWithinL3() {
        return massiveRead(L3_SIZE / 2);
    }

    @Benchmark
    public long accessingMainMemory() {
        return massiveRead(data.length);
    }
}