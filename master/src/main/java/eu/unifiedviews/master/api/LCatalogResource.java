package eu.unifiedviews.master.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import eu.comsode.libraries.jckan.CkanException;
import eu.comsode.libraries.jckan.CkanRepository;
import eu.comsode.libraries.jckan.model.Resource;
import eu.unifiedviews.master.model.ApiException;
import eu.unifiedviews.master.model.LCatalogDTO;
import eu.unifiedviews.master.model.LCatalogResponseDTO;

@Component
@Path("/resources")
public class LCatalogResource {

    @Autowired
    private AppConfig appConfig;

//    @POST
//    @Path("/{datasetId}")
//    @Produces({ MediaType.APPLICATION_JSON })
//    @Consumes({ MediaType.APPLICATION_JSON })
//    public List<LCatalogDTO> postLCatalog(@PathParam("datasetId") String datasetId, LCatalogDTO lCatalogDTO) {
//        String ckanLocation = appConfig.getString(ConfigProperty.CKAN_LOCATION);
//        String ckanApiKey = appConfig.getString(ConfigProperty.CKAN_API_KEY);
//        CkanRepository ckanRepository = new CkanRepository(ckanLocation, ckanApiKey);
////      Resource resource = ckanRepository.getResourceDAO().read("2337edaf-56ca-40f1-bbce-59518dd88ce4");
//
//        Resource resource1 = new Resource();
//        resource1.setUrl(lCatalogDTO.getUri());
//        resource1.setName(lCatalogDTO.getName());
//        Resource resource;
//        try {
//            resource = ckanRepository.getResourceDAO().create(datasetId, resource1, null);
//        } catch (CkanException ex) {
//            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "");
//        }
////        ckanRepository.getResourceDAO().delete(resource.getId());
//        System.out.println(resource.toString());
//        return Collections.<LCatalogDTO> emptyList();
//    }

    @POST
    @Path("/{datasetId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public List<LCatalogResponseDTO> insertAndUpdateResources(@PathParam("datasetId") String datasetId, List<LCatalogDTO> lCatalogDTOs) {
        String ckanLocation = appConfig.getString(ConfigProperty.CKAN_LOCATION);
        String ckanApiKey = appConfig.getString(ConfigProperty.CKAN_API_KEY);
        CkanRepository ckanRepository = new CkanRepository(ckanLocation, ckanApiKey);
        Resource resource = null;
        List<Resource> existingResources = null;
        try {
            existingResources = ckanRepository.getResourceDAO().list(datasetId);
        } catch (CkanException ex) {
            ckanRepository.closeClient();
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "");
        }

        List<LCatalogResponseDTO> result = new ArrayList<LCatalogResponseDTO>();

        List<Resource> resourcesToUpdate = new ArrayList<Resource>();
        List<Resource> resourcesToCreate = new ArrayList<Resource>();
        boolean updFlag = false;
        for (LCatalogDTO lCatalogDTO : lCatalogDTOs) {
            updFlag = false;
            for (Resource existingResource : existingResources) {
                if (lCatalogDTO.getName().equals(existingResource.getName())) {
                    existingResource.setUrl(lCatalogDTO.getUri());
                    resourcesToUpdate.add(existingResource);
                    updFlag = true;
                    break;
                }
            }
            if (!updFlag) {
                Resource resourceToCreate = new Resource();
                resourceToCreate.setUrl(lCatalogDTO.getUri());
                resourceToCreate.setName(lCatalogDTO.getName());
                resourcesToCreate.add(resourceToCreate);
            }

        }

        for (Resource updRes : resourcesToUpdate) {
            try {
                resource = ckanRepository.getResourceDAO().update(updRes, null);
                result.add(new LCatalogResponseDTO(updRes.getName(), updRes.getUrl(), false, true, true, null));
            } catch (CkanException cke) {
                result.add(new LCatalogResponseDTO(updRes.getName(), updRes.getUrl(), false, false, false, cke.getMessage()));
            }
        }
        for (Resource crtRes : resourcesToCreate) {
            try {
                resource = ckanRepository.getResourceDAO().create(datasetId, crtRes, null);
                result.add(new LCatalogResponseDTO(crtRes.getName(), crtRes.getUrl(), true, false, true, null));
            } catch (CkanException cke) {
                result.add(new LCatalogResponseDTO(crtRes.getName(), crtRes.getUrl(), false, false, false, cke.getMessage()));
            }
        }
        ckanRepository.closeClient();
        return result;
    }
}
