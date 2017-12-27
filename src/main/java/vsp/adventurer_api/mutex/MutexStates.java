package vsp.adventurer_api.mutex;

public enum MutexStates {

    /**
     * Does not hold a lock and does not want a lock.
     */
    RELEASED("released"),

    /**
     * Does not hold a lock but wants it.
     */
    WANTING("wanting"),

    /**
     * Does hold a lock and may enter the critical section.
     */
    HELD("held");

    private final String str;

    MutexStates(String str) {
        this.str = str;
    }

    public String asString() {
        return str;
    }
}
