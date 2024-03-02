package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import org.apache.tools.ant.taskdefs.Local;
import org.checkerframework.checker.units.qual.min;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

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

    public Enrollment(EnrollmentDto enrollmentDto, Volunteer volunteer, Activity activity) {
        setVolunteer(volunteer);
        setActivity(activity);
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDate(LocalDateTime.now());
        verifyInvariants();
    }

    public void update(EnrollmentDto enrollmentDto, Volunteer volunteer, Activity activity) {
        setVolunteer(volunteer);
        setActivity(activity);
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDate(LocalDateTime.now());
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
            throw new IllegalArgumentException("Motivation cannot be empty");
        }
    }

    private void onlyOneEnrollmentPerActivity() {
        if (activity.getEnrollments().contains(this)) {
            throw new IllegalArgumentException("Volunteer already enrolled in this activity");
        }
    }

    private void enrollmentDateBeforeAcDate() {
        if (enrollmentDate.isAfter(activity.getEndingDate())) {
            throw new IllegalArgumentException("Enrollment date must be before activity starting date");
        }
    }
}
