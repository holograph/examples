// Based off of http://stackoverflow.com/questions/21432400/is-branch-prediction-not-working

package com.tomergabel.examples.cpu.branching;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple baseline performance benchmarks. Creates a bunch of random bytes, and subsequently sums up
 * all positive bytes.
 */
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(2)
@State(Scope.Thread)
public class Baseline {

    private byte[] data = new byte[Configuration.DATA_SIZE];

    @Setup
    public void setup() {
        new Random(Configuration.RANDOM_SEED).nextBytes(data);
    }

    @Benchmark
    public long sum() {
        long sum = 0;
        for (int c = 0; c < Configuration.DATA_SIZE; c++)
            if (data[c] >= 0)
                sum += data[c];
        return sum;
    }
}