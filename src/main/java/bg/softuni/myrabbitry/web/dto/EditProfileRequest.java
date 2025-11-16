package bg.softuni.myrabbitry.web.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    @URL
    private String profilePicture;

    @Email
    private String email;

    private String country;
}
