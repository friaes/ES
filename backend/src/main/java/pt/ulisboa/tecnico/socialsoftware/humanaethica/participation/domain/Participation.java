package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import org.springframework.security.crypto.keygen.KeyGenerators;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;


@Entity
@Table(name = "participations")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer rating;

    @Column(name = "acceptance_date")
    private LocalDateTime acceptanceDate;

    @ManyToMany
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public Participation() {
    }

    public Participation(Activity activity, Volunteer volunteer, ParticipationDto participationDto) {
        setActivity(activity);
        setVolunteer(volunteer);
        setRating(participationDto.getRating());
        setAcceptanceDate(DateHandler.toLocalDateTime(participationDto.getAcceptanceDate()));
        verifyInvariants();
    }


    public Integer getId() { return id; }

    public Integer getRating() { return rating; }

    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getAcceptanceDate() {return acceptanceDate;}

    public void setAcceptanceDate(LocalDateTime acceptanceDate) { this.acceptanceDate = acceptanceDate; }

    public Activity getActivity() {return activity;}

    public void setActivity(Activity activity) {
        this.activity = activity;
        activity.addParticipation(this);
    }

    public Volunteer getVolunteer() {
        return this.volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addParticipation(this);
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", rating='" + getRating() + "'" +
            ", acceptanceDate='" + getAcceptanceDate() + "'" +
            ", activity='" + getActivity() + "'" +
            ", volunteer='" + getVolunteer() + "'" +
            "}";
    }

    public void verifyInvariants() {
        checkActivityLimit();
    }

    private void checkActivityLimit() {
        if (activity.getParticipations().length() + 1 > activity.getParticipationLimit()) {
            throw new IllegalArgumentException("Number of participations exceeded");
        }
    }

}



