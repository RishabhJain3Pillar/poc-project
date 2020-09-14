package mujina.idp.service;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    User getUserByUsername(String username);

    List<User> getAllAdmins();
}
