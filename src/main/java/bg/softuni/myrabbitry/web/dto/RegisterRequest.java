package bg.softuni.myrabbitry.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;

    private String lastName;

    @NotBlank
    @Size(min = 3, max = 26, message = "username must be between 3 and 26 symbols.")
    private String username;

    @NotBlank
    @Size(min = 6, max = 10, message = "Password must be between 6 and 10 symbols.")
    private String password;

    @Email
    private String email;
}
