package mk.ukim.finki.wp.lab.service;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import mk.ukim.finki.wp.lab.repository.jpa.DishRepository;
import mk.ukim.finki.wp.lab.service.impl.DishServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit тестови за DishServiceImpl.
 *
 * Се користи Mockito за mock-ирање на Repository слојот.
 * Овие тестови НЕ користат база, туку ја тестираат бизнис логиката.
 */
//unit testovi = testiranje samo edna klasa ili metod kade sto testirame service metodi i isklcoci
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private ChefRepository chefRepository;

    private DishServiceImpl dishService;

    /**
     * Иницијализација на Mockito mocks и сервис инстанца.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        dishService = new DishServiceImpl(dishRepository, chefRepository);
    }

    /**
     * Тест за create метод со невалидни параметри.
     * Очекуваме IllegalArgumentException.
     */
    @Test
    void testCreateFailsInvalidData() {

        assertThatThrownBy(() ->
                dishService.create("", "", null, -1, List.of())
        ).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Тест за update метод со невалидни параметри.
     * Очекуваме IllegalArgumentException.
     */
    @Test
    void testUpdateFailsInvalidData() {

        assertThatThrownBy(() ->
                dishService.update(1L, "", "", null, -5, List.of())
        ).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Тест за успешно креирање на Dish.
     *
     * ВАЖНО:
     * Во unit тест мора рачно да се иницијализираат
     * collection полињата (Hibernate тука НЕ учествува).
     */
    @Test
    void testCreateSuccess() {

        // Креирање Chef со иницијализирана листа dishes
        Chef chef = new Chef("Chef", "Test", "Bio", Gender.MALE);
        chef.setId(5L);
        chef.setDishes(new ArrayList<>());

        // Mock однесување
        when(chefRepository.findAllById(List.of(5L)))
                .thenReturn(List.of(chef));

        when(dishRepository.save(any(Dish.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Повик на сервис метод
        Dish d = dishService.create("D1", "Dish", Cuisine.ITALIAN, 10, List.of(5L));

        // Проверки
        assertThat(d).isNotNull();
        assertThat(d.getDishId()).isEqualTo("D1");
        assertThat(d.getName()).isEqualTo("Dish");
        assertThat(d.getCuisine()).isEqualTo(Cuisine.ITALIAN);
        assertThat(d.getPreparationTime()).isEqualTo(10);
        assertThat(d.getChefs()).hasSize(1);

        // Проверка дека Dish е поврзан со Chef
        assertThat(chef.getDishes()).contains(d);
    }
}
