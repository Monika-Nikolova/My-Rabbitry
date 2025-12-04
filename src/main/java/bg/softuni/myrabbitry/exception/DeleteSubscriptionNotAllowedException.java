package bg.softuni.myrabbitry.exception;

public class DeleteSubscriptionNotAllowedException extends RuntimeException {

    public DeleteSubscriptionNotAllowedException(String message) {
        super(message);
    }
}
