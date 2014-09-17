package cz.cuni.mff.xrg.odcs.commons.app;

public class JobsTypes {
    // Jobs types are defined by priority number.
    // PipelineExecution is unscheduled when priority is 1 ...
    // PipelineExecution is scheduled when priority is > 1 ...

    public static final long RUNNING = -1;

    // run from frontend - unscheduled pipeline
    public static final long UNSCHEDULED = 1;

    // reserved for jobs with the highest priority
    public static final long MAX_PRIORITY = 0;

}
