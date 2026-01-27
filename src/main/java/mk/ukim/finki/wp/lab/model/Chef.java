package mk.ukim.finki.wp.lab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import mk.ukim.finki.wp.lab.model.enums.Gender;

import java.util.List;

@Data
@AllArgsConstructor
@Entity
public class Chef {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String bio;

    @ManyToMany(mappedBy = "chefs")
    private List<Dish> dishes;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public Chef() {}

    public Chef(String firstName, String lastName, String bio, List<Dish> dishes, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.dishes = dishes;
        this.gender = gender;
    }

    public Chef(String firstName, String lastName, String bio, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.gender = gender;
    }
}
