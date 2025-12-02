package bg.softuni.myrabbitry.utils;

import bg.softuni.myrabbitry.subscription.model.Subscription;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SubscriptionUtils {

    public static Object getNotDeletedSubscriptions(List<Subscription> subscriptions) {
        return subscriptions.stream().filter(subscription -> !subscription.isDeleted()).collect(Collectors.toList());
    }
}
