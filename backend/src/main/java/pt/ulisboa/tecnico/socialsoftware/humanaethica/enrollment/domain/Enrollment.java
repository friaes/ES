package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "enrollment")
public class Enrollment {
    //generates a unique id for each enrollment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //motivation for the enrollment
    private String motivation;
    //date and time of the enrollment
    private LocalDateTime enrollmentDate;


    //each enrollment is associated with a volunteer
    @ManyToOne
    private Volunteer volunteer;

    //each enrollment is associated with an activity
    @ManyToOne
    private Activity activity;

    public Enrollment() {
    }

    public Enrollment(Activity activity, Volunteer volunteer, EnrollmentDto enrollmentDto) {
        setVolunteer(volunteer);
        setActivity(activity);
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDate(DateHandler.toLocalDateTime(enrollmentDto.getEnrollmentDate()));
        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void verifyInvariants() {
        minimumMotivation();
        onlyOneEnrollmentPerActivity();
        enrollmentDateBeforeAcDate();
    }

    private void minimumMotivation() {
        if (motivation.length() < 10) {
            throw new HEException(MINIMUM_MOTIVATION_LENGTH);
        }
    }

    //um voluntário só pode estar inscrito uma vez numa atividade
    private void onlyOneEnrollmentPerActivity() {
        if (this.volunteer.getEnrollments()==null) {
            return;
        }
        for (Enrollment enrollment : this.volunteer.getEnrollments()) {
            if (enrollment.getActivity().equals(this.activity)) {
                throw new HEException(VOLUNTERR_ALREADY_ENROLLED_IN_ACTIVITY);
            }
        }
    }

    private void enrollmentDateBeforeAcDate() {
        if (this.enrollmentDate.isAfter(this.activity.getApplicationDeadline())) {
            throw new HEException(ENROLLMENT_DATE_AFTER_ACTIVITY_DATE);
        }
    }
}
