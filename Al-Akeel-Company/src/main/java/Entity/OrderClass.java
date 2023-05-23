package Entity;

import java.util.Collection;
import jakarta.persistence.*;
import java.io.Serializable;


@Entity
public class OrderClass implements Serializable {

	public enum OrderState {
    	canceled,
    	Under_preparation,
        Delivered
        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long order_id;

    @OneToMany(mappedBy = "orderClass", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Meal> Order_items;

    private double total_Price;

    private String order_date;

    private double delivery_cost;

    private long customer_Id;

    @OneToOne
    private Runner runner_object;

    @ManyToOne
    private Restaurant restaurant_object;

    private OrderState order_State;

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public Collection<Meal> getOrder_items() {
        return Order_items;
    }

    public void setOrder_items(Collection<Meal> order_items) {
        Order_items = order_items;
    }

    public double getTotal_Price() {
        return total_Price;
    }

    public void setTotal_Price(double total_Price) {
        this.total_Price = total_Price;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public double getDelivery_cost() {
        return delivery_cost;
    }

    public void setDelivery_cost(double delivery_cost) {
        this.delivery_cost = delivery_cost;
    }

    public long getCustomer_Id() {
        return customer_Id;
    }

    public void setCustomer_Id(long customer_Id) {
        this.customer_Id = customer_Id;
    }

    public Runner getRunner_object() {
        return runner_object;
    }

    public void setRunner_object(Runner runner_object) {
        this.runner_object = runner_object;
    }

    public Restaurant getRestaurant_object() {
        return restaurant_object;
    }

    public void setRestaurant_object(Restaurant restaurant_object) {
        this.restaurant_object = restaurant_object;
    }

    public OrderState getOrder_State() {
        return order_State;
    }

    public void setOrder_State(OrderState order_State) {
        this.order_State = order_State;
    }
}
