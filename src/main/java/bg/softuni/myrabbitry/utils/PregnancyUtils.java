package bg.softuni.myrabbitry.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class PregnancyUtils {

    public Double calculateDeathPercentage(Integer bornKids, Integer weanedKids) {
        if (bornKids != null && weanedKids != null) {
            return ((bornKids - weanedKids) * 1.0 / bornKids) * 100;
        }
        return null;
    }

    public LocalDate calculateEarliestDueDate(LocalDate dayOfFertilization) {
        return dayOfFertilization != null ? dayOfFertilization.plusDays(28) : null;
    }

    public LocalDate calculateLatestDueDate(LocalDate dayOfFertilization) {
        return dayOfFertilization != null ? dayOfFertilization.plusDays(33) : null;
    }

    public static Double calculateLactatingQuantity(Double totalWeightKidsDay1, Double totalWeightKidsDay20) {
        return totalWeightKidsDay1 != null && totalWeightKidsDay20 != null ? (totalWeightKidsDay20 - totalWeightKidsDay1) * 2 : null;
    }
}
