// Based off of http://stackoverflow.com/questions/21432400/is-branch-prediction-not-working

package com.tomergabel.examples.cpu.branching;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Thread)
@Fork(2)
public class Predicated {
    private final byte[] data = new byte[Configuration.DATA_SIZE];

    @Setup
    public void setup() {
        new Random(Configuration.RANDOM_SEED).nextBytes(data);
    }

    @Benchmark
    public long sum() {
        long sum = 0;
        for (int c = 0; c < Configuration.DATA_SIZE; c++)
            if (data[c] >= 0)
                sum = data[c];
        return sum;
    }
}