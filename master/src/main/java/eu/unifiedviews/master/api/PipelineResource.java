package eu.unifiedviews.master.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import eu.unifiedviews.master.converter.PipelineExecutionDTOConverter;
import eu.unifiedviews.master.converter.PipelineExecutionEventToDTOConverter;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PipelineDTO createPipeline() {
        Pipeline pipeline = null;
        try {
            pipeline = pipelineFacade.createPipeline();
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline could not be created."));
            }
            pipelineFacade.save(pipeline);
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        return PipelineToDTOConverter.convert(pipeline);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineDTO> getPipelines() {
        List<Pipeline> pipelines = null;
        try {
            pipelines = pipelineFacade.getAllPipelines();
            if (pipelines == null) {
                throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }

        return PipelineToDTOConverter.convert(pipelines);
    }

    @GET
    @Path("/{pipelineid}")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineDTO getPipeline(@PathParam("pipelineid") String id) {
        Pipeline pipeline = null;
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", id));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", id));
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }

        return PipelineToDTOConverter.convert(pipeline);
    }

    @GET
    @Path("/{pipelineid}/schedules")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineScheduleDTO> getPipelineSchedules(@PathParam("pipelineid") String id) {
        Pipeline pipeline = null;
        List<Schedule> schedules = null;
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", id));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", id));
            }
            schedules = scheduleFacade.getSchedulesFor(pipeline);
            if (schedules == null) {
                throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "ScheduleFacade returned null!");
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }

        return ScheduleDTOConverter.convertToDTOs(schedules);
    }

    @GET
    @Path("/{pipelineid}/schedules/{scheduleid}")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineScheduleDTO getPipelineSchedule(@PathParam("pipelineid") String pipelineId, @PathParam("scheduleid") String scheduleId) {
        Pipeline pipeline = null;
        Schedule schedule = null;
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(scheduleId) || !StringUtils.isNumeric(scheduleId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid schedule ID", scheduleId));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
            }
            schedule = scheduleFacade.getSchedule(Long.parseLong(scheduleId));
            if (schedule == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline schedule with id=%s doesn't exist!", scheduleId));
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        if (schedule.getPipeline().getId().equals(pipeline.getId())) {
            return ScheduleDTOConverter.convertToDTO(schedule);
        } else {
            throw new ApiException(Response.Status.BAD_REQUEST, String.format("Schedule with id=%s is not for pipeline with id=%s!", scheduleId, pipelineId));
        }
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
        try {
            Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
            }
            Schedule schedule = scheduleFacade.getSchedule(Long.parseLong(scheduleId));
            if (schedule == null) {
                throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "ScheduleFacade returned null!");
            }
            if (schedule.getPipeline().getId().equals(pipeline.getId())) {
                List<Pipeline> afterPipelines = null;
                if (scheduleToUpdate.getAfterPipelines() != null) {
                    afterPipelines = new ArrayList<Pipeline>();
                    for (Long pipId : scheduleToUpdate.getAfterPipelines()) {
                        Pipeline loadedPip = pipelineFacade.getPipeline(pipId.longValue());
                        if (loadedPip != null) {
                            afterPipelines.add(loadedPip);
                        } else {
                            throw new ApiException(Response.Status.BAD_REQUEST, String.format("Pipeline with id=%d doesn't exist!", pipId));
                        }
                    }
                }
                ScheduleDTOConverter.convertFromDTO(scheduleToUpdate, afterPipelines, schedule);
                scheduleFacade.save(schedule);
                return ScheduleDTOConverter.convertToDTO(schedule);
            } else {
                throw new ApiException(Response.Status.BAD_REQUEST, String.format("Schedule with id=%s is not for pipeline with id=%s!", scheduleId, pipelineId));
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }

    }

    @GET
    @Path("/{pipelineid}/executions")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineExecutionDTO> getPipelineExecutions(@PathParam("pipelineid") String id) {
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", id));
        }
        try {
            Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", id));
            }
            List<PipelineExecution> executions = pipelineFacade.getExecutions(pipeline);
            if (executions == null) {
                throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
            }
            return PipelineExecutionDTOConverter.convert(executions);
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @GET
    @Path("/{pipelineid}/executions/{executionid}")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineExecutionDTO getPipelineExecution(@PathParam("pipelineid") String pipelineId, @PathParam("executionid") String executionId) {
        Pipeline pipeline = null;
        PipelineExecution execution = null;
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.BAD_REQUEST, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(executionId) || !StringUtils.isNumeric(executionId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline execution ID", executionId));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
            }
            execution = pipelineFacade.getExecution(Long.parseLong(executionId));
            if (execution == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline execution with id=%s doesn't exist!", executionId));
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }

        if (execution.getPipeline().getId().equals(pipeline.getId())) {
            return PipelineExecutionDTOConverter.convert(execution);
        } else {
            throw new ApiException(Response.Status.BAD_REQUEST, String.format("PipelineExecution with id=%s is not execution of pipeline with id=%s!", executionId, pipelineId));
        }
    }

    @POST
    @Path("/{pipelineid}/executions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PipelineExecutionDTO createPipelineExecution(@PathParam("pipelineid") String pipelineId, PipelineExecutionDTO newExecution) {
        PipelineExecution execution = null;
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        try {
            Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
            }

            execution = PipelineExecutionDTOConverter.createPipelineExecution(newExecution, pipeline);
            pipelineFacade.save(execution);
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
        return PipelineExecutionDTOConverter.convert(execution);
    }

    @GET
    @Path("/{pipelineid}/executions/{executionid}/events")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineExecutionEventDTO> getPipelineExecutionEvents(@PathParam("pipelineid") String pipelineId, @PathParam("executionid") String executionId) {
        Pipeline pipeline = null;
        PipelineExecution execution = null;
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(executionId) || !StringUtils.isNumeric(executionId)) {
            throw new ApiException(Response.Status.NOT_FOUND, String.format("ID=%s is not valid pipeline execution ID", executionId));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, String.format("Pipeline with id=%s doesn't exist!", pipelineId));
            }
            execution = pipelineFacade.getExecution(Long.parseLong(executionId));
            if (execution == null) {
                throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (RuntimeException exception) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }

        if (execution.getPipeline().getId().equals(pipeline.getId())) {
            List<MessageRecord> events = dpuFacade.getAllDPURecords(execution);
            return PipelineExecutionEventToDTOConverter.convert(events);
        } else {
            throw new ApiException(Response.Status.BAD_REQUEST, String.format("PipelineExecution with id=%s is not execution of pipeline with id=%s!", executionId, pipelineId));
        }
    }

}
