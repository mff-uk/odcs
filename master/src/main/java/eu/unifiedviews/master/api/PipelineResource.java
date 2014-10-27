package eu.unifiedviews.master.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import eu.unifiedviews.master.converter.PipelineToDTOConverter;
import eu.unifiedviews.master.model.ApiException;
import eu.unifiedviews.master.model.PipelineDTO;

@Component
@Path("/pipelines")
public class PipelineResource {
    @Autowired
    private PipelineFacade pipelineFacade;

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineDTO> getPipelines() {
        List<Pipeline> pipelines = pipelineFacade.getAllPipelines();
        if (pipelines == null) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "PipelineFacade returned null!");
        }
        return PipelineToDTOConverter.convert(pipelines);
    }

//    @GET
//    @Path("/{pipelineid}")
//    @Produces({ MediaType.APPLICATION_JSON })
//    public PipelineDTO getPipeline(@PathParam("pipelineid") Long id) {
//        Pipeline pip = pipelineFacade.getPipeline(id.longValue());
//        if (pip == null) {
//            throw new ApiException(Response.Status.BAD_REQUEST, String.format("Pipeline with id=%d doesn't exist!", id));
//        }
//        return PipelineToDTOConverter.convert(pip);
//    }

}
