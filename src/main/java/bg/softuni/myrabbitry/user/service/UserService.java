package bg.softuni.myrabbitry.user.service;

import bg.softuni.myrabbitry.event.ChangedSubscriptionEvent;
import bg.softuni.myrabbitry.exception.UserAlreadyExistsException;
import bg.softuni.myrabbitry.exception.UserNotFoundException;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.user.repository.UserRepository;
import bg.softuni.myrabbitry.web.dto.EditProfileRequest;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final String PREGNANCY_DETAILS_PERMISSION = "view_pregnancy_details";
    private final String CREATE_PREGNANCY_DETAILS_PERMISSION = "create_pregnancy_details";
    private final String EDIT_PREGNANCY_DETAILS_PERMISSION = "edit_pregnancy_details";
    private final String MY_RABBITS_PERMISSION = "view_my_rabbits";
    private final String CREATE_RABBIT_PERMISSION = "create_rabbits";
    private final String EDIT_RABBIT_PERMISSION = "edit_rabbits";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with username %s already exists", registerRequest.getUsername()));
        }

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail().isBlank() ? null : registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .permissions(List.of(PREGNANCY_DETAILS_PERMISSION, MY_RABBITS_PERMISSION, CREATE_PREGNANCY_DETAILS_PERMISSION, EDIT_PREGNANCY_DETAILS_PERMISSION, CREATE_RABBIT_PERMISSION, EDIT_RABBIT_PERMISSION))
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        if (user.getUsername().equals("Admin123")) {
            user.setRole(UserRole.ADMIN);
        }

        user = userRepository.save(user);

        Subscription subscription = subscriptionService.creatDefaultSubscription(user);

        user.setSubscriptions(List.of(subscription));

        log.info("New user has been created with username [{}]", user.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(String.format("User with username %s not found", username)));

        return new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.getPermissions(), user.isActive());
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", id)));
    }

    @Cacheable("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void changeStatus(UUID id) {

        User user = getById(id);

        user.setActive(!user.isActive());

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void changeRole(UUID id) {
        User user = getById(id);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void editProfile(UUID id, EditProfileRequest editProfileRequest) {
        User user = getById(id);

        user.setFirstName(editProfileRequest.getFirstName());
        user.setLastName(editProfileRequest.getLastName());
        user.setBirthDate(editProfileRequest.getBirthDate());
        user.setProfilePicture(editProfileRequest.getProfilePicture());
        user.setEmail(editProfileRequest.getEmail());
        user.setCountry(editProfileRequest.getCountry());
        user.setUpdatedOn(LocalDateTime.now());

        userRepository.save(user);
    }

    @EventListener
    public void editPermissionsOnChangedSubscription(ChangedSubscriptionEvent changedSubscriptionEvent) {
        User user = changedSubscriptionEvent.getUser();
        user.setPermissions(changedSubscriptionEvent.getPermissions());
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);

        refreshUserAuthentication(user.getUsername());
    }

    private void refreshUserAuthentication(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getName().equals(username)) {
            UserDetails userDetails = loadUserByUsername(username);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    auth.getCredentials(),
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
