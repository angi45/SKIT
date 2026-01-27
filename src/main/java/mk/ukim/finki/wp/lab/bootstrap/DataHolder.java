package mk.ukim.finki.wp.lab.bootstrap;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.User;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.model.enums.Role;
import mk.ukim.finki.wp.lab.repository.jpa.UserRepository;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import mk.ukim.finki.wp.lab.repository.jpa.DishRepository;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Component
public class DataHolder {

    private final ChefRepository chefRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataHolder(ChefRepository chefRepository, DishRepository dishRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.chefRepository = chefRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        Chef chef1 = new Chef("Gordon","Ramsay",
                "World-renowned chef known for his fiery personality.", new ArrayList<>(),Gender.MALE);

        Chef chef2 = new Chef("Massimo","Bottura",
                "Italian chef famous for modern Italian cuisine.", new ArrayList<>(), Gender.MALE);

        Chef chef3 = new Chef("Alice","Waters",
                "Pioneer of the farm-to-table movement in the USA.",new ArrayList<>(),Gender.FEMALE);

        Chef chef4 = new Chef("Heston","Blumenthal",
                "Chef known for his scientific approach to cooking.", new ArrayList<>(),Gender.MALE);

        Chef chef5 = new Chef("Dominique","Crenn",
                "Chef celebrated for artistic and creative cuisine.", new ArrayList<>(),Gender.MALE);

        chefRepository.save(chef1);
        chefRepository.save(chef2);
        chefRepository.save(chef3);
        chefRepository.save(chef4);
        chefRepository.save(chef5);

        Dish dish1 = new Dish("D01", "Scrambled Eggs", Cuisine.BRITISH, 10, new ArrayList<>());
        Dish dish2 = new Dish("D02", "Tiramisu", Cuisine.ITALIAN, 30, new ArrayList<>());
        Dish dish3 = new Dish("D03", "Seasonal Salad", Cuisine.AMERICAN, 20, new ArrayList<>());
        Dish dish4 = new Dish("D04", "Lobster Ravioli", Cuisine.FRENCH, 80, new ArrayList<>());
        Dish dish5 = new Dish("D05", "Tagliatelle al Ragu", Cuisine.ITALIAN, 60, new ArrayList<>());

        dishRepository.save(dish1);
        dishRepository.save(dish2);
        dishRepository.save(dish3);
        dishRepository.save(dish4);
        dishRepository.save(dish5);


        User admin = new User("admin",passwordEncoder.encode("admin"),"Admin","Admin", Role.ROLE_ADMIN);
        User user = new User("user",passwordEncoder.encode("user"),"User","User",Role.ROLE_USER);

        userRepository.save(admin);
        userRepository.save(user);
    }
}