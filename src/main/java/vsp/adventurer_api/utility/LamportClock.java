package vsp.adventurer_api.utility;

import org.apache.log4j.Logger;

public class LamportClock {
    private static final Logger LOG = Logger.getLogger(LamportClock.class);

    /**
     * Holds the value for the next request.
     */
    private int localTime;

    public LamportClock() {
        localTime = 0;
    }

    /**
     * Shows current logical time.
     */
    public int watch() {
        return localTime - 1;
    }

    /**
     * Returns new Lamport-time and increase the time for the next request.
     *
     * @return new Lamport-time.
     */
    public synchronized int getAndIncrease() {
        return localTime++;
    }

    /**
     * Returns new Lamport-time and increase the time for the next request.
     *
     * @param msg For logging.
     * @return new Lamport-time.
     */
    public synchronized int getAndIncrease(String msg) {
        int time = getAndIncrease();
        LOG.info("[" + time + "] >>> " + msg);
        return time;
    }

    /**
     * Increases logical time.
     *
     * @param msg For logging.
     */
    public synchronized void increase(String msg) {
        getAndIncrease(msg);
    }

    /**
     * max(56,60)+1 = 61
     *
     * @param receivedTIme Time from the requesting service.
     */
    public synchronized int compareAndIncrease(int receivedTIme) {
        localTime = Math.max(localTime, receivedTIme) + 1;
        return getAndIncrease();
    }

}
