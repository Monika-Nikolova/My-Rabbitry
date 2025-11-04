package bg.softuni.myrabbitry.rabbit.repository;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RabbitRepository extends JpaRepository<Rabbit, UUID> {
}
