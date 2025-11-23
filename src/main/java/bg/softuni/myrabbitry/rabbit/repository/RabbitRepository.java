package bg.softuni.myrabbitry.rabbit.repository;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RabbitRepository extends JpaRepository<Rabbit, UUID> {

    List<Rabbit> getByOwnerOrderByCreatedOnDesc(User owner);

    Optional<Rabbit> findByCodeAndOwner(String code, User byId);
}
