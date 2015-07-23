/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package eu.unifiedviews.commons.dao.view;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

/**
 * @author Škoda Petr
 */
@Entity
@Table(name = "exec_view")
public class ExecutionView implements DataObject {

    /**
     * Unique id of pipeline execution.
     */
    @Id
    private Long id;

    /**
     * Actual status for executed pipeline.
     */
    @Enumerated(EnumType.ORDINAL)
    private PipelineExecutionStatus status;

    /**
     * Id of pipeline being executed.
     */
    @Column(name = "pipeline_id")
    private String pipelineId;

    /**
     * Name of pipeline being executed.
     */
    @Column(name = "pipeline_name")
    private String pipelineName;

    /**
     * Run in debug mode?
     */
    @Column(name = "debug_mode")
    private boolean isDebugging;

    /**
     * Timestamp when this execution started, or null.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_start")
    private Date start;

    /**
     * Timestamp when this execution started, or null.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_end")
    private Date end;

    /**
     * Schedule that planned this execution. Null for execution created by user.
     */
    @Column(name = "schedule_id")
    private Integer schedule;

    /**
     * Execution owner.
     */
    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_full_name")
    private String ownerFullName;

    @Column(name = "user_actor_name")
    private String userActorName;

    /**
     * True if pipeline should or has been stopped on user request.
     */
    @Column(name = "stop")
    private boolean stop;

    /**
     * Timestamp when this execution was last changed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_last_change")
    private Date lastChange;

    @Override
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

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public boolean isDebugging() {
        return isDebugging;
    }

    public void setDebugging(boolean isDebugging) {
        this.isDebugging = isDebugging;
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

    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        this.schedule = schedule;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public String getUserActorName() {
        return this.userActorName;
    }

    public void setUserActorName(String userActorName) {
        this.userActorName = userActorName;
    }

    public String getOwnerFullName() {
        return this.ownerFullName;
    }

    public void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }

    /**
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
