package com.distributedclearance.server.managers;

import com.distributedclearance.database.dao.UserDAO;
import com.distributedclearance.models.User;

public class AuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationService() {
        userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        boolean authenticated =
                userDAO.authenticateUser(username, password);

        if (!authenticated) {
            return null;
        }

        return userDAO.findUserByUsername(
                username
        );
    }
}