package mk.ukim.finki.wp.lab.web.controller;

import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.service.ChefService;
import mk.ukim.finki.wp.lab.service.DishService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;
    private final ChefService chefService;

    public DishController(DishService dishService, ChefService chefService) {
        this.dishService = dishService;
        this.chefService = chefService;
    }

    @GetMapping
    public String getDishesPage(@RequestParam(required = false) String error, Model model) {

        if (error != null) {
            model.addAttribute("error", error);
        }

        model.addAttribute("dishes", dishService.listDishes());
        model.addAttribute("bodyContent", "listDishes");
        return "master-template";
    }


    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(@PathVariable Long id) {
        dishService.delete(id);
        return "redirect:/dishes";
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveDish(@RequestParam String dishId,
                           @RequestParam String name,
                           @RequestParam Cuisine cuisine,
                           @RequestParam int preparationTime,
                           @RequestParam List<Long> chefsId) {
        dishService.create(dishId, name, cuisine, preparationTime, chefsId);
        return "redirect:/dishes";
    }


    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editDish(@PathVariable Long id,
                           @RequestParam String dishId,
                           @RequestParam String name,
                           @RequestParam Cuisine cuisine,
                           @RequestParam int preparationTime,
                           @RequestParam List<Long> chefsId) {
        dishService.update(id, dishId, name, cuisine, preparationTime, chefsId);
        return "redirect:/dishes";
    }


    @GetMapping("/dish-form/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getEditDishForm(@PathVariable Long id, Model model) {

        Dish dish = dishService.findById(id);
        if (dish == null) {
            return "redirect:/dishes?error=DishNotFound.";
        }

        model.addAttribute("dish", dish);
        model.addAttribute("chefs", chefService.listChefs());
        model.addAttribute("cuisines", Cuisine.values());

        return "dish-form";
    }


    @GetMapping("/dish-form")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAddDishPage(Model model) {
        model.addAttribute("chefs", chefService.listChefs());
        model.addAttribute("cuisines", Cuisine.values());
        return "dish-form";
    }
}
