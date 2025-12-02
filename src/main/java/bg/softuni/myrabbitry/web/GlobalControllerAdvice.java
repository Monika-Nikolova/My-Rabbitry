package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(RabbitAlreadyExistsException.class)
    public String handlePregnancyNotFoundException(RabbitAlreadyExistsException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/rabbits/new";
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExistsException(UserAlreadyExistsException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(DeleteSubscriptionNotAllowedException.class)
    public String handleDeleteSubscriptionNotAllowedException(DeleteSubscriptionNotAllowedException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/subscriptions/history";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RabbitWrongSexException.class)
    public String handleRabbitWrongSexException() {
        return "wrong-sex";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoResourceFoundException.class,
            PregnancyNotFoundException.class,
            RabbitNotFoundException.class,
            UserNotFoundException.class,
            SubscriptionNotFoundException.class})
    public String handleNotFoundException() {
        return "not-found";
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException() {
        return "access-denied";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleException() {

        return "general-error";
    }
}
