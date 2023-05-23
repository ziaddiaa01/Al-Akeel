package Java_Bean;

import Entity.OrderClass;
import Entity.Meal;
import Entity.Restaurant;
import Entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import java.util.List;

@Stateless
@Path("/owner")
@Produces("application/json")
@Consumes("application/json")
public class Owner {
    @PersistenceContext
    EntityManager entity_manager;
    private User ownerObj = null;
    private Restaurant restaurantObj = null;
    @POST
    @Path("/register")
    public String Register(User owner_man) {
    	owner_man.setRole(User.UserRole.owner);
        entity_manager.persist(owner_man);
        return "Owner ("+owner_man.getUsername()+")  registered successfully";
    }
    @POST
    @Path("/signin")
    public String signIn(User owner_man) {
        Query owner_query;
        owner_query  = entity_manager.createQuery("select o from User o where o.username='" + owner_man.getUsername() + "' and o.password='" + owner_man.getPassword() + "'");

        if (owner_query.getResultList().isEmpty())
            return null;

        else {
            User loggedInOwner = (User) owner_query.getSingleResult();

            if (loggedInOwner.getRole().equals(User.UserRole.owner)){
                this.ownerObj = loggedInOwner;
                return "Owner ("+loggedInOwner.getUsername()+")  Logged In successfully";
            }
            else
                return null;
        }
    }

    @POST
    @Path("/add-menu")
    public String createNewMenu(List<Meal> mealsLists) {

        if (this.restaurantObj == null) {
            return "Restaurant isn't created yet";
        }

        if (this.restaurantObj.getMealsLists() != null) {
            return "The menu was created already";
        }

        for (Meal meal1:mealsLists) {
            meal1.setRestaurant(this.restaurantObj);

            entity_manager.persist(meal1);
        }
        this.restaurantObj.setMealsLists(mealsLists);
        entity_manager.merge(this.restaurantObj);

        return "The new Menu is created";
    }

    @POST
    @Path("/add-restaurant")
    public String addRestaurant(Restaurant new_restaurant) {
        if (this.restaurantObj != null) {

            return "Restaurant was created already";
        }
        new_restaurant.setOwner_Id(new_restaurant.getId());

        entity_manager.persist(new_restaurant);
        this.restaurantObj = new_restaurant;

        return "Restaurant "+new_restaurant.getRestaurant_name()+" is added successfully";
    }


    @PUT
    @Path("/update-restaurant-menu")
    public String updateRestaurant(List<Meal> mealsLists) {
        if (this.restaurantObj == null) {
            return "restaurant isn't created yet";
        }
        for (Meal meal2: this.restaurantObj.getMealsLists()) {
            meal2.setRestaurant(null);
            entity_manager.merge(meal2);
        }
        this.restaurantObj.setMealsLists(null);
        entity_manager.merge(this.restaurantObj);
        if(mealsLists != null) {
            for (Meal meal3 : mealsLists) {
                meal3.setRestaurant(this.restaurantObj);
                entity_manager.merge(meal3);
            }
            this.restaurantObj.setMealsLists(mealsLists);
        } else {
            return "There is no menu, you need to create one first";
        }
        entity_manager.merge(this.restaurantObj);
        return "Restaurant is updated ";
    }
        @GET
    @Path("/restaurant_details/{id}")
    public Restaurant getRestaurantDetailsById(@PathParam("id") int id) {
        Query restaurant_query;
        restaurant_query  = entity_manager.createQuery("select r from Restaurant r JOIN FETCH r.mealsLists where r.id=" + id);
        if (restaurant_query.getResultList().isEmpty())
            return null;

        return (Restaurant) restaurant_query.getSingleResult();
    }

    @GET
    @Path("/create_restaurant_report/{id}")
    public String createRestaurantReport(@PathParam("id") int id) {
        Query restaurant_query = entity_manager.createQuery("select r from OrderClass r where r.restaurant_object=" + id);
        if (restaurant_query.getResultList().isEmpty()) {
            return "Restaurant not found";
        }

        List<OrderClass> ordersLists = restaurant_query.getResultList();
        Query create_restaurant_query = entity_manager.createQuery("select r from Restaurant r where r.id=" + id);

        Restaurant restaurantObj = (Restaurant) create_restaurant_query.getSingleResult();
        String restaurant_name = restaurantObj.getRestaurant_name();

        double total_orders = 0;
        int completed_orders = 0;
        int canceled_orders = 0;

        for (int i=0; i<ordersLists.size(); i++) {
            if (ordersLists.get(i).getOrder_State().equals(OrderClass.OrderState.Delivered)) {
            	total_orders += ordersLists.get(i).getTotal_Price();
            	completed_orders += 1;
            }
            else if (ordersLists.get(i).getOrder_State().equals(OrderClass.OrderState.canceled)) {
            	canceled_orders += 1;
            }
        }
        return "Restaurant:  "+ restaurant_name+"\n" +"Total earnings:  "+total_orders+"\n" +"Completed orders:  "+completed_orders+"\n" +"Canceled orders:  "+canceled_orders+"\n";
    }


}