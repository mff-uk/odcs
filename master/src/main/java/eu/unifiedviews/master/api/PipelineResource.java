package eu.unifiedviews.master.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.eea.xxx.domain.PipelineDTO;
import sk.eea.xxx.domain.PipelineToDTOConverter;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;

@Component
@Path("/pipelines")
public class PipelineResource {
    @Autowired
    private PipelineFacade pipelineFacade;

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineDTO> getPipelines() {
        return PipelineToDTOConverter.convert(pipelineFacade.getAllPipelines());
    }

//    @GET
//    @Path("count")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getCount() {
//        int count = pipelineFacade.getAllPipelines().size();
//        return String.valueOf(count);
//    }

}
