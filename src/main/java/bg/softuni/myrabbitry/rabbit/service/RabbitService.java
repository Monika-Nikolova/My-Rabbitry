package bg.softuni.myrabbitry.rabbit.service;

import bg.softuni.myrabbitry.rabbit.repository.RabbitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    private final RabbitRepository rabbitRepository;

    @Autowired
    public RabbitService(RabbitRepository rabbitRepository) {
        this.rabbitRepository = rabbitRepository;
    }
}
