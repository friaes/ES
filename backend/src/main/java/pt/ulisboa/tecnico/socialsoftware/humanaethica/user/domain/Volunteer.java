package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {
    @OneToMany(mappedBy = "volunteer")
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
}
