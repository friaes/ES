package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.PARTICIPATION_BEFORE_APPLICATION_END;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.PARTICIPATION_DUPLICATE;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.PARTICIPATION_EXCEEDS_ACTIVITY_LIMIT;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;


@Entity
@Table(name = "Participation")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer rating;

    private LocalDateTime acceptanceDate;

    @ManyToOne
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
        activity.addParticipation(this);
        volunteer.addParticipation(this);
    }

    public void update(ParticipationDto participationDto) {
        setRating(participationDto.getRating());

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
    }

    public Volunteer getVolunteer() {
        return this.volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
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
        participationsLimitReached();
        duplicatedParticipation();
        acceptanceBeforeApplication();
    }

    private void participationsLimitReached() {
        if (activity.getParticipations().size() + 1 > activity.getParticipantsNumberLimit()) {
            throw new HEException(PARTICIPATION_EXCEEDS_ACTIVITY_LIMIT);
        }
    }

    private void duplicatedParticipation() {
        if (volunteer.getParticipations().stream().anyMatch(e -> e.getActivity().equals(activity))) {
            throw new HEException(PARTICIPATION_DUPLICATE);
        }
    }

    private void acceptanceBeforeApplication() {
        if (!acceptanceDate.isAfter(activity.getApplicationDeadline())) {
            throw new HEException(PARTICIPATION_BEFORE_APPLICATION_END);
        }
    }

}



