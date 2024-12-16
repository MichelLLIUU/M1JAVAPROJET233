package services;

import dao.UserDAO;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean validateUser(String username, String password) {
        return userDAO.validateUser(username, password);
    }
}
