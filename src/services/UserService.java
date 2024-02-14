package services;

import constants.UserOrder;
import entities.User;
import repositories.IUserRepository;

import java.util.List;

public class UserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //feature 1
    public User createUser(String name){
        User newUser = new User(name);
        User createdUser = userRepository.save(newUser);
        return createdUser;
    }

    //get the leaderBoard // get all users on the basis of scores
    public List<User> getUsers(UserOrder userOrder) {
        List<User> users = userRepository.findAll();
        if (userOrder == UserOrder.SCORE_ASC) {
            users.sort((u1, u2) -> u1.getTotalScore() - u2.getTotalScore());
        } else if (userOrder == UserOrder.SCORE_DESC) {
            users.sort((u1, u2) -> u2.getTotalScore() - u1.getTotalScore());
        }
        return users;
    }
}
