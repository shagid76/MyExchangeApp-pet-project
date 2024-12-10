package us.yarik.ExchangeApp.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import us.yarik.ExchangeApp.model.User;
import us.yarik.ExchangeApp.repository.UserRepository;

import java.util.Optional;

@Service
public class RegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder,
                               UserRepository userRepository,
                               UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public void registerUser(User user) {
        Optional<User> existingUser = userService.findByName(user.getName());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

}
