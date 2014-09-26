package cz.cuni.mff.xrg.odcs.commons.app;

public enum ScheduledJobsPriority {
    IGNORE(0), HIGHEST(3), MEDIUM(2), LOWEST(1);

    private final long priority;

    ScheduledJobsPriority(long priority) {
        this.priority = priority;
    }

    public long getValue() {
        return priority;
    }

}
