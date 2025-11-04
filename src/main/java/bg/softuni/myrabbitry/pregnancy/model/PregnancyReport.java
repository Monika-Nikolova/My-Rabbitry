package bg.softuni.myrabbitry.pregnancy.model;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pregnancy_reports")
public class PregnancyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Rabbit mother;

    @ManyToOne
    @JoinColumn(name = "id")
    private Rabbit father;

    private LocalDate dayOfFertilization;

    private LocalDate earliestDueDate; //28-th day

    private LocalDate LatestDueDate; //33-rd day

    private LocalDate dateOfBirth;

    private Integer countBornKids;

    private Integer countWeanedKids;

    private Double totalWeightKidsDay1;

    private Double totalWeightKidsDay20;

    @Column(nullable = false, name = "is_false_pregnancy")
    private boolean isFalsePregnancy;

    @Column(nullable = false, name = "is_cannibalism_present")
    private boolean isCannibalismPresent;

    @Column(nullable = false, name = "has_abort")
    private boolean hasAbort;

    @Column(nullable = false, name = "was_pregnant")
    private boolean wasPregnant;

    private Double lactatingQuantity;
}
