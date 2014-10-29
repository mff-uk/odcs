package eu.unifiedviews.master.model;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

public class PipelineExecutionDTO {
    private Long id;

    private PipelineExecutionStatus status;

    private boolean isDebugging;

    private Long orderNumber;

    private String start;

    private String end;

    private Long schedule;

    private boolean silentMode;

    private boolean stop;

    private String lastChange;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PipelineExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineExecutionStatus status) {
        this.status = status;
    }

    public boolean isDebugging() {
        return isDebugging;
    }

    public void setDebugging(boolean isDebugging) {
        this.isDebugging = isDebugging;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Long getSchedule() {
        return schedule;
    }

    public void setSchedule(Long schedule) {
        this.schedule = schedule;
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public String getLastChange() {
        return lastChange;
    }

    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }

}
