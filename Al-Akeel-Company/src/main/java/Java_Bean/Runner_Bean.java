package Java_Bean;

import Entity.OrderClass;
import Entity.Runner;
import Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;

@Stateless
@Path("/runner")
@Produces("application/json")
@Consumes("application/json")
public class Runner_Bean {
    @PersistenceContext
    EntityManager entity_manager;
    private Runner runnerObj;

    @POST
    @Path("/register")
    public String Register(Runner runner_man) {
    	runner_man.setState(Runner.RunnerState.available);
    	runner_man.setRole(User.UserRole.runner);
    	runner_man.setTrips_number(0);
    	entity_manager.persist(runner_man);
        return "runner  ("+runner_man.getUsername()+")  registered successfully";
    }
    @POST
    @Path("/signin")
    public String signIn(Runner runner_man) {
        Query runner_query;
        runner_query = entity_manager.createQuery("select r from User r where r.username='" + runner_man.getUsername() + "' and r.password='" + runner_man.getPassword() + "'");
        if (runner_query.getResultList().isEmpty())
            return null;
        else {
            Runner loggedInRunner = (Runner) runner_query.getSingleResult();
            if (loggedInRunner.getRole().equals(User.UserRole.runner)){
                this.runnerObj = loggedInRunner;
                return "runner  ("+loggedInRunner.getUsername()+ ")  Logged In successfully";
            }
            else
                return null;
        }
    }

    @GET
    @Path("/all_runner_trips/{id}")
    public String getAllTripsByRunnerId(@PathParam("id") long id) {
        Runner runnerInstance = entity_manager.find(Runner.class, id);
        return "Number of trips of "+runnerInstance.getUsername()+" = "+runnerInstance.getTrips_number();
    }

    @POST
    @Path("/order_info/{id}")
    public String OrderInfo(@PathParam("id") long id) {
        OrderClass orderClassObj = entity_manager.find(OrderClass.class, id);
        if (orderClassObj.getOrder_State().equals(OrderClass.OrderState.canceled))
            return "Order of Id("+orderClassObj.getOrder_id()+") has been cancelled";
        orderClassObj.setOrder_State(OrderClass.OrderState.Delivered);
        this.runnerObj.setState(Runner.RunnerState.available);
        this.runnerObj.setTrips_number(this.runnerObj.getTrips_number()+1);
        entity_manager.merge(orderClassObj);
        entity_manager.merge(this.runnerObj);
        return "Order of Id("+orderClassObj.getOrder_id()+") has been completed";
    }


}
