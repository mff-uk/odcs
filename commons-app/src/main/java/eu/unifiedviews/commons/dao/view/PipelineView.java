package eu.unifiedviews.commons.dao.view;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

/**
 * View for pipelines.
 *
 * @author Škoda Petr
 */
@XmlRootElement
@Entity()
@Table(name = "pipeline_view")
public class PipelineView implements Serializable, DataObject {

    @Id
    private Long id;

    /**
     * Human-readable pipeline name
     */
    @Column
    private String name;

    /**
     * Start of last pipeline execution.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_start")
    private Date start;

    /**
     * End of last pipeline execution.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_end")
    private Date end;

    /**
     * Status of last pipeline execution.
     */
    @Enumerated(EnumType.ORDINAL)
    private PipelineExecutionStatus status;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public PipelineExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineExecutionStatus status) {
        this.status = status;
    }

    /**
     *
     * @return Duration of last pipeline execution, -1 if no such execution exists.
     */
    public long getDuration() {
        if (start == null || end == null) {
            return -1l;
        } else {
            return end.getTime() - start.getTime();
        }
    }

}
