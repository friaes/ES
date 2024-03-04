package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER) 
public class Volunteer extends User {

    //Add the relationship between the volunteer and the enrollments
    @OneToMany
    private List<Enrollment> enrollments = new ArrayList<>();
    
    public Volunteer() {
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
}
