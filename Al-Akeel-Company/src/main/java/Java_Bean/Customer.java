package Java_Bean;

import Entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Stateless
@Path("/customer")
@Produces("application/json")
@Consumes("application/json")
public class Customer {
    @PersistenceContext
    EntityManager entity_manager;

    private User customerObj = null;

    @POST
    @Path("/register")
    public String Register(User customer) {
        customer.setRole(User.UserRole.customer);
        entity_manager.persist(customer);
        return "Customer ("+customer.getUsername()+")  registered successfully";
    }

    @POST
    @Path("/signin")
    public String signIn(User Customer) {

        Query customer_query = entity_manager.createQuery("select y from User y where y.username='" + Customer.getUsername() + "' and y.password='" + Customer.getPassword() + "'");
        if (customer_query.getResultList().isEmpty())
            return null;
        else {

            User loggedInCustomer = (User) customer_query.getSingleResult();
            if (loggedInCustomer.getRole().equals(User.UserRole.customer)){
                this.customerObj = loggedInCustomer;
                return "Customer ("+loggedInCustomer.getUsername()+")  Logged In successfully";

            }

            else
                return null;
        }
    }


    @POST
    @Path("/create_new_order")
    public String createNewOrder(List<Meal> all_meals) {
        Query new_order_query;
        OrderClass orderClassObj = new OrderClass();
        entity_manager.persist(orderClassObj);
        String restaurant = null;
        ArrayList<Meal> mealsList = new ArrayList<>();
        double total_price = 0;
        if (all_meals == null || all_meals.isEmpty()) {
            return "There is no meals please add some meals";
        }

        for (Meal mealObj: all_meals) {
            new_order_query = entity_manager.createQuery("select f from Meal f where f.meal_name='" + mealObj.getMeal_name() + "'");
            if (new_order_query.getResultList().isEmpty()) {
                return "Meal:" + mealObj.getMeal_name() + "not found";
            }

            Meal meal = (Meal) new_order_query.getSingleResult();
            total_price += meal.getMeal_price();
            meal.setOrderClass(orderClassObj);
            mealsList.add(meal);
            entity_manager.merge(meal);

        }

        restaurant = mealsList.get(0).getRestaurant().getRestaurant_name();

        for (Meal meal:mealsList) {
            if(meal.getRestaurant().getRestaurant_name() != restaurant) {

                return "This group of meals aren't from the same restaurant";

            }
        }

        orderClassObj.setOrder_items(mealsList);

        new_order_query = entity_manager.createQuery("select l from Restaurant l join fetch l.mealsLists where l.restaurant_name='" + restaurant + "'");
        Restaurant restaurantObj = (Restaurant) new_order_query.getSingleResult();
        orderClassObj.setRestaurant_object(restaurantObj);

        orderClassObj.setCustomer_Id(this.customerObj.getId());
        new_order_query = entity_manager.createQuery("select x from Runner x");
        if (new_order_query.getResultList().isEmpty()) {

            return "There is no runners available";
        }

        List<Runner> runners_list = new_order_query.getResultList();

        Random random = new Random();
        Runner runnerObj = runners_list.get(random.nextInt(runners_list.size()));
        runnerObj.setState(Runner.RunnerState.busy);
        entity_manager.merge(runnerObj);
        orderClassObj.setDelivery_cost(runnerObj.getDelivery_cost());

        total_price += runnerObj.getDelivery_cost();
        orderClassObj.setOrder_State(OrderClass.OrderState.Under_preparation);
        orderClassObj.setRunner_object(runners_list.get(random.nextInt(runners_list.size())));
        orderClassObj.setOrder_date(LocalDate.now().toString());
        orderClassObj.setTotal_Price(total_price);
        entity_manager.merge(orderClassObj);

        return "Order of Id ( "+orderClassObj.getOrder_id()+" )created successfully";
    }

    @GET
    @Path("/get_order/{id}")
    public List<OrderClass> getOrderById(@PathParam("id") long id) {
        Query order_query = entity_manager.createQuery("select o from OrderClass o where o.customer_Id=" + id);
        if (order_query.getResultList().isEmpty()) {
            return null;
        }
        return (List<OrderClass>) order_query.getResultList();

    }

    @GET
    @Path("/get_restaurants")
    public List<Restaurant> getRestaurants() {
        Query restaurant_query = entity_manager.createQuery("select r FROM Restaurant r");
        return restaurant_query.getResultList();

    }

    @PUT
    @Path("/cancel_order/{id}")
    public String cancel(@PathParam("id") long id) {
        OrderClass orderClassObj = entity_manager.find(OrderClass.class, id);

        orderClassObj.setOrder_State(OrderClass.OrderState.canceled);
        entity_manager.merge(orderClassObj);

        return "Order of Id ( "+orderClassObj.getOrder_id()+" ) is cancelled successfully";
    }

    @PUT
    @Path("/update_order/{id}")
    public String updateOrder(@PathParam("id") long id, List<Meal> all_meals) {
        OrderClass orderClassObj = entity_manager.find(OrderClass.class, id);

        if (orderClassObj == null) {
            return "Order of Id ( "+orderClassObj.getOrder_id()+" ) not found";
        }

        if (!orderClassObj.getOrder_State().equals(OrderClass.OrderState.Under_preparation)) {
            return "Order of Id ( "+orderClassObj.getOrder_id()+" ) is under preparation";
        }

        for (Meal mealObj: orderClassObj.getOrder_items()) {
        	mealObj.setOrderClass(null);
        	entity_manager.merge(mealObj);
        }

        orderClassObj.setOrder_items(null);
        entity_manager.merge(orderClassObj);
        ArrayList<Meal> mealsList = new ArrayList<>();

        if (all_meals != null) {
            for (Meal mealObj2 : all_meals) {
                Query query1 = entity_manager.createQuery("select m from Meal m where m.meal_name='" + mealObj2.getMeal_name() + "'");
                if (query1.getResultList().isEmpty())
                    return "The item: "+ mealObj2.getMeal_name() + " not found";
                Meal mealObj3 = (Meal) query1.getSingleResult();
                mealObj3.setOrderClass(orderClassObj);
                mealsList.add(mealObj3);
                entity_manager.merge(mealObj3);
            }
            orderClassObj.setOrder_items(mealsList);

        }
        else {
            return "There is no items added";
        }

        entity_manager.merge(orderClassObj);
        return "Order of Id ( "+orderClassObj.getOrder_id()+" ) updated successfully";
    }
}
