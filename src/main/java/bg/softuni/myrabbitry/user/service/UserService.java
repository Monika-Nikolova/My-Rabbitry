package bg.softuni.myrabbitry.user.service;

import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.user.repository.UserRepository;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()) {
            throw new RuntimeException(String.format("User with username %s already exists", registerRequest.getUsername()));
        }

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail().isBlank() ? null : registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        Subscription subscription = subscriptionService.creatDefaultSubscription(user);

        user.setSubscriptions(List.of(subscription));

        log.info(String.format("New user has been created with username [%s]", user.getUsername()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(String.format("User with username %s not found", username)));

        return  new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("User with id %s not found", id)));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void changeStatus(UUID id) {

        User user = getById(id);

        user.setActive(!user.isActive());

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

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
}
