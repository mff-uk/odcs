package eu.unifiedviews.master.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import eu.unifiedviews.master.converter.PipelineToDTOConverter;
import eu.unifiedviews.master.converter.ScheduleToDTOConverter;
import eu.unifiedviews.master.model.ApiException;
import eu.unifiedviews.master.model.PipelineDTO;
import eu.unifiedviews.master.model.PipelineScheduleDTO;

@Component
@Path("/pipelines")
public class PipelineResource {
    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private ScheduleFacade scheduleFacade;

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
    @Path("/{pipelineid}/schedules")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineScheduleDTO> getPipelineSchedules(@PathParam("pipelineid") Long id) {
        Pipeline pipeline = pipelineFacade.getPipeline(id.longValue());
        if (pipeline == null) {
            throw new ApiException(Response.Status.BAD_REQUEST, String.format("Pipeline with id=%d doesn't exist!", id));
        }
        List<Schedule> schedules = scheduleFacade.getSchedulesFor(pipeline);
        if (schedules == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "ScheduleFacade returned null!");
        }
        return ScheduleToDTOConverter.convert(schedules);
    }

}
