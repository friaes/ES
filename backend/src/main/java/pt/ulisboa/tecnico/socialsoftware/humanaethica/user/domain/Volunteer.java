package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.List;
import java.util.ArrayList;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER) 
public class Volunteer extends User {

    //Add the relationship between the volunteer and the enrollments
    @OneToMany(mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Enrollment> enrollments = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Participation> participations = new ArrayList<>();
    
    @OneToMany(mappedBy = "volunteer", orphanRemoval = true)
    private List<Assessment> assessments;

    public Volunteer() {
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    //Add getter for the enrollments
    public List<Enrollment> getEnrollments() {
        return enrollments;
    }
    
    //Add method to add an enrollment to the list
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public List<Participation> getParticipations() {
        return this.participations;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
    }

}
