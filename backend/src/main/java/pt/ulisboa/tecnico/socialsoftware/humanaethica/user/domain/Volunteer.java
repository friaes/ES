package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {
        //CHECK IN CLASS
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.EAGER)
        private List<Participation> participations = new ArrayList<>();
        public Volunteer() {
        }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }


    public List<Participation> getParticipations() {
        return this.participations;
    }

    public void addParticipation(Participation participation) {
        this.participations.add(participation);
    }

}
