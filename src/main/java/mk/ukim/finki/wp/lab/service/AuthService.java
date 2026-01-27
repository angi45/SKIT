package mk.ukim.finki.wp.lab.service;

import mk.ukim.finki.wp.lab.model.User;

public interface AuthService {
    User login(String username, String password);
}
