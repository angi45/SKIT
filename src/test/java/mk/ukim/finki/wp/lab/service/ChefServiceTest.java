package mk.ukim.finki.wp.lab.service;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import mk.ukim.finki.wp.lab.repository.jpa.DishRepository;
import mk.ukim.finki.wp.lab.service.impl.ChefServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit тестови за ChefServiceImpl со користење на Mockito.
 *
 * Се тестира delete логиката:
 *  - Chef се пронаоѓа преку repository
 *  - Chef се отстранува од сите Dish (ManyToMany unlink)
 *  - листата dishes кај Chef се чисти
 *  - Chef се брише преку ChefRepository
 *
 * Тестот е целосно усогласен со имплементацијата на ChefServiceImpl.
 */
class ChefServiceTest {

    @Mock
    private ChefRepository chefRepository;

    @Mock
    private DishRepository dishRepository;

    private ChefServiceImpl chefService;

    private Chef chef;
    private Dish dish;

    /**
     * Подготовка на mock dependencies и тест податоци.
     */
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        chefService = new ChefServiceImpl(chefRepository, dishRepository);

        // Креирање Chef
        chef = new Chef("Test", "Chef", "Bio", new ArrayList<>(), Gender.MALE);
        chef.setId(1L);

        // Креирање Dish и поврзување со Chef (ManyToMany)
        dish = new Dish("D1", "Name", Cuisine.ITALIAN, 10, new ArrayList<>(List.of(chef)));
        dish.setId(10L);

        chef.getDishes().add(dish);
    }

    /**
     * Тест за delete методот:
     *
     * 1. Chef се пронаоѓа преку repository
     * 2. Chef се отстранува од сите негови Dish
     * 3. Листата dishes кај Chef се празни
     * 4. Chef се брише преку ChefRepository
     */
    @Test
    void testDeleteChefUnlinksDishes() {

        when(chefRepository.findById(1L))
                .thenReturn(Optional.of(chef));

        chefService.delete(1L);

        // Проверка дека Chef е отстранет од Dish
        assertThat(dish.getChefs()).doesNotContain(chef);

        // Проверка дека листата dishes кај Chef е празна
        assertThat(chef.getDishes()).isEmpty();

        // Проверка дека Chef е избришан
        verify(chefRepository).deleteById(1L);

        // DishRepository НЕ се повикува во delete логиката
        verifyNoInteractions(dishRepository);
    }
}
