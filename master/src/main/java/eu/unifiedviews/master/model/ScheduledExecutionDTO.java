package eu.unifiedviews.master.model;

public class ScheduledExecutionDTO {

    private String start;

    private Long schedule;

    public ScheduledExecutionDTO(String start, Long schedule) {
        super();
        this.start = start;
        this.schedule = schedule;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Long getSchedule() {
        return schedule;
    }

    public void setSchedule(Long schedule) {
        this.schedule = schedule;
    }

}
