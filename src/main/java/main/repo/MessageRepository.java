package main.repo;

import main.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {

    @Query(value="SELECT COUNT(*) FROM messages", nativeQuery = true)
    int countAll();


    @Query(value="select * from messages where id=(select min(id) from messages)", nativeQuery = true)
    Message findOldest();
}
