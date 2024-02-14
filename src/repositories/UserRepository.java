package repositories;

import entities.User;

import java.util.*;

public class UserRepository implements IUserRepository{
    private Long autoIncreament = 1L;
    private final Map<Long, User> userMap;

    public UserRepository() {
        userMap = new HashMap<Long, User>();
    }

    @Override
    public User save(User user) {
        User savedUser = new User(user.getName(), autoIncreament);
        userMap.put(autoIncreament, savedUser);
        autoIncreament++;
        return savedUser;
    }

    @Override
    public List<User> findAll() {

        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> findById(Long id) {

        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByName(String name) {

        return userMap.values().stream().filter(u -> u.getName().equals(name)).findFirst();
    }
}
