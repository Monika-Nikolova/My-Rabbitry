package bg.softuni.myrabbitry.rabbit.model;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rabbits")
public class Rabbit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String photoUrl;

    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Rabbit mother;

    @ManyToOne(fetch = FetchType.LAZY)
    private Rabbit father;

    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(nullable = false)
    private String colour;

    @Column(nullable = false)
    private String pattern;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EyeColour eyeColour;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EarShape earShape;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CoatLength coatLength;

    private String breed;

    private LocalDate vaccinatedOn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;
}
