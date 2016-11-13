package com.tomergabel.examples.cpu;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple benchmark to showcase branch prediction:
 * <ul>
 *     <li>
 *         The "baseline" benchmark sums up all positive numbers from a completely random sample. This
 *         demonstrates normal behavior.
 *     </li>
 *     <li>
 *         The "presorted" benchmark does the exact same thing, except it presorts the input array. This guarantees
 *         that the condition stays the same on either side of the "pivot" element (the first non-negative element
 *         in the array), which is the optimal situation for branch prediction.
 *     </li>
 *     <li>
 *         The "predicated" benchmark acts on a random data set again, but switches the operation to selecting the
 *         last non-negative element in the array. While the JIT isn't clever enough to understand the algorithm,
 *         it can make use of the <code>cmovl</code> instruction which doesn't branch, resulting in roughly the same
 *         behavior.
 *     </li>
 * </ul>
 *
 * See http://stackoverflow.com/questions/21432400/is-branch-prediction-not-working for further discussion.
 *
 * Sample output:
 * <pre>
 *     Benchmark                            Mode  Cnt    Score   Error  Units
 *     BranchingShowcase.baseline           avgt   10  130.051 ± 2.838  ms/op
 *     BranchingShowcase.predicated         avgt   10   18.976 ± 0.458  ms/op
 *     BranchingShowcase.presorted          avgt   10   13.478 ± 0.230  ms/op
 * </pre>
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class BranchingShowcase {
    private final static int DATA_SIZE = 32 * 1024 * 1024;
    private final static long RANDOM_SEED = 0xdeadbeef;

    private byte[] data;
    private byte[] presorted;

    @Setup
    public void setup() {
        data = new byte[DATA_SIZE];
        new Random(RANDOM_SEED).nextBytes(data);
        presorted = data.clone();
        Arrays.sort(presorted);
    }

    @Benchmark
    public long baseline() {
        long sum = 0;
        for (int c = 0; c < DATA_SIZE; c++)
            if (data[c] >= 0)
                sum += data[c];
        return sum;
    }

    @Benchmark
    public long presorted() {
        long sum = 0;
        for (int c = 0; c < DATA_SIZE; c++)
            if (presorted[c] >= 0)
                sum += presorted[c];
        return sum;
    }

    @Benchmark
    public long predicated() {
        long sum = 0;
        for (int c = 0; c < DATA_SIZE; c++)
            if (data[c] >= 0)
                sum = data[c];
        return sum;
    }
}
