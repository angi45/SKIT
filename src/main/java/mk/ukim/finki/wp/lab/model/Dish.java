package mk.ukim.finki.wp.lab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;

import java.util.List;

@Data
@AllArgsConstructor
@Entity
public class Dish {
    @Id
    @GeneratedValue
    private Long id;
    private String dishId;
    private String name;
    @Enumerated(EnumType.STRING)
    private Cuisine cuisine;
    private int preparationTime;

    @ManyToMany
    private List<Chef> chefs;

    public Dish() {}

    public Dish(String dishId, String name, Cuisine cuisine, int preparationTime, List<Chef> chefs) {
        this.dishId = dishId;
        this.name = name;
        this.cuisine = cuisine;
        this.preparationTime = preparationTime;
        this.chefs = chefs;
    }

    public Dish(String dishId, String name, Cuisine cuisine, int preparationTime) {
        this.dishId = dishId;
        this.name = name;
        this.cuisine = cuisine;
        this.preparationTime = preparationTime;
    }
}
