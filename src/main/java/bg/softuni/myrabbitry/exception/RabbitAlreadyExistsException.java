package bg.softuni.myrabbitry.exception;

public class RabbitAlreadyExistsException extends RuntimeException {

    public RabbitAlreadyExistsException(String message) {
        super(message);
    }
}
