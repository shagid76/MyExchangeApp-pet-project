package us.yarik.ExchangeApp.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.yarik.ExchangeApp.model.User;
import us.yarik.ExchangeApp.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public Optional<User> findByName(String name){
        return userRepository.findByName(name);
    }

}
