package com.fooddelivery.repository;

import com.fooddelivery.model.User;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.Optional;

public class UserRepository extends FileRepository<User>
        implements com.fooddelivery.domain.repository.UserRepository {

    private static UserRepository instance;

    private UserRepository() {
        super("data/users.json", new TypeToken<Map<String, User>>() {}.getType());
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findWhere(user ->
                user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
                .stream()
                .findFirst();
    }
}