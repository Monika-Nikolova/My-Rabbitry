package bg.softuni.myrabbitry.pregnancy.model;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JoinColumn(nullable = false)
    private Rabbit mother;

    @ManyToOne
    private Rabbit father;

    private LocalDate dayOfFertilization;

    private LocalDate earliestDueDate; //28-th day

    private LocalDate latestDueDate; //33-rd day

    private LocalDate dateOfBirth;

    private Integer countBornKids;

    private Integer countWeanedKids;

    private Double deathPercentage;

    private Double totalWeightKidsDay1;

    private Double totalWeightKidsDay20;

    @Column(nullable = false)
    private boolean isFalsePregnancy;

    @Column(nullable = false)
    private boolean isCannibalismPresent;

    @Column(nullable = false)
    private boolean hasAbort;

    @Column(nullable = false)
    private boolean wasPregnant;

    private Double lactatingQuantity;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;
}