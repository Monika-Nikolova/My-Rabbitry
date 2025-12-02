package bg.softuni.myrabbitry.user;

import bg.softuni.myrabbitry.exception.UserAlreadyExistsException;
import bg.softuni.myrabbitry.exception.UserNotFoundException;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.user.repository.UserRepository;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.EditProfileRequest;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.BDDAssertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private UserService userService;

    @Test
    void whenChangeRole_andRepositoryReturnsUser_thenRoleIsChangedToAdmin_andUserPersistedInTheDatabase() {

        UUID id = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.USER)
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.changeRole(id);

        assertEquals(UserRole.ADMIN, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenChangeRole_andRepositoryReturnsAdmin_thenRoleIsChangedToUser_andUserPersistedInTheDatabase() {

        UUID id = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.changeRole(id);

        assertEquals(UserRole.USER, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenChangeRole_andRepositoryReturnsEmptyOptional_thenExceptionIsThrown() {

        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.changeRole(id));
    }

    @Test
    void whenChangeStatus_andRepositoryReturnsActive_thenStatusIsChangedToInactive_andUserPersistedInTheDatabase() {

        UUID id = UUID.randomUUID();
        User user = User.builder()
                .isActive(true)
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.changeStatus(id);

        assertFalse(user.isActive());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenChangeStatus_andRepositoryReturnsInactive_thenStatusIsChangedToActive_andUserPersistedInTheDatabase() {

        UUID id = UUID.randomUUID();
        User user = User.builder()
                .isActive(false)
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.changeStatus(id);

        assertTrue(user.isActive());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenEditProfile_thenPersistEditedUser() {

        UUID id = UUID.randomUUID();
        User user = User.builder()
                .firstName("Simba")
                .lastName("Lion")
                .birthDate(LocalDate.of(1994, 6, 15))
                .profilePicture("https://upload.wikimedia.org/wikipedia/en/thumb/2/2e/Simba%28TheLionKing%29.png/250px-Simba%28TheLionKing%29.png")
                .email("simba@gmail.com")
                .country("Kenya")
                .build();

        EditProfileRequest editProfileRequest = EditProfileRequest.builder()
                .firstName("Baloo")
                .lastName("Bear")
                .birthDate(LocalDate.of(1967, 10, 18))
                .profilePicture("https://static.wikitide.net/poohsadventureswiki/thumb/5/59/Clipjungle4new.webp/346px-Clipjungle4new.webp.png")
                .email("baloo@gmail.com")
                .country("India")
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.editProfile(id, editProfileRequest);

        assertEquals(editProfileRequest.getFirstName(), user.getFirstName());
        assertEquals(editProfileRequest.getLastName(), user.getLastName());
        assertEquals(editProfileRequest.getBirthDate(), user.getBirthDate());
        assertEquals(editProfileRequest.getProfilePicture(), user.getProfilePicture());
        assertEquals(editProfileRequest.getEmail(), user.getEmail());
        assertEquals(editProfileRequest.getCountry(), user.getCountry());
        verify(userRepository).save(user);
    }

    @Test
    void whenRegister_andUserDoesNotExist_createNewUser_andPersistInDatabase() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Monika123")
                .password("123456")
                .email("")
                .build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(subscriptionService.creatDefaultSubscription(any())).thenReturn(new Subscription());
        when(userRepository.save(any())).thenReturn(new User());

        userService.register(registerRequest);

        verify(userRepository).save(any());
        verify(subscriptionService).creatDefaultSubscription(any());
    }

    @Test
    void whenRegister_andUserAlreadyExists_throwException() {

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(User.builder().build()));


        assertThrows(UserAlreadyExistsException.class, () -> userService.register(new RegisterRequest()));
    }
}
