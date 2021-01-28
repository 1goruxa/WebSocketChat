package main.repo;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

    @Transactional
    @Modifying
    @Query(value="update users set online = 0", nativeQuery = true)
    void dropAllUsers();

    Optional<User> findOneByName(String name);

    @Query(value="select name from users where online=1", nativeQuery = true)
    ArrayList<String> findAllOnline();


}
