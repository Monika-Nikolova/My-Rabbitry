package bg.softuni.myrabbitry.exception;

public class RabbitNotFoundException extends RuntimeException {

    public RabbitNotFoundException(String message) {
        super(message);
    }
}
