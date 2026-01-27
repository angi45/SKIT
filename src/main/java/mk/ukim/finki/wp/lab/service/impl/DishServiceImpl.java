package mk.ukim.finki.wp.lab.service.impl;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import mk.ukim.finki.wp.lab.repository.jpa.DishRepository;
import mk.ukim.finki.wp.lab.service.DishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    private final DishRepository dishRepository;
    private final ChefRepository chefRepository;

    public DishServiceImpl(DishRepository dishRepository, ChefRepository chefRepository) {
        this.dishRepository = dishRepository;
        this.chefRepository = chefRepository;
    }

    @Override
    public List<Dish> listDishes() {
        return this.dishRepository.findAll();
    }

    @Override
    public Dish findByDishId(String dishId) {
        return this.dishRepository.findByDishId(dishId);
    }

    @Override
    public Dish findById(Long id) {
        return this.dishRepository.findById(id).orElse(null);
    }

    @Override
    public Dish create(String dishId, String name, Cuisine cuisine, int preparationTime, List<Long> chefsId) {
        if (    dishId == null || dishId.isEmpty() ||
                name == null || name.isEmpty() ||
                cuisine == null  ||
                preparationTime < 0)
            throw new IllegalArgumentException();


        List<Chef> chefs = chefRepository.findAllById(chefsId);
        Dish dish = new Dish(dishId, name, cuisine, preparationTime);
        dish.setChefs(chefs);
        for (Chef chef: chefs)
        {
            if(!chef.getDishes().contains(dish))
            {
                chef.getDishes().add(dish);
            }
        }
        return dishRepository.save(dish);
    }

    @Override
    public Dish update(Long id, String dishId, String name, Cuisine cuisine, int preparationTime, List<Long> chefsId) {
        if (    dishId == null || dishId.isEmpty() ||
                name == null || name.isEmpty() ||
                cuisine == null ||
                preparationTime < 0)
            throw new IllegalArgumentException();

        Dish dish = dishRepository.findById(id).orElse(null);

        List<Chef> chefs = chefRepository.findAllById(chefsId);

        dish.setDishId(dishId);
        dish.setName(name);
        dish.setCuisine(cuisine);
        dish.setPreparationTime(preparationTime);
        dish.setChefs(chefs);
        for (Chef chef: chefs)
        {
            if(!chef.getDishes().contains(dish))
            {
                chef.getDishes().add(dish);
            }
        }
        return dishRepository.save(dish);
    }

    @Override
    public void delete(Long id) {
        dishRepository.deleteById(id);
    }

    @Override
    public List<Dish> listDishesByChef(Long chefId) {
        return dishRepository.findAllByChefs_Id(chefId);
    }
}