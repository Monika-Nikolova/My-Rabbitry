package bg.softuni.myrabbitry.rabbit.model;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(name = "photo_url")
    private String photoUrl;

    private String name;

    @Column(nullable = false, name = "custom_id")
    private String code;

    private String description;

    @Column(name = "mother_id")
    private String motherId;

    @Column(name = "father_id")
    private String fatherId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(nullable = false)
    private String colour;

    @Column(nullable = false)
    private String pattern;

    @Column(nullable = false, name = "eye_colour")
    @Enumerated(EnumType.STRING)
    private EyeColour eyeColour;

    @Column(nullable = false, name = "ear_shape")
    @Enumerated(EnumType.STRING)
    private EarShape earShape;

    @Column(nullable = false, name = "coat_length")
    @Enumerated(EnumType.STRING)
    private CoatLength coatLength;

    private String breed;

    @Column(name = "vaccinate_on")
    private LocalDate vaccinatedOn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User owner;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "mother")
    private List<PregnancyReport> pregnancyReports = new ArrayList<>();
}
