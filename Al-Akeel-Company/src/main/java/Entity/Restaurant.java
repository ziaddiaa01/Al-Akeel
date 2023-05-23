package Entity;


import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
public class Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    public String restaurant_name;

    private long owner_Id;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Meal> mealsLists;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public long getOwner_Id() {
        return owner_Id;
    }

    public void setOwner_Id(long owner_Id) {
        this.owner_Id = owner_Id;
    }

    public Collection<Meal> getMealsLists() {
        return mealsLists;
    }

    public void setMealsLists(Collection<Meal> mealsLists) {
        this.mealsLists = mealsLists;
    }

    public void addNewMeal (Meal new_meal) { this.mealsLists.add(new_meal); }
}
