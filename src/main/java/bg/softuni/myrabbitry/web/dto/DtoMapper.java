package bg.softuni.myrabbitry.web.dto;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.user.model.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DtoMapper {

    public static EditProfileRequest fromUser(User user) {

        return EditProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .profilePicture(user.getProfilePicture())
                .email(user.getEmail())
                .country(user.getCountry())
                .build();
    }

    public static RabbitRequest fromRabbit(Rabbit rabbit) {

        return RabbitRequest.builder()
                .photoUrl(rabbit.getPhotoUrl())
                .name(rabbit.getName())
                .code(rabbit.getCode())
                .description(rabbit.getDescription())
                .motherCode(rabbit.getMother() == null ? null : rabbit.getMother().getCode())
                .fatherCode(rabbit.getFather() == null ? null : rabbit.getFather().getCode())
                .birthDate(rabbit.getBirthDate())
                .sex(rabbit.getSex())
                .colour(rabbit.getColour())
                .pattern(rabbit.getPattern())
                .eyeColour(rabbit.getEyeColour())
                .earShape(rabbit.getEarShape())
                .coatLength(rabbit.getCoatLength())
                .breed(rabbit.getBreed())
                .vaccinatedOn(rabbit.getVaccinatedOn())
                .status(rabbit.getStatus())
                .build();
    }

    public static Rabbit fromRabbitRequest(RabbitRequest rabbitRequest, Rabbit mother,  Rabbit father, User owner) {

        return Rabbit.builder()
                .photoUrl(rabbitRequest.getPhotoUrl())
                .name(rabbitRequest.getName())
                .code(rabbitRequest.getCode())
                .description(rabbitRequest.getDescription())
                .mother(mother)
                .father(father)
                .birthDate(rabbitRequest.getBirthDate())
                .sex(rabbitRequest.getSex())
                .colour(rabbitRequest.getColour())
                .pattern(rabbitRequest.getPattern())
                .eyeColour(rabbitRequest.getEyeColour())
                .earShape(rabbitRequest.getEarShape())
                .coatLength(rabbitRequest.getCoatLength())
                .breed(rabbitRequest.getBreed())
                .vaccinatedOn(rabbitRequest.getVaccinatedOn())
                .status(rabbitRequest.getStatus())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .owner(owner)
                .build();
    }

    public static PregnancyRequest fromPregnancyReport(PregnancyReport pregnancyReport) {

        return PregnancyRequest.builder()
                .mother(pregnancyReport.getMother() == null ? null : pregnancyReport.getMother().getCode())
                .father(pregnancyReport.getFather() == null ? null : pregnancyReport.getFather().getCode())
                .dayOfFertilization(pregnancyReport.getDayOfFertilization())
                .dateOfBirth(pregnancyReport.getDateOfBirth())
                .countBornKids(pregnancyReport.getCountBornKids())
                .countWeanedKids(pregnancyReport.getCountWeanedKids())
                .totalWeightKidsDay1(pregnancyReport.getTotalWeightKidsDay1())
                .totalWeightKidsDay20(pregnancyReport.getTotalWeightKidsDay20())
                .isFalsePregnancy(pregnancyReport.isFalsePregnancy())
                .isCannibalismPresent(pregnancyReport.isCannibalismPresent())
                .hasAbort(pregnancyReport.isHasAbort())
                .wasPregnant(pregnancyReport.isWasPregnant())
                .build();
    }

    public static PaymentRequest fromSubscriptionRequestToPaymentRequest(SubscriptionType subscriptionType, SubscriptionRequest subscriptionRequest) {

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setSubscriptionType(subscriptionType);
        paymentRequest.setCardTier(subscriptionRequest.getCardTier());
        paymentRequest.setPeriod(subscriptionRequest.getPeriod());
        return paymentRequest;
    }
}
