package com.application.ems.repo;

import com.application.ems.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);

    //we can load a certain ammount of users in the futuer using pagable latter.
}
