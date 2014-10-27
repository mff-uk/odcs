package eu.unifiedviews.master.model;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.scheduling.PeriodUnit;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleType;

public class PipelineScheduleDTO {
    private Long id;

    private String description;

    private boolean enabled;

    private ScheduleType scheduleType;

    private String firstExecution;

    private String lastExecution;

    private List<Long> afterPipelines;

    private boolean justOnce;

    private Integer period;

    private PeriodUnit periodUnit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getFirstExecution() {
        return firstExecution;
    }

    public void setFirstExecution(String firstExecution) {
        this.firstExecution = firstExecution;
    }

    public String getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(String lastExecution) {
        this.lastExecution = lastExecution;
    }

    public List<Long> getAfterPipelines() {
        return afterPipelines;
    }

    public void setAfterPipelines(List<Long> afterPipelines) {
        this.afterPipelines = afterPipelines;
    }

    public boolean isJustOnce() {
        return justOnce;
    }

    public void setJustOnce(boolean justOnce) {
        this.justOnce = justOnce;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public PeriodUnit getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(PeriodUnit periodUnit) {
        this.periodUnit = periodUnit;
    }

}
