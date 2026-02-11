package mk.ukim.finki.wp.lab.service;

import mk.ukim.finki.wp.lab.model.User;
import mk.ukim.finki.wp.lab.model.enums.Role;
import mk.ukim.finki.wp.lab.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.wp.lab.model.exceptions.UsernameAlreadyExistsException;
import mk.ukim.finki.wp.lab.repository.jpa.UserRepository;
import mk.ukim.finki.wp.lab.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit тестови за UserServiceImpl со користење на Mockito.
 *
 * Се тестира:
 *  - успешно регистрирање на корисник со валидни податоци
 *  - фрлање исклучок кога лозинките не се совпаѓаат
 *  - фрлање исклучок кога корисникот веќе постои
 *
 * Репозиториумот и password encoder-от се mock-ираат за изолирано тестирање.
 */
//unit testovi = testiranje samo edna klasa ili metod kade sto testirame service metodi i isklcoci
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    /**
     * Иницијализација на mock објекти и сервис пред секој тест.
     */
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    /**
     * Тест за успешна регистрација:
     *  - username не постои во базата
     *  - password се енкодира
     *  - корисникот се зачувува
     */
    @Test
    void registerSuccess() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register("test", "pass", "pass", "Name", "Surname", Role.ROLE_USER);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test");
        verify(userRepository).save(any());
    }

    /**
     * Тест за неуспешна регистрација поради несовпаѓање на лозинки.
     */
    @Test
    void registerPasswordMismatch() {
        assertThatThrownBy(() ->
                userService.register("test", "pass", "wrong", "Name", "Surname", Role.ROLE_USER)
        ).isInstanceOf(PasswordsDoNotMatchException.class);
    }

    /**
     * Тест за неуспешна регистрација кога username веќе постои.
     */
    @Test
    void registerUsernameExists() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() ->
                userService.register("test", "pass", "pass", "Name", "Surname", Role.ROLE_USER)
        ).isInstanceOf(UsernameAlreadyExistsException.class);
    }
}
