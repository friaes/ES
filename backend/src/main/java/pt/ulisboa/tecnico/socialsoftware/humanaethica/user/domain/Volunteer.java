package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER) 
public class Volunteer extends User {
    //Add the relationship between the volunteer and the activities
    @oneToMany
    private List<Activity> activities = new ArrayList<>();
    
    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }
    //Add the getter for the activities
    public List<Activity> getActivities() {
        return activities;
    }
    //Add setter for the activities
    public void addActivity(List<Activity> activities) {
        this.activities = activities;
    }
}
