package Entity;

import jakarta.persistence.*;

@Entity
public class Runner extends User{

    public enum RunnerState {
        busy,
        available
    }


    @OneToOne
    private OrderClass orderClass;

    private RunnerState state;

    private double delivery_cost;

    private int trips_number;

    public OrderClass getOrderClass() {
        return orderClass;
    }

    public void setOrderClass(OrderClass orderClass) {
        this.orderClass = orderClass;
    }

    public RunnerState getState() {
        return state;
    }

    public void setState(RunnerState state) {
        this.state = state;
    }

    public double getDelivery_cost() {
        return delivery_cost;
    }

    public void setDelivery_cost(double delivery_cost) {
        this.delivery_cost = delivery_cost;
    }

    public int getTrips_number() {
        return trips_number;
    }

    public void setTrips_number(int trips_number) {
        this.trips_number = trips_number;
    }
}

