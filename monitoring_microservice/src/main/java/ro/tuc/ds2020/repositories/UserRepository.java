package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.tuc.ds2020.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findUserById(Long id);
}
