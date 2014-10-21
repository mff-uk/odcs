package eu.unifiedviews.master.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sk.eea.xxx.domain.Employee;

@Path("/employee")
public class EmployeeResource {
//    @Context
//    UriInfo uriInfo;
//
//    @Context
//    Request request;

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Employee getEmployee() {
        Employee employee = new Employee();
        return employee;
    }

}
