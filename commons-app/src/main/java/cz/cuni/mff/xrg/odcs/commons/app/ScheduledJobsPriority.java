package cz.cuni.mff.xrg.odcs.commons.app;

public enum ScheduledJobsPriority {
    // from the highest to the lowest priority
    A(1), B(2), C(3), D(4), E(5), F(6), G(7), H(8), I(9), J(10);

    private final long priority;

    ScheduledJobsPriority(long priority) {
        this.priority = priority;
    }

    public long getValue() {
        return priority;
    }

}
