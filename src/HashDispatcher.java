import dk.brics.automaton.*;
import java.util.*;

/**
 * The HashDispatcher class takes care of creating, starting, and interrupting
 * threads dedicated to calculating a lot of tripcodes. It also provides
 * information about the number of tripcodes calculated per second.
 *
 * All methods are static and the class can and should not be initialized.
 */
public class HashDispatcher {
    /**
     * Private constructor to prevent initialization.
     */
    private HashDispatcher() {
    }

    /**
     * Creates and starts a number of threads searching for tripcodes contained
     * in the language recognized by the given RunAutomaton.
     * @param aut A RunAutomaton representing the language of desired tripcodes.
     * @param threads The number of threads to dispatch.
     */
    public static void dispatch(RunAutomaton aut, int threads) {
    // Set lastCheck to initialize the kTps counter.
        lastCheck = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
        // Find a suitable random origin to start searching from.
            String s = Long.toHexString((new Random()).nextLong());
            HashThread h = new HashThread(aut, s);
            HashDispatcher.threads.add(h);
            h.start();
        }
    }

    /**
     * Interrupts all running threads, effectively stopping the calculation of
     * tripcodes.
     */
    public static void killAll() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    // Reset the ticks in case the kTps counter should be polled.
    ticks = 0;
    }

    /**
     * This private class takes care of actually calculating tripcodes and a set
     * of instances of the class are maintaining by the HashDispatcher.
     */
    private static class HashThread extends Thread {

    /**
     * Creates a new HashThread searching for tripcodes in the language
     * specified by the given RunAutomaton.
     * @param runAut A RunAutomaton representing the language of desired tripcodes.
     * @param init An origin to start searching from.
     */
        public HashThread(RunAutomaton runAut, String init) {
            aut = runAut;
            pass = init;
        }

    /**
     * Continually calculates tripcodes and reports matches until
     * interrupted by the HashDispatcher.
     */
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
        // Calculate the tripcode of the current password.
                trip = Hash.getTripCode(pass);
        // If a match is found, report it.
        if (aut.run(trip)) {
            GUI.addTrip(pass, trip);
        }
        // Find the next password to calculate the tripcode of.
        // TODO: find a method that does not overlap with other threads.
                pass = trip;
        // Update the counter every 100 tripcodes.
                if ((ticks = ++ticks % 100) == 0) {
            HashDispatcher.tick100();
        }
            }
        }
        private String pass;
        private String trip;
        private RunAutomaton aut;
    private int ticks = 0;
    }

    /**
     * Returns the number of kilotrips calculated per second since last poll.
     */
    public static synchronized long getKtps() {
        long now = System.currentTimeMillis();
    // Use Math.max(now - lastCheck, 1) to avoid division by zero initially.
        long tps = ticks / (Math.max(now - lastCheck, 1));
        ticks = 0;
        lastCheck = now;
        return tps;
    }

    /**
     * Update the counter with 100 "ticks" representing 100 calculated trips.
     */
    private static synchronized void tick100() {
    ticks += 100;
    }

    // Keep track of ticks, time of last kTps poll, and the set of threads.
    private static long ticks = 0;
    private static long lastCheck = 0;
    private static Set<Thread> threads = new HashSet<Thread>();
}
