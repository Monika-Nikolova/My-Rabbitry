package bg.softuni.myrabbitry.Event;

import bg.softuni.myrabbitry.user.model.User;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangedSubscriptionEvent {

    private List<String> permissions;

    private User user;
}
