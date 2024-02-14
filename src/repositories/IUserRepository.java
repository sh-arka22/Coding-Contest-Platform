package repositories;

import entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    User save(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByName(String name);
}
