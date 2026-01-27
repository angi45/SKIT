package mk.ukim.finki.wp.lab.service;

import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;

import java.util.List;

public interface DishService {
    List<Dish> listDishes();
    Dish findByDishId(String dishId);
    Dish findById(Long id);
    Dish create(String dishId, String name, Cuisine cuisine, int preparationTime, List<Long> chefsId);
    Dish update(Long id, String dishId, String name, Cuisine cuisine, int preparationTime, List<Long> chefsId);
    void delete(Long id);
    List<Dish> listDishesByChef(Long chefId);

}