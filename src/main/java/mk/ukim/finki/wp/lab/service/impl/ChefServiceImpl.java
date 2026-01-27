package mk.ukim.finki.wp.lab.service.impl;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import mk.ukim.finki.wp.lab.repository.jpa.DishRepository;
import mk.ukim.finki.wp.lab.service.ChefService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChefServiceImpl implements ChefService {
    private final ChefRepository chefRepository;
    private final DishRepository dishRepository;

    public ChefServiceImpl(ChefRepository chefRepository, DishRepository dishRepository) {
        this.chefRepository = chefRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    public List<Chef> listChefs() {
        return this.chefRepository.findAll();
    }

    @Override
    public Chef findById(Long id) {
        return this.chefRepository.findById(id).orElse(null);
    }

    @Override
    public Chef create(String firstName, String lastName, String bio,Gender gender) {
        if (    firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                bio == null || bio.isEmpty() ||
                gender == null)
            throw new IllegalArgumentException();

        Chef chef = new Chef(firstName, lastName, bio, gender);
        return chefRepository.save(chef);
    }

    @Override
    public Chef update(Long id, String firstName, String lastName,String bio, Gender gender) {
        if (    firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                bio == null || bio.isEmpty() ||
                gender == null)
            throw new IllegalArgumentException();


        Chef chef = chefRepository.findById(id).orElse(null);
        chef.setFirstName(firstName);
        chef.setLastName(lastName);
        chef.setBio(bio);
        chef.setGender(gender);
        return chefRepository.save(chef);
    }

    @Override
    public void delete(Long id) {
        Chef chef = chefRepository.findById(id).orElse(null);
        for (Dish d : chef.getDishes()) {
            d.getChefs().remove(chef);
        }
        chef.getDishes().clear();
        chefRepository.deleteById(id);
    }
}