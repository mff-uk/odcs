package eu.unifiedviews.master.api;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import eu.comsode.libraries.jckan.CkanException;
import eu.comsode.libraries.jckan.CkanRepository;
import eu.comsode.libraries.jckan.model.Resource;
import eu.unifiedviews.master.model.ApiException;
import eu.unifiedviews.master.model.LCatalogDTO;

@Component
@Path("/resources")
public class LCatalogResource {

    @POST
    @Path("/{datasetId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public List<LCatalogDTO> postLCatalog(@PathParam("datasetId") String datasetId, LCatalogDTO lCatalogDTO) {
        CkanRepository ckanRepository = new CkanRepository("http://192.168.3.131:5000/api/3/action/", "c0b9f02a-6aff-4329-83c3-6686e14b4b5d");
//      Resource resource = ckanRepository.getResourceDAO().read("2337edaf-56ca-40f1-bbce-59518dd88ce4");

        Resource resource1 = new Resource();
        resource1.setUrl(lCatalogDTO.getUri());
        resource1.setName(lCatalogDTO.getName());
        Resource resource;
        try {
            resource = ckanRepository.getResourceDAO().create(datasetId, resource1, null);
        } catch (CkanException ex) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "");
        }
//        ckanRepository.getResourceDAO().delete(resource.getId());
        System.out.println(resource.toString());
        return Collections.<LCatalogDTO> emptyList();
    }
}
