package eu.unifiedviews.master.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import eu.unifiedviews.master.converter.PipelineExecutionEventToDTOConverter;
import eu.unifiedviews.master.converter.PipelineExecutionToDTOConverter;
import eu.unifiedviews.master.converter.PipelineToDTOConverter;
import eu.unifiedviews.master.converter.ScheduleDTOConverter;
import eu.unifiedviews.master.model.ApiException;
import eu.unifiedviews.master.model.PipelineDTO;
import eu.unifiedviews.master.model.PipelineExecutionDTO;
import eu.unifiedviews.master.model.PipelineExecutionEventDTO;
import eu.unifiedviews.master.model.PipelineScheduleDTO;

@Component
@Path("/pipelines")
public class PipelineResource {
    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private ScheduleFacade scheduleFacade;

    @Autowired
    private DPUFacade dpuFacade;

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineDTO> getPipelines() {
        List<Pipeline> pipelines = pipelineFacade.getAllPipelines();
        if (pipelines == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
        }
        return PipelineToDTOConverter.convert(pipelines);
    }

    @GET
    @Path("/{pipelineid}")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineDTO getPipeline(@PathParam("pipelineid") String id) {
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", id));
        }
        Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
        if (pipeline == null) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", id));
        }
        return PipelineToDTOConverter.convert(pipeline);
    }

    @GET
    @Path("/{pipelineid}/schedules")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineScheduleDTO> getPipelineSchedules(@PathParam("pipelineid") String id) {
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", id));
        }
        Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
        if (pipeline == null) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", id));
        }
        List<Schedule> schedules = scheduleFacade.getSchedulesFor(pipeline);
        if (schedules == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "ScheduleFacade returned null!");
        }
        return ScheduleDTOConverter.convertToDTOs(schedules);
    }

    @GET
    @Path("/{pipelineid}/schedules/{scheduleid}")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineScheduleDTO getPipelineSchedule(@PathParam("pipelineid") String pipelineId, @PathParam("scheduleid") String scheduleId) {
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(scheduleId) || !StringUtils.isNumeric(scheduleId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid schedule ID", scheduleId));
        }
        Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
        if (pipeline == null) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
        }
        List<Schedule> schedules = scheduleFacade.getSchedulesFor(pipeline);
        if (schedules == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "ScheduleFacade returned null!");
        }
        for (Schedule schedule : schedules) {
            if (schedule.getId() == Long.parseLong(scheduleId)) {
                return ScheduleDTOConverter.convertToDTO(schedule);
            }
        }
        throw new ApiException(Response.Status.NOT_FOUND, String.format("Schedule with id=%s doesn't exist for pipeline id=%s!", scheduleId, pipelineId));
    }

    @PUT
    @Path("/{pipelineid}/schedules/{scheduleid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PipelineScheduleDTO updatePipelineSchedule(@PathParam("pipelineid") String pipelineId, @PathParam("scheduleid") String scheduleId, PipelineScheduleDTO scheduleToUpdate) {
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(scheduleId) || !StringUtils.isNumeric(scheduleId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid schedule ID", scheduleId));
        }
        Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
        if (pipeline == null) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
        }
        List<Schedule> schedules = scheduleFacade.getSchedulesFor(pipeline);
        if (schedules == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "ScheduleFacade returned null!");
        }
        for (Schedule schedule : schedules) {
            if (schedule.getId() == Long.parseLong(scheduleId)) {
                ScheduleDTOConverter.convertFromDTO(scheduleToUpdate, schedule);
                try {
                    scheduleFacade.save(schedule);
                } catch (Exception e) {
                    throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
                }
                return ScheduleDTOConverter.convertToDTO(schedule);
            }
        }
        throw new ApiException(Response.Status.NOT_FOUND, String.format("Schedule with id=%s doesn't exist for pipeline id=%s!", scheduleId, pipelineId));

    }

    @GET
    @Path("/{pipelineid}/executions")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineExecutionDTO> getPipelineExecutions(@PathParam("pipelineid") String id) {
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", id));
        }
        Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
        if (pipeline == null) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", id));
        }
        List<PipelineExecution> executions = pipelineFacade.getExecutions(pipeline);
        if (executions == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
        }
        return PipelineExecutionToDTOConverter.convert(executions);
    }

    @GET
    @Path("/{pipelineid}/executions/{executionid}")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineExecutionDTO getPipelineExecution(@PathParam("pipelineid") String pipelineId, @PathParam("executionid") String executionId) {
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.BAD_REQUEST, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(executionId) || !StringUtils.isNumeric(executionId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline execution ID", executionId));
        }
        Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
        if (pipeline == null) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
        }
        List<PipelineExecution> executions = pipelineFacade.getExecutions(pipeline);
        if (executions == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
        }
        for (PipelineExecution execution : executions) {
            if (execution.getId() == Long.parseLong(executionId)) {
                return PipelineExecutionToDTOConverter.convert(execution);
            }
        }
        throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline execution with id=%s doesn't exist for pipeline id=%s!", executionId, pipelineId));
    }

    @GET
    @Path("/{pipelineid}/executions/{executionid}/events")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineExecutionEventDTO> getPipelineExecutionEvents(@PathParam("pipelineid") String pipelineId, @PathParam("executionid") String executionId) {
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(executionId) || !StringUtils.isNumeric(executionId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline execution ID", executionId));
        }
        Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
        if (pipeline == null) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
        }
        List<PipelineExecution> executions = pipelineFacade.getExecutions(pipeline);
        if (executions == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
        }
        for (PipelineExecution execution : executions) {
            if (execution.getId() == Long.parseLong(executionId)) {
                List<MessageRecord> events = dpuFacade.getAllDPURecords(execution);
                return PipelineExecutionEventToDTOConverter.convert(events);
            }
        }
        throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline execution with id=%s doesn't exist for pipeline id=%s!", executionId, pipelineId));
    }

}
