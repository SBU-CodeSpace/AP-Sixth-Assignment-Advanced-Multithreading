package MonteCarloPI;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");
    }

    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints)
    {
        Random random = new Random();
        long pointsInsideCircle = 0;

        for (long i = 0; i < numPoints; i++) {
            double x = random.nextDouble() * 2 - 1; // x in [-1, 1]
            double y = random.nextDouble() * 2 - 1; // y in [-1, 1]
            if (x * x + y * y <= 1) { // Inside circle
                pointsInsideCircle++;
            }
        }

        return 4.0 * pointsInsideCircle / numPoints;
    }

    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException {
        AtomicLong pointsInsideCircle = new AtomicLong(0);
        Thread[] threads = new Thread[numThreads];
        long pointsPerThread = numPoints / numThreads;
        long remainingPoints = numPoints % numThreads;

        // Distribute points among threads using try-with-resources
        try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            for (int i = 0; i < numThreads; i++) {
                long pointsForThisThread = pointsPerThread + (i == 0 ? remainingPoints : 0);
                threads[i] = new Thread(new PiTask(pointsForThisThread, pointsInsideCircle));
                executor.execute(threads[i]); // Use executor to manage threads
            }

            // Wait for all threads to complete
            for (Thread thread : threads) {
                thread.join();
            }
        } // ExecutorService automatically shuts down here

        return 4.0 * pointsInsideCircle.get() / numPoints;
    }

    // Task for each thread
    private static class PiTask implements Runnable {
        private final long numPoints;
        private final AtomicLong pointsInsideCircle;

        public PiTask(long numPoints, AtomicLong pointsInsideCircle) {
            this.numPoints = numPoints;
            this.pointsInsideCircle = pointsInsideCircle;
        }

        @Override
        public void run() {
            Random random = new Random();
            long localCount = 0;

            for (long i = 0; i < numPoints; i++) {
                double x = random.nextDouble() * 2 - 1;
                double y = random.nextDouble() * 2 - 1;
                if (x * x + y * y <= 1) {
                    localCount++;
                }
            }

            pointsInsideCircle.addAndGet(localCount);
        }
    }
}