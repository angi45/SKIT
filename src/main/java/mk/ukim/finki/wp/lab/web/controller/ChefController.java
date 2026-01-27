package mk.ukim.finki.wp.lab.web.controller;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.service.ChefService;
import mk.ukim.finki.wp.lab.service.DishService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/chefs")
public class ChefController {

    private final ChefService chefService;
    private final DishService dishService;

    public ChefController(ChefService chefService, DishService dishService) {
        this.chefService = chefService;
        this.dishService = dishService;
    }

    @GetMapping
    public String getChefsPage(@RequestParam(required = false) String error, Model model) {

        if (error != null)
            model.addAttribute("error", error);
        model.addAttribute("chefs", chefService.listChefs());
        model.addAttribute("bodyContent", "chefsList");
        return "master-template";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        chefService.delete(id);
        return "redirect:/chefs";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveChef(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String bio,
                           @RequestParam Gender gender) {
        chefService.create(firstName, lastName, bio, gender);
        return "redirect:/chefs";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editChef(@PathVariable Long id,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String bio,
                           @RequestParam Gender gender){
        chefService.update(id, firstName, lastName, bio, gender);
        return "redirect:/chefs";
    }

    @GetMapping("/chef-form/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getEditChefForm(@PathVariable Long id, Model model)
    {
        Chef chef = chefService.findById(id);
        if(chef == null)
            return "redirect:/chefs?error=ChefNotFound.";
        model.addAttribute("chef", chef);
        model.addAttribute("genders", Gender.values());

        return "chef-form";
    }

    @GetMapping("/chef-form")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAddChefPage(Model model)
    {
        model.addAttribute("genders", Gender.values());
        return "chef-form";
    }

    @GetMapping("/dishes/{id}")
    public String getDishesByChef(@PathVariable Long id, Model model) {
        Chef chef = chefService.findById(id);
        List<Dish> dishes = dishService.listDishesByChef(id);

        model.addAttribute("chef", chef);
        model.addAttribute("dishes", dishes);
        model.addAttribute("bodyContent", "dishesByChef");
        return "master-template";
    }
}
